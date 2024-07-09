***********Cài đặt cục bộ***********
***Yêu cầu
 - IDE: IntelliJ
 - Java 17+
 - MySQL

***Cài đặt
 - Chạy tệp database.sql tại thư mục dự án để khởi tạo cơ sở dữ liệu.
 - Mở dự án bằng IntelliJ.
 - Mở tệp application-dev.yaml trong thư mục dự án tại đường dẫn src/main/resources/application-dev.yaml. Thực hiện chỉnh sửa username và password thành thông tin đăng nhập của server MySQL trên máy.
 - Nhấn Run dự án bằng cấu hình Spring Boot mặc định của IntelliJ để khởi động server.
 - Truy cập API tại: http://localhost:8080/miniverse/api/v2/swagger-ui 

***********Cài đặt với docker***********
***Yêu cầu
 - Docker

***Cài đặt
 - Di chuyển đến thư mục dự án, mở terminal và chạy lệnh sau để xây dựng và chạy docker compose: docker compose -f docker-compose.yml up --build -d           
 - Kiểm tra trạng thái của containers bằng lệnh: docker-compose ps
 - Truy cập API tại: http://localhost:8080/miniverse/api/v2/swagger-ui 