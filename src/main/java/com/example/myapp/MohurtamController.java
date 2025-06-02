package com.example.myapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api")
public class MohurtamController {

    private final PanchangamRepository panchangamRepository;
    private final DailyTimesRepository dailyTimesRepository;

    @Autowired
    public MohurtamController(PanchangamRepository panchangamRepository, DailyTimesRepository dailyTimesRepository) {
        this.panchangamRepository = panchangamRepository;
        this.dailyTimesRepository = dailyTimesRepository;
    }
private String formatTimeRange(LocalTime start, LocalTime end) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
    return start.format(formatter) + " - " + end.format(formatter);
}

    @GetMapping("/nakshatram-schedules")
    public List<NakshatramResponseDTO> getNakshatramSchedules() {
        List<Panchangam> allPanchangams = panchangamRepository.findAll();

        Map<String, List<ScheduleDTO>> groupedSchedules = new HashMap<>();

        for (Panchangam p : allPanchangams) {
            Optional<DailyTimes> optionalDailyTimes = dailyTimesRepository.findByDate(p.getDate());

            ScheduleDTO schedule = new ScheduleDTO();
            schedule.setDate(p.getDate());
            schedule.setPaksha(p.getPaksha());
            schedule.setTithi(p.getTithi());
            schedule.setVaaram(p.getVaaram());
            schedule.setLagnam(p.getLagnam());
            schedule.setMohurtam(p.getMohurtam());
            schedule.setTime(p.getTime());
            schedule.setNotes(p.getNotes());
optionalDailyTimes.ifPresent(dt -> {
    schedule.setYamagandam(formatTimeRange(dt.getYamagandamStart(), dt.getYamagandamEnd()));
    schedule.setRahukalam(formatTimeRange(dt.getRahukalamStart(), dt.getRahukalamEnd()));
    schedule.setVarjam(formatTimeRange(dt.getVarjamStart(), dt.getVarjamEnd()));
    schedule.setDurmohurtam(formatTimeRange(dt.getDurmohurtamStart(), dt.getDurmohurtamEnd()));
});


            groupedSchedules
                .computeIfAbsent(p.getNakshatram(), k -> new ArrayList<>())
                .add(schedule);
        }

        return groupedSchedules.entrySet().stream()
                .map(entry -> new NakshatramResponseDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
