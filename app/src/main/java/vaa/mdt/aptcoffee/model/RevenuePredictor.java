package vaa.mdt.aptcoffee.model;

import java.util.List;
import vaa.mdt.aptcoffee.dao.HoaDonChiTietDAO;

public class RevenuePredictor {

    /**
     * Dự đoán doanh thu sử dụng hồi quy tuyến tính
     * @param revenueDataList Danh sách dữ liệu doanh thu
     * @param futureDays Số ngày cần dự đoán trong tương lai
     * @return Doanh thu dự đoán
     */
    public static double predictLinearRegression(List<HoaDonChiTietDAO.RevenueData> revenueDataList, int futureDays) {
        if (revenueDataList == null || revenueDataList.isEmpty()) {
            return 0;
        }

        int n = revenueDataList.size();
        if (n < 3) {
            // Nếu không đủ dữ liệu, trả về trung bình các giá trị hiện có
            return calculateAverage(revenueDataList);
        }

        // Tính các tổng cần thiết cho công thức hồi quy
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;

        for (int i = 0; i < n; i++) {
            double x = i;
            double y = revenueDataList.get(i).revenue;
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }

        // Tính hệ số hồi quy (slope và intercept)
        double slope = calculateSlope(n, sumX, sumY, sumXY, sumX2);
        double intercept = calculateIntercept(n, sumX, sumY, slope);

        // Dự đoán cho futureDays ngày tiếp theo
        return slope * (n + futureDays - 1) + intercept;
    }

    /**
     * Dự đoán doanh thu sử dụng phương pháp trung bình động (Moving Average)
     * @param revenueDataList Danh sách dữ liệu doanh thu
     * @param windowSize Kích thước cửa sổ (số ngày để tính trung bình)
     * @return Doanh thu dự đoán
     */
    public static double predictMovingAverage(List<HoaDonChiTietDAO.RevenueData> revenueDataList, int windowSize) {
        if (revenueDataList == null || revenueDataList.isEmpty()) {
            return 0;
        }

        int n = revenueDataList.size();
        if (windowSize <= 0 || windowSize > n) {
            windowSize = Math.min(3, n); // Mặc định 3 nếu windowSize không hợp lệ
        }

        double sum = 0;
        for (int i = n - windowSize; i < n; i++) {
            sum += revenueDataList.get(i).revenue;
        }

        return sum / windowSize;
    }

    /**
     * Dự đoán doanh thu sử dụng phương pháp tốt nhất tự động lựa chọn
     * @param revenueDataList Danh sách dữ liệu doanh thu
     * @param futureDays Số ngày cần dự đoán
     * @return Doanh thu dự đoán
     */
    public static double predictBestFit(List<HoaDonChiTietDAO.RevenueData> revenueDataList, int futureDays) {
        if (revenueDataList == null || revenueDataList.size() < 5) {
            // Ít dữ liệu thì dùng moving average
            return predictMovingAverage(revenueDataList, 3);
        } else {
            // Nhiều dữ liệu thì dùng hồi quy tuyến tính
            return predictLinearRegression(revenueDataList, futureDays);
        }
    }

    // ===== CÁC PHƯƠNG THỨC HỖ TRỢ =====

    private static double calculateSlope(int n, double sumX, double sumY, double sumXY, double sumX2) {
        return (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
    }

    private static double calculateIntercept(int n, double sumX, double sumY, double slope) {
        return (sumY - slope * sumX) / n;
    }

    private static double calculateAverage(List<HoaDonChiTietDAO.RevenueData> revenueDataList) {
        return revenueDataList.stream()
                .mapToDouble(data -> data.revenue)
                .average()
                .orElse(0);
    }
}