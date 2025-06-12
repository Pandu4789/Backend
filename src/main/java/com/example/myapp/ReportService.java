package com.example.myapp;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ReportService {

    @Autowired private AppointmentBookingRepository bookingRepository;
    @Autowired private MuhurtamRequestRepository muhurtamRepository;
    @Autowired private AppointmentRepository manualAppointmentRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private EventRepository eventRepository;

    private record UnifiedBooking(String eventName, String customerName, LocalDate bookingDate) {}

    // Utility method to parse estimated price string (single or range) and return average price as double
   private double parseEstimatedPrice(String priceStr) {
    if (priceStr == null || priceStr.isBlank()) return 0.0;
    priceStr = priceStr.trim();

    // Skip non-numeric descriptions
    String lower = priceStr.toLowerCase();
    if (lower.contains("varies") || lower.contains("sponsorship") || lower.contains("contact")) {
        System.out.println("Skipped non-numeric price: " + priceStr);
        return 0.0;
    }

    // Remove currency symbols and other characters
    priceStr = priceStr.replaceAll("[^0-9.\\-]", "");

    try {
        if (priceStr.contains("-")) {
            String[] parts = priceStr.split("-");
            if (parts.length == 2) {
                double low = Double.parseDouble(parts[0].trim());
                double high = Double.parseDouble(parts[1].trim());
                return (low + high) / 2.0;
            }
            return 0.0;
        } else {
            return Double.parseDouble(priceStr);
        }
    } catch (NumberFormatException e) {
        System.out.println("Failed to parse price: " + priceStr);
        return 0.0;
    }
}

    public byte[] generateDetailedReportPdf(Long priestId, LocalDate startDate, LocalDate endDate) throws IOException {
        User priest = userRepository.findById(priestId).orElse(null);

        List<AppointmentBooking> customerBookings = bookingRepository.findByPriestIdAndDateBetween(priestId, startDate.toString(), endDate.toString());
        List<Appointment> manualAppointments = manualAppointmentRepository.findByPriestIdAndStartBetween(priestId, startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        List<MuhurtamRequest> muhurtamRequests = muhurtamRepository.findByPriestId(priestId);

        // Map event names to average prices (parsing string, handling range)
        Map<String, Double> eventPriceMap = new HashMap<>();
        eventRepository.findAll().forEach(event -> {
            eventPriceMap.put(event.getName(), parseEstimatedPrice(event.getEstimatedPrice()));
        });

        Stream<UnifiedBooking> unifiedCustomerBookings = customerBookings.stream()
            .filter(b -> b != null && b.getStatus() != null && b.getDate() != null &&
                         ("ACCEPTED".equalsIgnoreCase(b.getStatus()) || "CONFIRMED".equalsIgnoreCase(b.getStatus())))
            .map(b -> new UnifiedBooking(
                b.getEvent() != null ? b.getEvent().getName() : "Unknown Event",
                b.getName(),
                LocalDate.parse(b.getDate())
            ));

        Stream<UnifiedBooking> unifiedManualAppointments = manualAppointments.stream()
            .filter(a -> a != null && a.getStart() != null)
            .map(a -> new UnifiedBooking(
                a.getEventName() != null ? a.getEventName() : "Manual Appointment",
                a.getName(),
                a.getStart().toLocalDate()
            ));

        List<UnifiedBooking> allBookings = Stream.concat(unifiedCustomerBookings, unifiedManualAppointments)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        YearMonth currentMonth = YearMonth.now();
        int currentYear = LocalDate.now().getYear();

        long bookingsThisMonth = allBookings.stream()
            .filter(b -> YearMonth.from(b.bookingDate()).equals(currentMonth))
            .count();

        long bookingsThisYear = allBookings.stream()
            .filter(b -> b.bookingDate().getYear() == currentYear)
            .count();

        Map<String, Long> eventCounts = allBookings.stream()
            .collect(Collectors.groupingBy(
                b -> b.eventName() != null ? b.eventName() : "Unknown",
                Collectors.counting()
            ));

        long confirmedCount = customerBookings.stream()
            .filter(b -> "ACCEPTED".equalsIgnoreCase(b.getStatus()) || "CONFIRMED".equalsIgnoreCase(b.getStatus()))
            .count();

        long rejectedCount = customerBookings.stream()
            .filter(b -> "REJECTED".equalsIgnoreCase(b.getStatus()))
            .count();

        // Calculate revenue only from bookings and manual appointments using average price from eventPriceMap
        double totalRevenue = allBookings.stream()
            .mapToDouble(b -> eventPriceMap.getOrDefault(b.eventName(), 0.0))
            .sum();

        int totalMuhurtamRequests = muhurtamRequests.size();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(baos));
        Document document = new Document(pdfDoc);

        try {
            document.add(new Paragraph("Priestify Detailed Report").setBold().setFontSize(20).setTextAlignment(TextAlignment.CENTER));
            if (priest != null) {
                document.add(new Paragraph("Priest: " + priest.getFirstName() + " " + priest.getLastName()+ "  | ID: " +priest.getId()));
            }
            document.add(new Paragraph("Period: " + startDate + " to " + endDate).setMarginBottom(20));

            document.add(new Paragraph("Overall Summary").setBold().setFontSize(16));
            Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{3, 1})).useAllAvailableWidth();
            summaryTable.addHeaderCell("Metric");
            summaryTable.addHeaderCell("Total");

            summaryTable.addCell("Confirmed Bookings");
            summaryTable.addCell(String.valueOf(confirmedCount));
            summaryTable.addCell("Rejected Bookings");
            summaryTable.addCell(String.valueOf(rejectedCount));
            summaryTable.addCell("Appointments added by You");
            summaryTable.addCell(String.valueOf(manualAppointments.size()));
            summaryTable.addCell("Overall Muhurtam Requests");
            summaryTable.addCell(String.valueOf(totalMuhurtamRequests));
            summaryTable.addCell("Total Bookings This Month");
            summaryTable.addCell(String.valueOf(bookingsThisMonth));
            summaryTable.addCell("Total Bookings This Year");
            summaryTable.addCell(String.valueOf(bookingsThisYear));
            summaryTable.addCell("Estimated Revenue ($)");
            summaryTable.addCell("$ " + String.format("%.2f", totalRevenue));
            document.add(summaryTable);

            document.add(new Paragraph("Bookings by Event Type").setBold().setFontSize(16).setMarginTop(20));
            Table eventTable = new Table(UnitValue.createPercentArray(new float[]{3, 1})).useAllAvailableWidth();
            eventTable.addHeaderCell("Event Name");
            eventTable.addHeaderCell("Total Bookings");

            if (eventCounts.isEmpty()) {
                eventTable.addCell(new Cell(1, 2).add(new Paragraph("No bookings in this period.")));
            } else {
                eventCounts.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEach(entry -> {
                        eventTable.addCell(entry.getKey());
                        eventTable.addCell(String.valueOf(entry.getValue()));
                    });
            }
            document.add(eventTable);

        } finally {
            document.close();
        }
        return baos.toByteArray();
    }
}
