package com.sa.event_mng.dto.request;

import com.sa.event_mng.model.enums.EventStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRequest {

    @NotBlank(message = "EVENT_NAME_REQUIRED")
    private String name;

    @NotNull(message = "CATEGORY_ID_REQUIRED")
    private Long categoryId;

    private String location;

    @NotNull(message = "START_TIME_REQUIRED")
    private LocalDateTime startTime;

    @NotNull(message = "END_TIME_REQUIRED")
    private LocalDateTime endTime;

    private String description;

    private EventStatus status;

    private List<MultipartFile> files;
}
