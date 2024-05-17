package com.major_project.digital_library.constant;

public enum ReportReason {
    GIA_MAO("Giả mạo"),
    DOI_TRUY("Chứa nội dung đồi truỵ"),
    THU_DICH("Chứa nội dung thù địch, kích động, chia rẽ"),
    SPAM("Spam"),
    SAI_LECH("Thông tin sai lệch"),
    BAN_QUYEN("Vi phạm bản quyền"),
    HIEU_LAM("Nội dung gây hiểu lầm"),
    PHAN_DONG("Nội dung phản động, chống phá"),
    CHAT_LUONG_KEM("Chất lượng kém"),
    BAO_LUC("Chứa ảnh bạo lực, nguy hiểm"),
    THU_GHET("Ngôn từ gây thù ghét"),
    BAN_HANG("Bán hàng"),
    TIN_GIA("Tin giả"),
    XUC_PHAM("Bạo lực ngôn từ, xúc phạm"),
    QUAY_ROI("Quấy rối"),
    LUA_DAO("Lừa đảo"),
    KHAC("Khác");

    private final String reason;

    ReportReason(String reason) {
        this.reason = reason;
    }

    public String getMessage() {
        return reason;
    }
}
