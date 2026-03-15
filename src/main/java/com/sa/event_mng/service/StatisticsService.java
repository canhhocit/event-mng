package com.sa.event_mng.service;

import com.sa.event_mng.dto.response.EventRevenueStatsResponse;
import com.sa.event_mng.dto.response.EventStatusStatsResponse;
import com.sa.event_mng.dto.response.EventTemporalStatsResponse;
import com.sa.event_mng.mapper.StatsMapper;
import com.sa.event_mng.model.projection.EventRevenueStatsProjection;
import com.sa.event_mng.model.projection.EventStatusStatsProjection;
import com.sa.event_mng.model.projection.EventTemporalStatsProjection;
import com.sa.event_mng.repository.StatisticsRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticsService {

    StatisticsRepository statisticsRepository;
    StatsMapper statsMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public EventStatusStatsResponse getEventStatusStats(Long quarter, Long year) {
        List<EventStatusStatsProjection> eventStatusStatsProjections = statisticsRepository.findEventStatusStats(quarter, year);
        long total = eventStatusStatsProjections.stream()
                .mapToLong(EventStatusStatsProjection::getCount)
                .sum();

        List<EventStatusStatsResponse.EventStatusStatsDetail> statusDetails = eventStatusStatsProjections.stream()
                .map(statsMapper::toEventStatusStatsDetail)
                .map(detail -> new EventStatusStatsResponse.EventStatusStatsDetail(
                        detail.getStatus(),
                        total == 0 ? 0.0 : (detail.getCountEvents() * 100.0) / total,
                        detail.getCountEvents()
                ))
                .toList();

        return EventStatusStatsResponse.builder()
                .quarter(quarter)
                .year(year)
                .total(total)
                .eventStatusStatsDetail(statusDetails)
                .build();
    }

    Map<Integer, String> dayOfWeekMap = Map.of(
            1, "Sunday",
            2, "Monday",
            3, "Tuesday",
            4, "Wednesday",
            5, "Thursday",
            6, "Friday",
            7, "Saturday"
    );

    @PreAuthorize("hasRole('ADMIN')")
    public EventTemporalStatsResponse getEventTemporalStats(int dayOfWeek) {

        List<EventTemporalStatsProjection> eventTemporalStatsProjection = statisticsRepository.findEventTemporalStats(dayOfWeek);

        List<EventTemporalStatsResponse.EventTemporalStatsDetail> eventTemporalStatsDetails = eventTemporalStatsProjection.stream()
                .map(statsMapper::toEventTemporalStatsResponse)
                .map(detail -> new EventTemporalStatsResponse.EventTemporalStatsDetail(
                        detail.getHourOfDay(),
                        detail.getCountEvents(),
                        detail.getTotalTickets(),
                        detail.getTicketsSold(),
                        detail.getPercentageOfTicketsSold()
                ))
                .toList();
        return EventTemporalStatsResponse.builder()
                .day(dayOfWeekMap.get(dayOfWeek))
                .eventTemporalStatsDetail(eventTemporalStatsDetails)
                .build();
    }

    @PreAuthorize("hasRole('ORGANIZER') and @securityCustom.isCurrentUser(#idOrganizer, authentication)")
    public List<EventRevenueStatsResponse> getEventRevenueStats(Long idOrganizer) {
        List<EventRevenueStatsProjection> eventRevenueStats = statisticsRepository.findEventRevenueStats(idOrganizer);
        return eventRevenueStats.stream()
                .map(statsMapper::toEventRevenueStatsResponse)
                .toList();
    }
}
