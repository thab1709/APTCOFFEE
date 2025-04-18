package vaa.mdt.aptcoffee.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import vaa.mdt.aptcoffee.database.CoffeeDB;
import vaa.mdt.aptcoffee.model.HoaDonChiTiet;
import vaa.mdt.aptcoffee.utils.XDate;

public class HoaDonChiTietDAO {
    private static final String TAG = "HoaDonChiTietDAO";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private final CoffeeDB coffeeDB;
    private final HangHoaDAO hangHoaDAO;

    public HoaDonChiTietDAO(Context context) {
        this.coffeeDB = new CoffeeDB(context);
        this.hangHoaDAO = new HangHoaDAO(context);
    }

    // Data classes
    public static class RevenueData {
        public final String date;
        public final double revenue;

        public RevenueData(String date, double revenue) {
            this.date = date;
            this.revenue = revenue;
        }
    }

    public static class ProductRevenue {
        public final int maHangHoa;
        public final String tenHangHoa;
        public final int soLuongBan;
        public final double doanhThu;

        public ProductRevenue(int maHangHoa, String tenHangHoa, int soLuongBan, double doanhThu) {
            this.maHangHoa = maHangHoa;
            this.tenHangHoa = tenHangHoa;
            this.soLuongBan = soLuongBan;
            this.doanhThu = doanhThu;
        }
    }

    // CRUD Operations
    public boolean insertHoaDonChiTiet(HoaDonChiTiet hoaDonChiTiet) {
        SQLiteDatabase db = coffeeDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("maHoaDon", hoaDonChiTiet.getMaHoaDon());
        values.put("maHangHoa", hoaDonChiTiet.getMaHangHoa());
        values.put("soLuong", hoaDonChiTiet.getSoLuong());
        values.put("giaTien", hoaDonChiTiet.getGiaTien());
        values.put("ghiChu", hoaDonChiTiet.getGhiChu());
        values.put("ngayXuatHoaDon", XDate.toStringDate(hoaDonChiTiet.getNgayXuatHoaDon()));

        long result = db.insert("HOADONCHITIET", null, values);
        return result != -1;
    }

    public boolean updateHoaDonChiTiet(HoaDonChiTiet hoaDonChiTiet) {
        SQLiteDatabase db = coffeeDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("maHoaDon", hoaDonChiTiet.getMaHoaDon());
        values.put("maHangHoa", hoaDonChiTiet.getMaHangHoa());
        values.put("soLuong", hoaDonChiTiet.getSoLuong());
        values.put("giaTien", hoaDonChiTiet.getGiaTien());
        values.put("ghiChu", hoaDonChiTiet.getGhiChu());
        values.put("ngayXuatHoaDon", XDate.toStringDate(hoaDonChiTiet.getNgayXuatHoaDon()));

        int affectedRows = db.update("HOADONCHITIET", values, "maHDCT=?",
                new String[]{String.valueOf(hoaDonChiTiet.getMaHDCT())});
        return affectedRows > 0;
    }

    public boolean deleteHoaDonChiTiet(String maHDCT) {
        SQLiteDatabase db = coffeeDB.getWritableDatabase();
        int affectedRows = db.delete("HOADONCHITIET", "maHDCT=?", new String[]{maHDCT});
        return affectedRows > 0;
    }

    public boolean deleteHoaDonChiTietByMaHoaDon(String maHoaDon) {
        SQLiteDatabase db = coffeeDB.getWritableDatabase();
        int affectedRows = db.delete("HOADONCHITIET", "maHoaDon=?", new String[]{maHoaDon});
        return affectedRows > 0;
    }

    // Query Methods
    @SuppressLint("Range")
    public ArrayList<HoaDonChiTiet> get(String sql, String... selectionArgs) {
        ArrayList<HoaDonChiTiet> list = new ArrayList<>();
        SQLiteDatabase db = coffeeDB.getReadableDatabase();

        try (Cursor cursor = db.rawQuery(sql, selectionArgs)) {
            while (cursor.moveToNext()) {
                HoaDonChiTiet hdct = new HoaDonChiTiet();
                hdct.setMaHDCT(cursor.getInt(cursor.getColumnIndex("maHDCT")));
                hdct.setMaHoaDon(cursor.getInt(cursor.getColumnIndex("maHoaDon")));
                hdct.setMaHangHoa(cursor.getInt(cursor.getColumnIndex("maHangHoa")));
                hdct.setSoLuong(cursor.getInt(cursor.getColumnIndex("soLuong")));
                hdct.setGiaTien(cursor.getInt(cursor.getColumnIndex("giaTien")));
                hdct.setGhiChu(cursor.getString(cursor.getColumnIndex("ghiChu")));

                try {
                    String dateStr = cursor.getString(cursor.getColumnIndex("ngayXuatHoaDon"));
                    hdct.setNgayXuatHoaDon(XDate.toDate(dateStr));
                } catch (ParseException e) {
                    Log.e(TAG, "Error parsing date", e);
                }

                int index = cursor.getColumnIndex("tenHangHoa");
                if (index != -1) {
                    hdct.setTenHangHoa(cursor.getString(index));
                }

                list.add(hdct);
            }
        }
        return list;
    }

