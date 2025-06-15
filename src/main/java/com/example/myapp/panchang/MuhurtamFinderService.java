package com.example.myapp.panchang;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class MuhurtamFinderService {

    private final PanchangamRepository panchangamRepo;
    private final DailyTimesRepository dailyTimesRepo;

    public MuhurtamFinderService(PanchangamRepository panchangamRepo, DailyTimesRepository dailyTimesRepo) {
        this.panchangamRepo = panchangamRepo;
        this.dailyTimesRepo = dailyTimesRepo;
    }

    private final Map<String, Integer> nakshatraOrder = createNakshatraMap();

    public List<MuhurtamFindResponseDto> findMuhurtams(List<String> janmaNakshatras) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(90);
        List<MuhurtamFindResponseDto> result = new ArrayList<>();

        Set<String> allFavorableNakshatras = new HashSet<>();
        for (String nakshatra : janmaNakshatras) {
            allFavorableNakshatras.addAll(getTaraBalamNakshatras(nakshatra));
         }

            for (LocalDate date = today; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<Panchangam> panchangams = panchangamRepo.findByDate(date);

            Optional<DailyTimes> dailyOpt = dailyTimesRepo.findByDate(date);
            if (panchangams.isEmpty() || dailyOpt.isEmpty()) continue;

            DailyTimes d = dailyOpt.get();
            for (Panchangam p : panchangams) {

                String todayNakshatra = p.getNakshatram();
                boolean isFavorable = allFavorableNakshatras.contains(todayNakshatra);

                String lagna = p.getMuhurtamLagna();
                String muhurtamTime = p.getMuhurtamTime();
                LocalTime muhurtham = parseTime(muhurtamTime);

             if (isFavorable && (muhurtham == null || lagna == null)) {
             // favorable but missing data → gray
             result.add(MuhurtamFindResponseDto.builder()
                 .date(date)
                 .nakshatram(todayNakshatra)
                 .tithi(p.getTithi())
                 .status("gray")
                 .muhurtamTime(muhurtamTime)
                 .muhurtamLagna(lagna)
                 .build());
                    continue;
                }

                  if (!isFavorable) {
                 // skip non-favorable nakshatra altogether
                    continue;
                    }


                LocalTime lagnaStart = getLagnaStart(d, lagna);
                LocalTime lagnaEnd = getLagnaEnd(d, lagna);

                if (lagnaStart == null || lagnaEnd == null) {
                    result.add(MuhurtamFindResponseDto.builder()
                            .date(date)
                            .nakshatram(todayNakshatra)
                            .tithi(p.getTithi())
                            .status("gray")
                            .muhurtamTime(muhurtamTime)
                            .build());
                    continue;
                }

            boolean withinLagna = isWithinRange(muhurtham, lagnaStart, lagnaEnd);
            boolean hasConflict = isInauspicious(muhurtham, d);


                if (withinLagna && !hasConflict) {
                    result.add(MuhurtamFindResponseDto.builder()
                            .date(date)
                            .nakshatram(todayNakshatra)
                            .tithi(p.getTithi())
                            .status("green")
                            .muhurtamLagna(capitalize(lagna))
                            .muhurtamTime(muhurtamTime)
                            .notes(p.getNotes())
                            .build());
                } else if (withinLagna) {
                    result.add(MuhurtamFindResponseDto.builder()
                            .date(date)
                            .nakshatram(todayNakshatra)
                            .tithi(p.getTithi())
                            .status("red")
                            .muhurtamLagna(capitalize(lagna))
                            .muhurtamTime(muhurtamTime)
                            .notes(p.getNotes())
                            .build());
                } else {
    // Try to adjust muhurtam time inside original lagna window
    LocalTime actualStart = getLagnaStart(d, lagna);
    LocalTime actualEnd = getLagnaEnd(d, lagna);

    if (actualStart != null && actualEnd != null && actualStart.plusMinutes(15).isBefore(actualEnd)) {
        if (!overlapsInauspicious(actualStart, actualStart.plusMinutes(15), d)) {
            String altRange = actualStart + " - " + actualStart.plusMinutes(15);

            result.add(MuhurtamFindResponseDto.builder()
                    .date(date)
                    .nakshatram(todayNakshatra)
                    .tithi(p.getTithi())
                    .status("orange")
                    .muhurtamLagna(capitalize(lagna)) // keep original lagna
                    .alternateTime(altRange)
                    .notes(p.getNotes())
                    .build());
            continue;
        }
    }

    // If original lagna is not usable, mark as gray
    result.add(MuhurtamFindResponseDto.builder()
            .date(date)
            .nakshatram(todayNakshatra)
            .tithi(p.getTithi())
            .status("gray")
            .muhurtamTime(muhurtamTime)
            .notes(p.getNotes())
            .build());
}

            }
        }

        return result;
    }

