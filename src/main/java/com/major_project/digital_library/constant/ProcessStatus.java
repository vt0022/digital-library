package com.major_project.digital_library.constant;

public enum ProcessStatus {
    PENDING("Đang chờ"),
    REVIEWED("Đã xem"),
    DISABLED("Đã gỡ nội dung"),
    DELETED("Đã xoá nội dung"),
    RESTORED("Đã khôi phục nội dung"),
    REMAIN("Giữ nguyên quyết định");

    private final String message;

    ProcessStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
