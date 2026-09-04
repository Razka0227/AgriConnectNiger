package ne.agriconnect.service;

import lombok.RequiredArgsConstructor;
import ne.agriconnect.domain.Notification;
import ne.agriconnect.domain.NotificationChannel;
import ne.agriconnect.domain.NotificationType;
import ne.agriconnect.domain.User;
import ne.agriconnect.exception.NotFoundException;
import ne.agriconnect.repository.NotificationRepository;
import ne.agriconnect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SmsService smsService;

    @Transactional
    public Notification notify(Long userId, String title, String message,
                               NotificationType type, NotificationChannel channel) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable"));
        Notification n = Notification.builder()
                .user(user).title(title).message(message).type(type).channel(channel).read(false)
                .build();
        notificationRepository.save(n);
        if (channel == NotificationChannel.SMS) {
            smsService.send(user.getPhone(), title + " : " + message);
        }
        return n;
    }

    @Transactional(readOnly = true)
    public List<Notification> list(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public long unreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Transactional
    public void markRead(Long userId, Long notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification introuvable"));
        if (!n.getUser().getId().equals(userId)) {
            throw new NotFoundException("Notification introuvable");
        }
        n.setRead(true);
    }

    @Transactional
    public void markAllRead(Long userId) {
        List<Notification> unread = notificationRepository.findByUserIdAndReadFalse(userId);
        unread.forEach(n -> n.setRead(true));
    }
}