    public ArrayList<HoaDonChiTiet> getAllHoaDonChiTiet() {
        String sql = "SELECT HCT.maHDCT, HCT.ngayXuatHoaDon, HCT.giaTien, HCT.maHangHoa, HCT.soLuong, HH.tenHangHoa " +
                "FROM HOADONCHITIET HCT " +
                "JOIN HANGHOA HH ON HCT.maHangHoa = HH.maHangHoa";
        return get(sql);
    }

    public HoaDonChiTiet getByMaHDCT(String maHDCT) {
        String sql = "SELECT * FROM HOADONCHITIET WHERE maHDCT=?";
        ArrayList<HoaDonChiTiet> list = get(sql, maHDCT);
        return list.isEmpty() ? null : list.get(0);
    }

    public ArrayList<HoaDonChiTiet> getByMaHoaDon(String maHoaDon) {
        String sql = "SELECT * FROM HOADONCHITIET WHERE maHoaDon=?";
        return get(sql, maHoaDon);
    }

    // Revenue Methods
    public List<RevenueData> getRevenueLast7Days() {
        Calendar calendar = Calendar.getInstance();
        String endDate = XDate.toStringDate(calendar.getTime());

        calendar.add(Calendar.DAY_OF_YEAR, -6);
        String startDate = XDate.toStringDate(calendar.getTime());

        return getRevenueByDateRange(startDate, endDate);
    }

    public List<RevenueData> getRevenueLast4Weeks() {
        Calendar calendar = Calendar.getInstance();
        String endDate = XDate.toStringDate(calendar.getTime());

        calendar.add(Calendar.WEEK_OF_YEAR, -3);
        String startDate = XDate.toStringDate(calendar.getTime());

        return getRevenueByDateRange(startDate, endDate);
    }

    public List<RevenueData> getRevenueLast6Months() {
        Calendar calendar = Calendar.getInstance();
        String endDate = XDate.toStringDate(calendar.getTime());

        calendar.add(Calendar.MONTH, -5);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String startDate = XDate.toStringDate(calendar.getTime());

        return getRevenueByDateRange(startDate, endDate);
    }

    public List<RevenueData> getRevenueByDateRange(String startDate, String endDate) {
        List<RevenueData> result = new ArrayList<>();
        SQLiteDatabase db = coffeeDB.getReadableDatabase();

        String query = "SELECT ngayXuatHoaDon, SUM(giaTien) AS revenue " +
                "FROM HOADONCHITIET " +
                "WHERE ngayXuatHoaDon BETWEEN ? AND ? " +
                "GROUP BY ngayXuatHoaDon " +
                "ORDER BY ngayXuatHoaDon ASC";

        try (Cursor cursor = db.rawQuery(query, new String[]{startDate, endDate})) {
            while (cursor.moveToNext()) {
                result.add(new RevenueData(
                        cursor.getString(0),
                        cursor.getDouble(1)
                ));
            }
        }
        return result;
    }

    public List<RevenueData> getRevenueForWeek(String startDate) {
        try {
            Date date = XDate.toDate(startDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DAY_OF_YEAR, 6); // Add 6 days to get a week
            String endDate = XDate.toStringDate(cal.getTime());

            return getRevenueByDateRange(startDate, endDate);
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing date", e);
            return new ArrayList<>();
        }
    }

    public List<RevenueData> getRevenueForMonth(String monthYear) {
        try {
            Date date = DATE_FORMAT.parse("01/" + monthYear);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            String startDate = XDate.toStringDate(cal.getTime());
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            String endDate = XDate.toStringDate(cal.getTime());

            return getRevenueByDateRange(startDate, endDate);
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing date", e);
            return new ArrayList<>();
        }
    }

