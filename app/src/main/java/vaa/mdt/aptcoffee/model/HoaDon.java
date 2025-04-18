package vaa.mdt.aptcoffee.model;

import androidx.annotation.NonNull;

import java.util.Date;

public class HoaDon {
    private int maHoaDon;
    private int maBan;
    private Date gioVao;
    private Date gioRa;
    private int trangThai;
    private String tenHangHoa;  // 🆕 Thêm thuộc tính này

    public static final int DA_THANH_TOAN = 1;
    public static final int CHUA_THANH_TOAN = 0;

    public HoaDon() {
    }

    public HoaDon(int maBan, Date gioVao, Date gioRa, int trangThai) {
        this.maBan = maBan;
        this.gioVao = gioVao;
        this.gioRa = gioRa;
        this.trangThai = trangThai;
    }

    public HoaDon(int maHoaDon, int maBan, Date gioVao, Date gioRa, int trangThai, String tenHangHoa) {
        this.maHoaDon = maHoaDon;
        this.maBan = maBan;
        this.gioVao = gioVao;
        this.gioRa = gioRa;
        this.trangThai = trangThai;
        this.tenHangHoa = tenHangHoa;  // 🆕 Gán giá trị
    }

    public int getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(int maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public int getMaBan() {
        return maBan;
    }

    public void setMaBan(int maBan) {
        this.maBan = maBan;
    }

    public Date getGioVao() {
        return gioVao;
    }

    public void setGioVao(Date gioVao) {
        this.gioVao = gioVao;
    }

    public Date getGioRa() {
        return gioRa;
    }

    public void setGioRa(Date gioRa) {
        this.gioRa = gioRa;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }

    public String getTenHangHoa() {  // 🆕 Thêm getter
        return tenHangHoa;
    }

    public void setTenHangHoa(String tenHangHoa) {  // 🆕 Thêm setter
        this.tenHangHoa = tenHangHoa;
    }

    @NonNull
    @Override
    public String toString() {
        return "HoaDon{" +
                "maHoaDon=" + maHoaDon +
                ", maBan=" + maBan +
                ", gioVao=" + gioVao +
                ", gioRa=" + gioRa +
                ", trangThai=" + trangThai +
                ", tenHangHoa='" + tenHangHoa + '\'' +  // 🆕 Cập nhật toString()
                '}';
    }
}

