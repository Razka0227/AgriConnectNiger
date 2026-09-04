package ne.agriconnect.controller;

import lombok.RequiredArgsConstructor;
import ne.agriconnect.domain.User;
import ne.agriconnect.dto.NotificationDto;
import ne.agriconnect.service.DtoMapper;
import ne.agriconnect.service.NotificationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final DtoMapper mapper;

    @GetMapping
    public List<NotificationDto> list(@AuthenticationPrincipal User user) {
        return notificationService.list(user.getId()).stream().map(mapper::notificationToDto).toList();
    }

    @GetMapping("/unread-count")
    public Map<String, Long> unreadCount(@AuthenticationPrincipal User user) {
        return Map.of("count", notificationService.unreadCount(user.getId()));
    }

    @PatchMapping("/{id}/read")
    public void markRead(@PathVariable Long id, @AuthenticationPrincipal User user) {
        notificationService.markRead(user.getId(), id);
    }

    @PatchMapping("/read-all")
    public void markAllRead(@AuthenticationPrincipal User user) {
        notificationService.markAllRead(user.getId());
    }
}
