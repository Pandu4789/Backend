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

    // **1)** Green: favorable, within lagna, no conflict
    @Test
    void testGreenStatus() {
        var p = new Panchangam();
        p.setNakshatram("Rohini"); // Ensure Rohini is favorable for input "Ashwini"
        p.setTithi("Saptami");
        p.setMuhurtamLagna("mesha");
        p.setMuhurtamTime("05:15");

        var d = new DailyTimes();
        d.setMeshaLagnaStart(LocalTime.of(5, 0));
        d.setMeshaLagnaEnd(LocalTime.of(6, 0));
        // No conflicting periods set => default null cause no clashes

        when(panchangamRepo.findByDate(today)).thenReturn(List.of(p));
        when(dailyTimesRepo.findByDate(today)).thenReturn(Optional.of(d));

        var list = service.findMuhurtams(List.of("Ashwini"));
        assertEquals(1, list.size());
        assertEquals("green", list.get(0).getStatus());
    }

    // **2)** Red: favorable, within lagna, but conflict present
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
        d.setRahukalamEnd(LocalTime.of(5, 30)); // conflicts with muhurtamTime

        when(panchangamRepo.findByDate(today)).thenReturn(List.of(p));
        when(dailyTimesRepo.findByDate(today)).thenReturn(Optional.of(d));

        var list = service.findMuhurtams(List.of("Ashwini"));
        assertEquals(1, list.size());
        assertEquals("red", list.get(0).getStatus());
    }

    // **3)** Orange: favorable, outside lagna, alternate found
    @Test
    void testOrangeStatusWithAlternateLagna() {
        var p = new Panchangam();
        p.setNakshatram("Rohini");
        p.setTithi("Navami");
        p.setMuhurtamLagna("mesha");
        p.setMuhurtamTime("04:00"); // before mesha lagna

        var d = new DailyTimes();
        d.setMeshaLagnaStart(LocalTime.of(5, 0));
        d.setMeshaLagnaEnd(LocalTime.of(6, 0));

        // Set mithuna lagna with length > 15 min and no conflicts
        d.setMithunaLagnaStart(LocalTime.of(4, 0));
        d.setMithunaLagnaEnd(LocalTime.of(5, 0));

        when(panchangamRepo.findByDate(today)).thenReturn(List.of(p));
        when(dailyTimesRepo.findByDate(today)).thenReturn(Optional.of(d));

        var list = service.findMuhurtams(List.of("Ashwini"));
        assertEquals(1, list.size());
        assertEquals("orange", list.get(0).getStatus());
        assertNotNull(list.get(0).getAlternateTime());
    }

    // **4)** Gray: non‑favorable => should be skipped entirely
    @Test
    void testNonFavorableSkipped() {
        var p = new Panchangam();
        p.setNakshatram("Magha"); // Assume Magha is not favorable
        p.setTithi("Dashami");
        p.setMuhurtamLagna("karka");
        p.setMuhurtamTime("06:00");

        var d = new DailyTimes();
        d.setKarkaLagnaStart(LocalTime.of(6, 0));
        d.setKarkaLagnaEnd(LocalTime.of(7, 0));

        when(panchangamRepo.findByDate(today)).thenReturn(List.of(p));
        when(dailyTimesRepo.findByDate(today)).thenReturn(Optional.of(d));

        var list = service.findMuhurtams(List.of("Ashwini"));
        assertTrue(list.isEmpty(), "Entries with non-favorable nakshatra should be skipped");
    }
@Test
void sanityCheck() {
    System.out.println("🟢 Sanity check ran!");
    assertTrue(1 < 2);
}

}
