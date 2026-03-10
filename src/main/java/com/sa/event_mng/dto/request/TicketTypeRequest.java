package com.sa.event_mng.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketTypeRequest {

    @NotNull(message = "UNCATEGORIZED_EXCEPTION") 
    private Long eventId;

    @NotBlank(message = "UNCATEGORIZED_EXCEPTION")
    private String name;

    @NotNull(message = "UNCATEGORIZED_EXCEPTION")
    private BigDecimal price;

    @NotNull(message = "UNCATEGORIZED_EXCEPTION")
    private Integer totalQuantity;

    private String description;
}
