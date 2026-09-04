package ne.agriconnect.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ne.agriconnect.domain.User;
import ne.agriconnect.dto.OrderDto;
import ne.agriconnect.dto.OrderRequest;
import ne.agriconnect.dto.OrderStatusRequest;
import ne.agriconnect.service.OrderService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('BUYER','ADMIN')")
    public OrderDto create(@Valid @RequestBody OrderRequest request, @AuthenticationPrincipal User user) {
        return orderService.create(request, user.getId());
    }

    @GetMapping("/buyer")
    @PreAuthorize("hasAnyRole('BUYER','ADMIN')")
    public List<OrderDto> buyerOrders(@AuthenticationPrincipal User user) {
        return orderService.forBuyer(user.getId());
    }

    @GetMapping("/seller")
    @PreAuthorize("hasAnyRole('FARMER','ADMIN')")
    public List<OrderDto> sellerOrders(@AuthenticationPrincipal User user) {
        return orderService.forSeller(user.getId());
    }

    @GetMapping("/transporter")
    @PreAuthorize("hasAnyRole('TRANSPORTER','ADMIN')")
    public List<OrderDto> transporterOrders(@AuthenticationPrincipal User user) {
        return orderService.forTransporter(user.getId());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("isAuthenticated()")
    public OrderDto transition(@PathVariable Long id, @Valid @RequestBody OrderStatusRequest request,
                               @AuthenticationPrincipal User user) {
        return orderService.transition(id, request.status(), user.getId());
    }
}