    // Transaction Data Methods
    public Map<String, List<String>> getTransactionData() {
        Map<String, List<String>> transactions = new HashMap<>();
        SQLiteDatabase db = coffeeDB.getReadableDatabase();

        try (Cursor cursor = db.rawQuery("SELECT maHoaDon, maHangHoa FROM HoaDonChiTiet", null)) {
            while (cursor.moveToNext()) {
                String maHoaDon = cursor.getString(0);
                String maHangHoa = cursor.getString(1);

                transactions.computeIfAbsent(maHoaDon, k -> new ArrayList<>()).add(maHangHoa);
            }
        }
        return transactions;
    }

    public Map<String, List<String>> getTransactionDataWithProductNames() {
        Map<String, List<String>> transactions = new HashMap<>();
        SQLiteDatabase db = coffeeDB.getReadableDatabase();

        String query = "SELECT HCT.maHoaDon, HH.tenHangHoa " +
                "FROM HOADONCHITIET HCT " +
                "JOIN HANGHOA HH ON HCT.maHangHoa = HH.maHangHoa";

        try (Cursor cursor = db.rawQuery(query, null)) {
            while (cursor.moveToNext()) {
                String maHoaDon = cursor.getString(0);
                String tenHangHoa = cursor.getString(1);

                transactions.computeIfAbsent(maHoaDon, k -> new ArrayList<>()).add(tenHangHoa);
            }
        }
        return transactions;
    }

    // Statistics Methods
    public List<ProductRevenue> getTopProducts(int limit) {
        List<ProductRevenue> result = new ArrayList<>();
        SQLiteDatabase db = coffeeDB.getReadableDatabase();

        String query = "SELECT HH.maHangHoa, HH.tenHangHoa, SUM(HCT.soLuong) as totalSold, SUM(HCT.giaTien) as totalRevenue " +
                "FROM HOADONCHITIET HCT " +
                "JOIN HANGHOA HH ON HCT.maHangHoa = HH.maHangHoa " +
                "GROUP BY HH.maHangHoa, HH.tenHangHoa " +
                "ORDER BY totalSold DESC " +
                "LIMIT ?";

        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(limit)})) {
            while (cursor.moveToNext()) {
                result.add(new ProductRevenue(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getDouble(3)
                ));
            }
        }
        return result;
    }

    @SuppressLint("Range")
    public int getGiaTien(int maHoaDon) {
        SQLiteDatabase db = coffeeDB.getReadableDatabase();
        String sql = "SELECT SUM(giaTien) as DoanhThu FROM HOADONCHITIET WHERE maHoaDon=?";

        try (Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(maHoaDon)})) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndex("DoanhThu"));
            }
        }
        return 0;
    }

    @SuppressLint("Range")
    public int getDTThangNam(String tuNgay, String denNgay) {
        SQLiteDatabase db = coffeeDB.getReadableDatabase();
        String sql = "SELECT SUM(giaTien) as doanhThu FROM HOADONCHITIET WHERE ngayXuatHoaDon BETWEEN ? AND ?";

        try (Cursor cursor = db.rawQuery(sql, new String[]{tuNgay, denNgay})) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndex("doanhThu"));
            }
        }
        return 0;
    }

    @SuppressLint("Range")
    public int getDoanhThuNgay(String date) {
        SQLiteDatabase db = coffeeDB.getReadableDatabase();
        String query = "SELECT SUM(giaTien) FROM HOADONCHITIET WHERE ngayXuatHoaDon=?";

        try (Cursor cursor = db.rawQuery(query, new String[]{date})) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }
        return 0;
    }

    @SuppressLint("Range")
    public String getTenHangHoa(int maHoaDon) {
        SQLiteDatabase db = coffeeDB.getReadableDatabase();
        String sql = "SELECT HH.tenHangHoa FROM HOADONCHITIET HCT " +
                "JOIN HANGHOA HH ON HCT.maHangHoa = HH.maHangHoa " +
                "WHERE HCT.maHoaDon = ?";

        try (Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(maHoaDon)})) {
            if (cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex("tenHangHoa"));
            }
        }
        return "Không xác định";
    }

    public int getTotalRevenueByDateRange(String startDate, String endDate) {
        SQLiteDatabase db = coffeeDB.getReadableDatabase();
        String query = "SELECT SUM(giaTien) FROM HOADONCHITIET WHERE ngayXuatHoaDon BETWEEN ? AND ?";

        try (Cursor cursor = db.rawQuery(query, new String[]{startDate, endDate})) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }
        return 0;
    }

    public boolean existsByMaHoaDon(int maHoaDon) {
        SQLiteDatabase db = coffeeDB.getReadableDatabase();
        String query = "SELECT 1 FROM HOADONCHITIET WHERE maHoaDon=? LIMIT 1";

        try (Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(maHoaDon)})) {
            return cursor.moveToFirst();
        }
    }
}




