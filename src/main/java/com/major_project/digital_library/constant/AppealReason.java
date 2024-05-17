package com.major_project.digital_library.constant;

public enum AppealReason {
    KHONG_CHINH_XAC("Quyết định không chính xác"),
    NOI_DUNG_HIEU_LAM("Nội dung bị hiểu lầm"),
    GIAO_DUC_THONG_TIN("Nội dung mang tính giáo dục hoặc thông tin"),
    BAO_CAO_SAI_LECH("Quyết định dựa trên báo cáo sai lệch"),
    KHONG_VI_PHAM("Bài viết không vi phạm tiêu chuẩn cộng đồng"),
    KHONG_MUC_DICH_GAY_HAI("Nội dung không nhằm mục đích gây hại"),
    HAI_HUOC_CHAM_BIEM("Nội dung mang tính hài hước hoặc châm biếm");

    private final String message;

    AppealReason(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

