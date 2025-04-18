package vaa.mdt.aptcoffee.interfaces;

import android.view.View;

import vaa.mdt.aptcoffee.model.HangHoa;
import vaa.mdt.aptcoffee.model.HoaDonChiTiet;

public interface ItemTangGiamSoLuongOnClick {
    void itemOclick(View view, int indext, HoaDonChiTiet hoaDonChiTiet, HangHoa hangHoa);
    void itemOclickDeleteHDCT(View view, HoaDonChiTiet hoaDonChiTiet);
}
