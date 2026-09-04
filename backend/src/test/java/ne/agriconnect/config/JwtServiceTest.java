package ne.agriconnect.config;

import ne.agriconnect.domain.Role;
import ne.agriconnect.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        String secret = "01234567890123456789012345678901"; // 32 chars for HMAC-SHA key
        long expirationMs = 3600000L;
        jwtService = new JwtService(secret, expirationMs);
    }

    @Test
    void generateToken_shouldIncludePhoneAndUserIdAndRole() {
        User user = new User();
        user.setId(123L);
        user.setPhone("+22712345678");
        user.setRole(Role.FARMER);
        user.setName("Test Farmer");

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals(user.getPhone(), jwtService.extractPhone(token));
    }

    @Test
    void parse_shouldReturnClaimsWithCustomProperties() {
        User user = new User();
        user.setId(321L);
        user.setPhone("+22787654321");
        user.setRole(Role.BUYER);

        String token = jwtService.generateToken(user);
        var claims = jwtService.parse(token);

        assertEquals(user.getPhone(), claims.getSubject());
        assertEquals(321, claims.get("userId", Integer.class).intValue());
        assertEquals(Role.BUYER.name(), claims.get("role", String.class));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(claims.getIssuedAt()));
    }
}
