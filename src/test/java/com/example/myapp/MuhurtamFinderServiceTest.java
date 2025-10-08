package com.example.myapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.myapp.panchang.DailyTimes;
import com.example.myapp.panchang.DailyTimesRepository;
import com.example.myapp.panchang.MuhurtamFinderService;
import com.example.myapp.panchang.Panchangam;
import com.example.myapp.panchang.PanchangamRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MuhurtamFinderServiceTest {

    private PanchangamRepository panchangamRepo;
    private DailyTimesRepository dailyTimesRepo;
    private MuhurtamFinderService service;
    private LocalDate today;

    @BeforeEach
    void setup() {
        panchangamRepo = mock(PanchangamRepository.class);
        dailyTimesRepo = mock(DailyTimesRepository.class);
        service = new MuhurtamFinderService(panchangamRepo, dailyTimesRepo);
        today = LocalDate.now();
    }

    @Test
    void testGreenStatus() {
        var p = new Panchangam();
        p.setNakshatram("Rohini");
        p.setTithi("Saptami");
        p.setMuhurtamLagna("mesha");
        p.setMuhurtamTime("05:15");

        var d = new DailyTimes();
        d.setMeshaLagnaStart(LocalTime.of(5, 0));
        d.setMeshaLagnaEnd(LocalTime.of(6, 0));

        when(panchangamRepo.findByDate(today)).thenReturn(List.of(p));
        when(dailyTimesRepo.findByDate(today)).thenReturn(Optional.of(d));

        // ✅ CHANGED: The service returns the response object, not a list
        var response = service.findMuhurtams(List.of("Ashwini"));
        var dailyResults = response.getDailyResults();

        // ✅ CHANGED: Assertions now run on the inner list
        assertEquals(1, dailyResults.size());
        assertEquals("green", dailyResults.get(0).getStatus());
        assertNotNull(response.getFavorableNakshatrams()); // Also good to test this
    }

    @Test
    void testRedStatusDueToConflict() {
        var p = new Panchangam();
        p.setNakshatram("Rohini");
        p.setTithi("Ashtami");
        p.setMuhurtamLagna("mesha");
        p.setMuhurtamTime("05:15");

        var d = new DailyTimes();
        d.setMeshaLagnaStart(LocalTime.of(5, 0));
        d.setMeshaLagnaEnd(LocalTime.of(6, 0));
        d.setRahukalamStart(LocalTime.of(5, 0));
        d.setRahukalamEnd(LocalTime.of(5, 30));

        when(panchangamRepo.findByDate(today)).thenReturn(List.of(p));
        when(dailyTimesRepo.findByDate(today)).thenReturn(Optional.of(d));
        
        // ✅ CHANGED: The service returns the response object
        var response = service.findMuhurtams(List.of("Ashwini"));
        var dailyResults = response.getDailyResults();
        
        // ✅ CHANGED: Assertions now run on the inner list
        assertEquals(1, dailyResults.size());
        assertEquals("red", dailyResults.get(0).getStatus());
    }

    @Test
    void testOrangeStatus() {
        var p = new Panchangam();
        p.setNakshatram("Rohini");
        p.setTithi("Navami");
        p.setMuhurtamLagna("mesha");
        p.setMuhurtamTime("04:00"); // outside mesha lagna

        var d = new DailyTimes();
        d.setMeshaLagnaStart(LocalTime.of(5, 0));
        d.setMeshaLagnaEnd(LocalTime.of(6, 0));
        
        when(panchangamRepo.findByDate(today)).thenReturn(List.of(p));
        when(dailyTimesRepo.findByDate(today)).thenReturn(Optional.of(d));

        // ✅ CHANGED: The service returns the response object
        var response = service.findMuhurtams(List.of("Ashwini"));
        var dailyResults = response.getDailyResults();

        // ✅ CHANGED: Assertions now run on the inner list
        assertEquals(1, dailyResults.size());
        assertEquals("orange", dailyResults.get(0).getStatus()); // Updated this test to check for orange status as intended
        assertNotNull(dailyResults.get(0).getAlternateTime());
    }

    @Test
    void testNonFavorableSkipped() {
        var p = new Panchangam();
        p.setNakshatram("Magha"); // Not favorable for Ashwini
        p.setTithi("Dashami");
        p.setMuhurtamLagna("karka");
        p.setMuhurtamTime("06:00");

        var d = new DailyTimes();
        d.setKarkaLagnaStart(LocalTime.of(6, 0));
        d.setKarkaLagnaEnd(LocalTime.of(7, 0));

        when(panchangamRepo.findByDate(today)).thenReturn(List.of(p));
        when(dailyTimesRepo.findByDate(today)).thenReturn(Optional.of(d));

        // ✅ CHANGED: The service returns the response object
        var response = service.findMuhurtams(List.of("Ashwini"));
        
        // ✅ CHANGED: Assertions now run on the inner list
        assertTrue(response.getDailyResults().isEmpty(), "Entries with non-favorable nakshatra should be skipped");
    }

    @Test
    void sanityCheck() {
        System.out.println("🟢 Sanity check ran!");
        assertTrue(1 < 2);
    }
}
