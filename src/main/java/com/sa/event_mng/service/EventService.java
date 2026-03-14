package com.sa.event_mng.service;

import com.sa.event_mng.dto.request.EventRequest;
import com.sa.event_mng.dto.response.EventResponse;
import com.sa.event_mng.exception.AppException;
import com.sa.event_mng.exception.ErrorCode;
import com.sa.event_mng.mapper.EventMapper;
import com.sa.event_mng.model.entity.Category;
import com.sa.event_mng.model.entity.Event;
import com.sa.event_mng.model.entity.EventImage;
import com.sa.event_mng.model.entity.User;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventService {

        EventRepository eventRepository;
        CategoryRepository categoryRepository;
        UserRepository userRepository;
        EventMapper eventMapper;

        @Transactional
        @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
        public EventResponse create(EventRequest request) {
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
                                .description(request.getDescription())
                                .status(request.getStatus() != null ? request.getStatus() : EventStatus.DRAFT)
                                .build();

                if (request.getFiles() != null && !request.getFiles().isEmpty()) {
                        List<EventImage> images = saveImages(request.getFiles(), event);
                        event.setImages(images);
                }

                return eventMapper.toEventResponse(eventRepository.save(event));
        }

        public Page<EventResponse> getAllPublished(PageRequest pageRequest) {
                Page<Event> events = eventRepository.findByStatus(EventStatus.PUBLISHED, pageRequest);
            return events.map(eventMapper::toEventResponse);
        }

        public EventResponse getById(Long id) {
                Event event = eventRepository.findById(id)
                                .orElseThrow(() -> new AppException(ErrorCode.EVENT_NOT_FOUND));
                return eventMapper.toEventResponse(event);
        }

        @Transactional
        @PreAuthorize("hasRole('ADMIN') or (hasRole('ORGANIZER') and #id == authentication.principal.id)") // This logic
                                                                                                           // needs
                                                                                                           // adjustment
        public EventResponse update(Long id, EventRequest request) {
                Event event = eventRepository.findById(id)
                                .orElseThrow(() -> new AppException(ErrorCode.EVENT_NOT_FOUND));

                // Security check: Only owner or admin
                String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
                if (!event.getOrganizer().getUsername().equals(currentUsername) &&
                                !SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                                                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                        throw new AppException(ErrorCode.UNAUTHORIZED);
                }

                event.setName(request.getName());
                event.setLocation(request.getLocation());
                event.setStartTime(request.getStartTime());
                event.setEndTime(request.getEndTime());
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
                                String imageUrl = "http://localhost:8080/event-mng/uploads/" + filename;
                                images.add(EventImage.builder().imageUrl(imageUrl).event(event).build());
                        } catch (IOException e) {
                                throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
                        }
                }
                return images;
        }


}
