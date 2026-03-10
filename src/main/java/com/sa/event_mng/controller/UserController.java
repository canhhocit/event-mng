package com.sa.event_mng.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sa.event_mng.dto.request.UserUpdateRequest;
import com.sa.event_mng.dto.response.ApiResponse;
import com.sa.event_mng.dto.response.UserResponse;
import com.sa.event_mng.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "Người dùng", description = "Hồ sơ và quản lý người dùng")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Lấy thông tin của chính mình", description = "Lấy thông tin hồ sơ của người dùng hiện đang đăng nhập")
    @GetMapping("/my-info")
    public ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }


    @Operation(summary = "Lấy tất cả người dùng", description = "Lấy danh sách tất cả các người dùng đang hoạt động (Chỉ ADMIN)")
    @GetMapping
    public ApiResponse<List<UserResponse>> getUsers() {
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .build();
    }

    @Operation(summary = "Lấy người dùng theo Username", description = "Lấy thông tin cụ thể của người dùng theo username")
    @GetMapping("/{username}")
    public ApiResponse<UserResponse> getUser(@PathVariable String username) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserByUsername(username))
                .build();
    }

    @Operation(summary = "Cập nhật người dùng", description = "Cập nhật thông tin hồ sơ của một người dùng cụ thể")
    @PutMapping("/{username}")
    public ApiResponse<UserResponse> updateUser(@PathVariable String username,
            @RequestBody @Valid UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(username, request))
                .build();
    }

    @Operation(summary = "Xóa người dùng", description = "Vô hiệu hóa tài khoản người dùng theo username(ADMIN)")
    @DeleteMapping("/{username}")
    public ApiResponse<String> deleteUser(@PathVariable String username) {
        return ApiResponse.<String>builder()
                .result(userService.deleteUser(username))
                .build();
    }
}