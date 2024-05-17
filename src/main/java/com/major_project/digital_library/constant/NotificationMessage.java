package com.major_project.digital_library.constant;

public enum NotificationMessage {
    WELCOME("Chào mừng bạn đến với diễn đàn"),
    REWARD_BADGE("Bạn đã nhận được huy hiệu"),
    REPLY("đã bình luận về bài đăng của bạn"),
    LIKE_REPLY("đã thích bình luận của bạn"),
    LIKE_POST("đã thích bài đăng của bạn"),
    BIRTHDAY("Chúc mừng sinh nhật bạn"),
    NEW_POST("đã đăng một bài viết mới"),
    NOTIFY_MEMBER("đã nhắc nhở thành viên"),
    WARN_POST("Bài đăng của bạn đã bị gỡ do vi phạm tiêu chuẩn cộng đồng về"),
    WARN_REPLY("Bình luận của bạn đã bị gỡ do vi phạm tiêu chuẩn cộng đồng về"),
    DELETE_POST("Bài đăng của bạn đã bị xoá vì vi phạm tiêu chuẩn cộng đồng về"),
    DELETE_REPLY("Bình luận của bạn đã bị xoá vì vi phạm tiêu chuẩn cộng đồng về"),
    RESTORE_POST("Bài đăng của bạn đã được khôi phục. Rất xin lỗi vì sự nhầm lẫn."),
    RESTORE_REPLY("Bình luận của bạn đã được khôi phục. Rất xin lỗi vì sự nhầm lẫn.");

    private final String message;

    NotificationMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
