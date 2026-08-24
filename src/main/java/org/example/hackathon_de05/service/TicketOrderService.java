package org.example.hackathon_de05.service;

import lombok.RequiredArgsConstructor;
import org.example.hackathon_de05.model.constant.TicketOrderStatus;
import org.example.hackathon_de05.model.entity.BusTrip;
import org.example.hackathon_de05.model.entity.Passenger;
import org.example.hackathon_de05.model.entity.TicketItem;
import org.example.hackathon_de05.model.entity.TicketOrder;
import org.example.hackathon_de05.repository.BusTripRepository;
import org.example.hackathon_de05.repository.PassengerRepository;
import org.example.hackathon_de05.repository.TicketItemRepository;
import org.example.hackathon_de05.repository.TicketOrderRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TicketOrderService {

    private final BusTripRepository busTripRepository;
    private final PassengerRepository passengerRepository;
    private final TicketOrderRepository ticketOrderRepository;
    private final TicketItemRepository ticketItemRepository;
    private final VectorStore vectorStore;

    @Tool(
            description = "tìm chuyến xe theo tên/ từ khóa , trả về tên, giá ghế , số ghế còn trống, tuyến xe cho từng chuyến lấy kết quả (thông tin số ghế còn trống lấy trực tiếp từ dữ liệu chuyến xe , không cần tool riêng để kiểm tra)"
    )
    public String searchTripbyName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return "Vui lòng nhập từ khóa tìm kiếm.";
        }
        List<BusTrip> trips = busTripRepository.findByNameContainingIgnoreCaseOrBusRouteNameContainingIgnoreCase(keyword.trim(), keyword.trim());
        if (trips.isEmpty()) {
            return "Không tìm thấy chuyến xe nào phù hợp với từ khóa: " + keyword;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Danh sách chuyến xe tìm được cho từ khóa '").append(keyword).append("':\n");
        for (BusTrip trip : trips) {
            String routeName = trip.getBusRoute() != null ? trip.getBusRoute().getName() : "N/A";
            sb.append(String.format("- [Mã chuyến: %d] Tên: %s | Tuyến: %s | Giá vé: %s VNĐ | Số ghế trống: %d | Mô tả: %s\n",
                    trip.getId(), trip.getName(), routeName, trip.getPrice(), trip.getStock(), trip.getDescription() != null ? trip.getDescription() : "Không có"));
        }
        return sb.toString();
    }

    @Tool(
            description = "trả về danh sách chuyến xe thuộc 1 tuyến xe, mỗi chuyến đưa ra danh sách đầy đủ giá và số ghế còn trống"
    )
    public String searchTripbyRoute(String routeName) {
        if (routeName == null || routeName.trim().isEmpty()) {
            return "Vui lòng nhập tên tuyến xe.";
        }
        List<BusTrip> trips = busTripRepository.findByBusRouteNameContainingIgnoreCase(routeName.trim());
        if (trips.isEmpty()) {
            return "Không tìm thấy chuyến xe nào thuộc tuyến xe: " + routeName;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Danh sách chuyến xe thuộc tuyến '").append(routeName).append("':\n");
        for (BusTrip trip : trips) {
            sb.append(String.format("- [Mã chuyến: %d] Tên: %s | Giá vé: %s VNĐ | Số ghế trống: %d | Mô tả: %s\n",
                    trip.getId(), trip.getName(), trip.getPrice(), trip.getStock(), trip.getDescription() != null ? trip.getDescription() : "Không có"));
        }
        return sb.toString();
    }

    @Tool(
            description = "Nhận câu hỏi của khách hàng về nhà xe ( địa chỉ , giờ hoạt động, chính sách,...) thực hiện similarity search trên bảng vector_store và trả về đoạn nội dung liên quan nhất để AI dùng làm căn cứ trả lời khách hàng"
    )
    public String getTravelInfo(String question) {
        if (question == null || question.trim().isEmpty()) {
            return "Câu hỏi không hợp lệ.";
        }
        try {
            List<Document> documents = vectorStore.similaritySearch(
                    SearchRequest.builder().query(question).topK(3).build()
            );
            if (documents == null || documents.isEmpty()) {
                return "Không tìm thấy thông tin liên quan đến câu hỏi: " + question;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Thông tin liên quan tìm được:\n");
            for (Document doc : documents) {
                sb.append("- ").append(doc.getFormattedContent() != null ? doc.getFormattedContent() : doc.getText()).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            return "Chưa có dữ liệu vector store hoặc xảy ra lỗi khi tìm kiếm: " + e.getMessage();
        }
    }

    @Tool(
            description = "Tạo đơn đặt vé xe cho hành khách. Yêu cầu truyền vào: tripId (ID chuyến xe), passengerName (Tên hành khách), passengerPhone (Số điện thoại), passengerEmail (Email, có thể để trống), quantity (Số lượng vé), note (Ghi chú, ví dụ: 'Đặt qua AI Chatbot')."
    )
    @Transactional
    public String createTicketOrder(Long tripId, String passengerName, String passengerPhone, String passengerEmail, Integer quantity, String note) {
        if (tripId == null || passengerName == null || passengerPhone == null || quantity == null || quantity <= 0) {
            return "Thông tin đặt vé không hợp lệ. Vui lòng cung cấp mã chuyến xe, tên khách hàng, số điện thoại và số lượng vé hợp lệ.";
        }

        Optional<BusTrip> tripOpt = busTripRepository.findById(tripId);
        if (tripOpt.isEmpty()) {
            return "Không tìm thấy chuyến xe với Mã chuyến: " + tripId;
        }

        BusTrip trip = tripOpt.get();
        if (trip.getStock() < quantity) {
            return String.format("Rất tiếc, chuyến xe '%s' chỉ còn %d ghế trống, không đủ cho %d vé quý khách yêu cầu.",
                    trip.getName(), trip.getStock(), quantity);
        }

        Optional<Passenger> passengerOpt = passengerRepository.findByPhone(passengerPhone);
        if (passengerOpt.isEmpty()) {
            return "Số điện thoại " + passengerPhone + " chưa được đăng ký trong hệ thống. Quý khách vui lòng đăng ký thông tin trước khi đặt vé.";
        }
        Passenger passenger = passengerOpt.get();

        trip.setStock(trip.getStock() - quantity);
        busTripRepository.save(trip);

        BigDecimal totalAmount = trip.getPrice().multiply(BigDecimal.valueOf(quantity));

        TicketOrder order = new TicketOrder();
        order.setPassenger(passenger);
        order.setTicketTicketOrderDate(LocalDateTime.now());
        order.setStatus(TicketOrderStatus.PENDING_PAYMENT);
        order.setTotalAmount(totalAmount);
        order.setNote(note != null ? note : "Đặt qua AI Chatbot");
        order = ticketOrderRepository.save(order);

        TicketItem item = new TicketItem();
        item.setTicketTicketOrder(order);
        item.setBusTrip(trip);
        item.setQuantity(quantity);
        item.setUnitPrice(trip.getPrice());
        ticketItemRepository.save(item);

        return String.format("Đặt vé thành công!\n- Mã đơn hàng: %d\n- Hành khách: %s (%s)\n- Chuyến xe: %s\n- Số lượng vé: %d\n- Tổng tiền: %s VNĐ\n- Trạng thái: %s",
                order.getId(), passenger.getFullName(), passenger.getPhone(), trip.getName(), quantity, totalAmount, order.getStatus());
    }
}
