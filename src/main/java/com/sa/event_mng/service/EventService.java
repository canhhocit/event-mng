package com.sa.event_mng.service;

import com.sa.event_mng.dto.request.EventRequest;
import com.sa.event_mng.dto.response.EventResponse;
import com.sa.event_mng.exception.AppException;
import com.sa.event_mng.exception.ErrorCode;
import com.sa.event_mng.mapper.EventMapper;
import com.sa.event_mng.model.entity.*;
import com.sa.event_mng.model.enums.EventStatus;
import com.sa.event_mng.repository.CategoryRepository;
import com.sa.event_mng.repository.EventRepository;
import com.sa.event_mng.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@lombok.extern.slf4j.Slf4j
public class EventService {

        EventRepository eventRepository;
        CategoryRepository categoryRepository;
        UserRepository userRepository;
        EventMapper eventMapper;
        
        @org.springframework.beans.factory.annotation.Value("${app.file.base-url}")
        @lombok.experimental.NonFinal
        String fileBaseUrl;

        @Transactional
        // @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER')")
        @PreAuthorize("hasRole('ORGANIZER')")
        public EventResponse create(EventRequest request) {
                log.info("Đang tạo sự kiện mới: Name={}, CategoryID={}", request.getName(), request.getCategoryId());
                
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                User organizer = userRepository.findByUsername(username)
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

                Category category = categoryRepository.findById(request.getCategoryId())
                                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

                Event event = Event.builder()
                                .name(request.getName())
                                .category(category)
                                .organizer(organizer)
                                .location(request.getLocation())
                                .startTime(request.getStartTime())
                                .endTime(request.getEndTime())
                                .saleStartDate(request.getSaleStartDate())
                                .saleEndDate(request.getSaleEndDate())
                                .description(request.getDescription())
                                .status(request.getStatus() != null ? request.getStatus() : EventStatus.PENDING)
                                .build();

                if (request.getFiles() != null && !request.getFiles().isEmpty()) {
                        List<EventImage> images = saveImages(request.getFiles(), event);
                        event.setImages(images);
                }

                return eventMapper.toEventResponse(eventRepository.save(event));
        }

        public Page<EventResponse> getAllPublished(PageRequest pageRequest) {
                List<EventStatus> activeStatuses = List.of(
                                EventStatus.UPCOMING,
                                EventStatus.OPENING,
                                EventStatus.CLOSED
                );
                Page<Event> events = eventRepository.findByStatusIn(activeStatuses, pageRequest);
                return events.map(eventMapper::toEventResponse);
        }

        public EventResponse getById(Long id) {
                Event event = eventRepository.findById(id)
                                .orElseThrow(() -> new AppException(ErrorCode.EVENT_NOT_FOUND));
                return eventMapper.toEventResponse(event);
        }

        @Transactional
        @PreAuthorize("hasRole('ADMIN') or (hasRole('ORGANIZER') and @securityCustom.isOwner(#id, authentication))") // This logic
                                                                                                           // needs
                                                                                                           // adjustment
        public EventResponse update(Long id, EventRequest request) {
                Event event = eventRepository.findById(id)
                                .orElseThrow(() -> new AppException(ErrorCode.EVENT_NOT_FOUND));
                event.setName(request.getName());
                event.setLocation(request.getLocation());
                event.setStartTime(request.getStartTime());
                event.setEndTime(request.getEndTime());
                event.setSaleStartDate(request.getSaleStartDate());
                event.setSaleEndDate(request.getSaleEndDate());
                event.setDescription(request.getDescription());
                if (request.getStatus() != null)
                        event.setStatus(request.getStatus());

                if (request.getCategoryId() != null) {
                        Category category = categoryRepository.findById(request.getCategoryId())
                                        .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
                        event.setCategory(category);
                }

                if (request.getFiles() != null && !request.getFiles().isEmpty()) {
                        List<EventImage> newImages = saveImages(request.getFiles(), event);
                        if (event.getImages() == null) {
                                event.setImages(new ArrayList<>());
                        }
                        event.getImages().addAll(newImages);
                }

                return eventMapper.toEventResponse(eventRepository.save(event));
        }

        private List<EventImage> saveImages(List<MultipartFile> files, Event event) {
                List<EventImage> images = new ArrayList<>();
                File uploadDir = new File("uploads/");
                if (!uploadDir.exists()) {
                        uploadDir.mkdirs();
                }

                for (MultipartFile file : files) {
                        if (file.isEmpty())
                                continue;
                        try {
                                String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                                File destinationFile = new File(
                                                uploadDir.getAbsolutePath() + File.separator + filename);
                                file.transferTo(destinationFile);
                                String imageUrl = fileBaseUrl + "/" + filename;
                                images.add(EventImage.builder().imageUrl(imageUrl).event(event).build());
                        } catch (IOException e) {
                                throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
                        }
                }
                return images;
        }

        public Page<EventResponse> getAllForAdmin(String search, String status, PageRequest pageRequest) {
                Page<Event> events;
                boolean hasSearch = search != null && !search.isBlank();
                boolean hasStatus = status != null && !status.isBlank();

                if (hasSearch && hasStatus) {
                        events = eventRepository.findByNameContainingIgnoreCaseAndStatus(
                                        search, EventStatus.valueOf(status), pageRequest);
                } else if (hasSearch) {
                        events = eventRepository.findByNameContainingIgnoreCase(search, pageRequest);
                } else if (hasStatus) {
                        events = eventRepository.findByStatus(EventStatus.valueOf(status), pageRequest);
                } else {
                        events = eventRepository.findAll(pageRequest);
                }
                return events.map(eventMapper::toEventResponse);
        }

        @Transactional
        @PreAuthorize("hasRole('ADMIN')")
        public EventResponse updateStatus(Long id, EventStatus status) {
                Event event = eventRepository.findById(id)
                                .orElseThrow(() -> new AppException(ErrorCode.EVENT_NOT_FOUND));
                
                //duyệt -> UPCOMING, từ chối -> CANCELLED
                event.setStatus(status);
                
                return eventMapper.toEventResponse(eventRepository.save(event));
        }

        @SuppressWarnings("unused")
        private EventStatus determineInitialStatus(Event event, LocalDateTime now) {
                // Xác định trạng thái ngay lập tức dựa trên thời gian khi vừa được duyệt
                if (event.getEndTime() != null && now.isAfter(event.getEndTime())) return EventStatus.COMPLETED;
                if (event.getSaleEndDate() != null && now.isAfter(event.getSaleEndDate())) return EventStatus.CLOSED;
                if (event.getSaleStartDate() != null && (now.isAfter(event.getSaleStartDate()) || now.isEqual(event.getSaleStartDate()))) return EventStatus.OPENING;
                return EventStatus.UPCOMING;
        }

}
