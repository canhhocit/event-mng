package com.sa.event_mng.repository;

import com.sa.event_mng.model.entity.Event;
import com.sa.event_mng.model.projection.EventRevenueStatsProjection;
import com.sa.event_mng.model.projection.EventStatusStatsProjection;
import com.sa.event_mng.model.projection.EventTemporalStatsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StatisticsRepository extends JpaRepository<Event, Long> {
    @Query(value = "SELECT status AS status, COUNT(*) AS count " +
            "FROM events " +
            "WHERE YEAR(start_time) = :year " +
            "AND QUARTER(start_time) = :quarter " +
            "GROUP BY status",
            nativeQuery = true)
    List<EventStatusStatsProjection> findEventStatusStats(@Param("quarter") Long quarter , @Param("year") Long year);

    @Query(value = """
    SELECT 
        HOUR(e.start_time) AS hourOfDay,
        COUNT(DISTINCT e.id) AS countEvents,
        SUM(t.total_quantity) AS totalTickets,
        SUM(t.total_quantity - t.remaining_quantity) AS ticketsSold,
        CASE 
            WHEN SUM(t.total_quantity) = 0 THEN 0
            ELSE SUM(t.total_quantity - t.remaining_quantity) * 100.0 / SUM(t.total_quantity)
        END AS percentageOfTicketsSold
    FROM events e
    JOIN ticket_types t ON e.id = t.event_id
    WHERE DAYOFWEEK(e.start_time) = :dayOfWeek
    GROUP BY HOUR(e.start_time)
    ORDER BY HOUR(e.start_time)
    """, nativeQuery = true)
    List<EventTemporalStatsProjection> findEventTemporalStats(@Param("dayOfWeek") Integer dayOfWeek);

    @Query(value = """

    select e.name as eventName,
    sum(oi.subtotal) as totalRevenue,
    sum(tt.total_quantity - tt.remaining_quantity) as ticketsSold,
    CASE
    	WHEN SUM(tt.total_quantity) = 0 THEN 0
    	ELSE SUM(tt.total_quantity - tt.remaining_quantity) * 100.0 / SUM(tt.total_quantity)
    END AS percentageOfTicketsSold
    from orders o
    join order_items oi on o.id = oi.order_id
    join ticket_types tt on oi.ticket_type_id = tt.id
    right join events e on e.id = tt.event_id
    where e.organizer_id = :id
    group by e.id
    """, nativeQuery = true)
    List<EventRevenueStatsProjection> findEventRevenueStats(@Param("id") Long id);
}