private Set<String> getTaraBalamNakshatras(String janmaNakshatra) {
    Integer startIndex = nakshatraOrder.get(janmaNakshatra);
    if (startIndex == null) return Set.of();

    int[] favorablePositionsInCycle = {1, 3, 5, 7, 8}; // 0-based: 2nd, 4th, 6th, 8th, 9th Taras

    Set<String> result = new HashSet<>();
    for (int cycle = 0; cycle < 3; cycle++) { // 3 full Tara cycles = 9 positions (27 nakshatrams)
        for (int offset : favorablePositionsInCycle) {
            int index = (startIndex + cycle * 9 + offset) % 27;
            String nak = getNakshatraByIndex(index);
            if (nak != null) result.add(nak);
        }
    }
    return result;
}


    private Map<String, Integer> createNakshatraMap() {
        List<String> names = List.of("Ashwini", "Bharani", "Krittika", "Rohini", "Mrigashira", "Ardra", "Punarvasu",
                "Pushya", "Ashlesha", "Magha", "Purva Phalguni", "Uttara Phalguni", "Hasta", "Chitra", "Swati",
                "Vishakha", "Anuradha", "Jyeshtha", "Mula", "Purva Ashadha", "Uttara Ashadha", "Shravana",
                "Dhanishta", "Shatabhisha", "Purva Bhadrapada", "Uttara Bhadrapada", "Revati");

        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < names.size(); i++) map.put(names.get(i), i);
        return map;
    }

    private String getNakshatraByIndex(int index) {
        for (Map.Entry<String, Integer> e : nakshatraOrder.entrySet()) {
            if (e.getValue() == index) return e.getKey();
        }
        return null;
    }
private boolean isInauspicious(LocalTime time, DailyTimes d) {
    return overlapsInauspicious(time, time.plusMinutes(1), d);
}


private boolean isWithinRange(LocalTime time, LocalTime rangeStart, LocalTime rangeEnd) {
    return time != null && rangeStart != null && rangeEnd != null &&
           !time.isBefore(rangeStart) && !time.isAfter(rangeEnd);
}



    private LocalTime getLagnaStart(DailyTimes d, String lagna) {
        return getTimeField(d, lagna.toLowerCase() + "LagnaStart");
    }

    private LocalTime getLagnaEnd(DailyTimes d, String lagna) {
        return getTimeField(d, lagna.toLowerCase() + "LagnaEnd");
    }

    private LocalTime getTimeField(DailyTimes d, String fieldName) {
        try {
            var field = DailyTimes.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (LocalTime) field.get(d);
        } catch (Exception e) {
            return null;
        }
    }

  private LocalTime parseTime(String time) {
    try {
        return LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
    } catch (Exception e) {
        return null;
    }
}


    private boolean overlapsInauspicious(LocalTime start, LocalTime end, DailyTimes d) {
        return overlaps(start, end, d.getRahukalamStart(), d.getRahukalamEnd()) ||
                overlaps(start, end, d.getDurmohurtamStart(), d.getDurmohurtamEnd()) ||
                overlaps(start, end, d.getYamagandamStart(), d.getYamagandamEnd()) ||
                overlaps(start, end, d.getVarjamStart(), d.getVarjamEnd());
    }

    private boolean overlaps(LocalTime aStart, LocalTime aEnd, LocalTime bStart, LocalTime bEnd) {
        return aStart != null && aEnd != null && bStart != null && bEnd != null &&
                !aEnd.isBefore(bStart) && !aStart.isAfter(bEnd);
    }


    private String capitalize(String str) {
        return (str == null || str.isEmpty()) ? str : Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    
}
