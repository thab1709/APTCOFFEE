package vaa.mdt.aptcoffee.model;

import androidx.annotation.NonNull;

import java.util.Date;

public class HoaDonChiTiet {
    private int maHDCT;
    private int maHoaDon;
    private int maHangHoa;
    private int soLuong;
    private int giaTien;
    private String ghiChu;
    private Date ngayXuatHoaDon;
    private String tenHangHoa;

    public HoaDonChiTiet() {
    }

    public HoaDonChiTiet(int maHoaDon, int maHangHoa, int soLuong, int giaTien, String ghiChu, Date ngayXuatHoaDon, String tenHangHoa) {
        this.maHoaDon = maHoaDon;
        this.maHangHoa = maHangHoa;
        this.soLuong = soLuong;
        this.giaTien = giaTien;
        this.ghiChu = ghiChu;
        this.ngayXuatHoaDon = ngayXuatHoaDon;
        this.tenHangHoa = tenHangHoa;
    }

    public HoaDonChiTiet(int maHDCT, int maHoaDon, int maHangHoa, int soLuong, int giaTien, String ghiChu, Date ngayXuatHoaDon, String tenHangHoa) {
        this.maHDCT = maHDCT;
        this.maHoaDon = maHoaDon;
        this.maHangHoa = maHangHoa;
        this.soLuong = soLuong;
        this.giaTien = giaTien;
        this.ghiChu = ghiChu;
        this.ngayXuatHoaDon = ngayXuatHoaDon;
        this.tenHangHoa = tenHangHoa;

    }

    public int getMaHDCT() {
        return maHDCT;
    }

    public void setMaHDCT(int maHDCT) {
        this.maHDCT = maHDCT;
    }

    public int getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(int maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public int getMaHangHoa() {
        return maHangHoa;
    }

    public void setMaHangHoa(int maHangHoa) {
        this.maHangHoa = maHangHoa;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public int getGiaTien() {
        return giaTien;
    }

    public void setGiaTien(int giaTien) {
        this.giaTien = giaTien;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public Date getNgayXuatHoaDon() {
        return ngayXuatHoaDon;
    }

    public void setNgayXuatHoaDon(Date ngayXuatHoaDon) {
        this.ngayXuatHoaDon = ngayXuatHoaDon;
    }
    public String getTenHangHoa() {
        return tenHangHoa;
    }
    public void setTenHangHoa(String tenHangHoa) {
        this.tenHangHoa = tenHangHoa;
    }
    @NonNull
    @Override
    public String toString() {
        return "HoaDonChiTiet{" +
                "maHDCT=" + maHDCT +
                ", maHoaDon=" + maHoaDon +
                ", maHangHoa=" + maHangHoa +
                ", giaTien=" + giaTien +
                ", ghiChu='" + ghiChu + '\'' +
                ", ngayXuatHoaDon=" + ngayXuatHoaDon +
                '}'+
                "tenHangHoa='" + tenHangHoa + '\'';
    }
}
