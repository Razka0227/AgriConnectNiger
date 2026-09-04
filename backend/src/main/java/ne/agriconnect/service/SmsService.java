package ne.agriconnect.service;

public interface SmsService {
    void send(String phone, String message);
}
