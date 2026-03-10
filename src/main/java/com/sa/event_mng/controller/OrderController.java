package com.sa.event_mng.controller;

import com.sa.event_mng.dto.response.ApiResponse;
import com.sa.event_mng.dto.response.OrderResponse;
import com.sa.event_mng.model.enums.PaymentMethod;
import com.sa.event_mng.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Đơn hàng", description = "Thanh toán và xem lịch sử mua vé")
public class OrderController {

    OrderService orderService;

    @PostMapping("/checkout")
    @Operation(summary = "Thanh toán giỏ hàng")
    public ApiResponse<OrderResponse> checkout(@RequestParam PaymentMethod paymentMethod) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.checkout(paymentMethod))
                .build();
    }

    @GetMapping
    @Operation(summary = "Xem lịch sử đơn hàng của tôi")
    public ApiResponse<List<OrderResponse>> getMyOrders() {
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orderService.getMyOrders())
                .build();
    }
}
