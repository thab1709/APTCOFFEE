package vaa.mdt.aptcoffee.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import vaa.mdt.aptcoffee.R;
import vaa.mdt.aptcoffee.adapter.ChiTietHoaDonAdapter;
import vaa.mdt.aptcoffee.dao.HangHoaDAO;
import vaa.mdt.aptcoffee.dao.HoaDonChiTietDAO;
import vaa.mdt.aptcoffee.dao.HoaDonDAO;
import vaa.mdt.aptcoffee.model.HangHoa;
import vaa.mdt.aptcoffee.model.HoaDon;
import vaa.mdt.aptcoffee.model.HoaDonChiTiet;
import vaa.mdt.aptcoffee.utils.MyToast;
import vaa.mdt.aptcoffee.utils.XDate;

public class ChiTietHoaDonActivity extends AppCompatActivity {
    private RecyclerView recyclerViewThucUong;
    private HoaDonChiTietDAO hoaDonChiTietDAO;
    private HangHoaDAO hangHoaDAO;
    private HoaDonDAO hoaDonDAO;
    private TextView tvMaBan, tvGioVao, tvGioRa;
    private ImageView ivBack;
    private Button btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_hoa_don);

        initView();
        initDAO();

        loadData();
        fillActivity();

        ivBack.setOnClickListener(view -> onBackPressed());

        btnDelete.setOnClickListener(view -> showConfirmDeleteHDCT());
    }

    private void initDAO() {
        hoaDonChiTietDAO = new HoaDonChiTietDAO(this);
        hangHoaDAO = new HangHoaDAO(this);
        hoaDonDAO = new HoaDonDAO(this);
    }

    private void showConfirmDeleteHDCT() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setMessage("Bạn có muốn xoá hoá đơn " + getMaHoaDon() + "?")
                .setPositiveButton("Xoá", (dialogInterface, i) -> {
                    if (hoaDonDAO.deleteHoaDon(getMaHoaDon()) && hoaDonChiTietDAO.deleteHoaDonChiTietByMaHoaDon(getMaHoaDon())) {
                        MyToast.successful(this, "Xoá thành công");
                        onBackPressed();
                    } else {
                        MyToast.error(this, "Xoá không thành công");
                    }
                })
                .setNegativeButton("Huỷ", (dialogInterface, i) -> {});
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void fillActivity() {
        HoaDon hoaDon = hoaDonDAO.getByMaHoaDon(getMaHoaDon());
        if (hoaDon != null) {
            tvMaBan.setText("B0" + hoaDon.getMaBan());
            tvGioVao.setText(XDate.toStringDateTime(hoaDon.getGioVao()));
            tvGioRa.setText(XDate.toStringDateTime(hoaDon.getGioRa()));
        }
    }

    private String getMaHoaDon() {
        return getIntent().getStringExtra(HoaDonActivity.MA_HOA_DON);
    }

    private void loadData() {
        String maHoaDon = getMaHoaDon();
        ArrayList<HoaDonChiTiet> listHDCT = hoaDonChiTietDAO.getByMaHoaDon(maHoaDon);
        ArrayList<HangHoa> list = new ArrayList<>();

        for (HoaDonChiTiet hdct : listHDCT) {
            HangHoa hangHoa = hangHoaDAO.getByMaHangHoa(String.valueOf(hdct.getMaHangHoa()));
            if (hangHoa != null) {
                list.add(hangHoa);
            }
        }

        recyclerViewThucUong.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewThucUong.setAdapter(new ChiTietHoaDonAdapter(this, list, listHDCT));
    }

    private void initView() {
        recyclerViewThucUong = findViewById(R.id.recyclerViewThucUong);
        tvMaBan = findViewById(R.id.tvMaBan);
        tvGioVao = findViewById(R.id.tvGioVao);
        tvGioRa = findViewById(R.id.tvGioRa);
        ivBack = findViewById(R.id.ivBack);
        btnDelete = findViewById(R.id.btnDelete);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_in_left, R.anim.anim_out_right);
    }
}