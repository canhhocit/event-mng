package com.sa.event_mng.mapper;

import com.sa.event_mng.dto.response.EventRevenueStatsResponse;
import com.sa.event_mng.dto.response.EventStatusStatsResponse;
import com.sa.event_mng.dto.response.EventTemporalStatsResponse;
import com.sa.event_mng.model.projection.EventRevenueStatsProjection;
import com.sa.event_mng.model.projection.EventStatusStatsProjection;
import com.sa.event_mng.model.projection.EventTemporalStatsProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StatsMapper {

    @Mapping(target = "percentage", ignore = true)
    @Mapping(target = "countEvents", source = "count")
    EventStatusStatsResponse.EventStatusStatsDetail toEventStatusStatsDetail(EventStatusStatsProjection eventStatusStatsProjection);

    EventTemporalStatsResponse.EventTemporalStatsDetail toEventTemporalStatsResponse(EventTemporalStatsProjection eventTemporalStatsProjection);

    EventRevenueStatsResponse toEventRevenueStatsResponse(EventRevenueStatsProjection eventRevenueStatsProjection);
}
