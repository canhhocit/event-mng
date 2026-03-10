# Event Management System - Project Overview

Hệ thống Quản lý Sự kiện và Bán vé đã được triển khai với đầy đủ các thành phần cơ bản theo mô hình Spring Boot 3.

## 📋 Những gì đã hoàn thành

### 1. Kiến trúc Hệ thống (Architecture)
- **Model-View-Controller**: Tách bạch logic xử lý, dữ liệu và giao diện API.
- **DTOs**: Sử dụng Data Transfer Objects để bảo mật và tối ưu hóa dữ liệu truyền tải qua API.
- **Mappers (MapStruct)**: Chuyển đổi tự động giữa Entity và DTO.
- **Global Exception Handling**: Quản lý lỗi tập trung, trả về định dạng JSON thống nhất (`ApiResponse`).
- **Timestamps**: Tự động lưu `created_at` và `updated_at` cho mọi bản ghi.

### 2. Chức năng chính theo Tác nhân (Actors & Features)

#### 👤 Khách hàng (Customer)
- **Đăng ký/Đăng nhập**: Hệ thống xác thực bằng JWT (Access Token & Refresh Token).
- **Xem sự kiện**: Tìm kiếm và xem thông tin chi tiết các sự kiện đang mở bán.
- **Giỏ hàng**: Thêm vé, cập nhật số lượng và quản lý vé trước khi thanh toán.
- **Đặt vé & Thanh toán**: Checkout giỏ hàng, hỗ trợ nhiều phương thức (Momo, VNPay, Chuyển khoản).
- **Vé điện tử**: Nhận mã vé (`ticket_code`) và mã QR ngay sau khi thanh toán thành công.

#### 🏢 Ban tổ chức (Organizer)
- **Quản lý sự kiện**: Tạo mới, chỉnh sửa và quản lý trạng thái sự kiện (Draft, Published).
- **Quản lý loại vé**: Thiết lập các hạng vé (VIP, Standard) với giá và số lượng riêng.
- **Check-in**: Sử dụng mã vé của khách để xác nhận tham gia sự kiện.
- **Thống kê**: Xem báo cáo tổng quan về doanh thu và số lượng vé bán ra.

#### 🛡️ Quản trị viên (Admin)
- **Quản lý người dùng**: Xem danh sách, khóa tài khoản hoặc phân quyền.
- **Giám sát**: Xem thống kê toàn hệ thống.

### 3. Quy trình hoạt động (Workflows)

1. **Mua vé**: 
   `Đăng ký` -> `Đăng nhập` -> `Chọn sự kiện` -> `Thêm vào Giỏ` -> `Checkout & Thanh toán` -> `Nhận Vé (QR)`.
2. **Quản lý & Check-in**:
   `Tạo sự kiện` -> `Đăng hạng vé` -> `Bắt đầu bán` -> `Quét QR Check-in`.


