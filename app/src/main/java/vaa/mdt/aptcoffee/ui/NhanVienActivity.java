package vaa.mdt.aptcoffee.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import vaa.mdt.aptcoffee.R;
import vaa.mdt.aptcoffee.adapter.NguoiDungAdapter;
import vaa.mdt.aptcoffee.dao.NguoiDungDAO;
import vaa.mdt.aptcoffee.interfaces.ItemNguoiDungOnClick;
import vaa.mdt.aptcoffee.model.NguoiDung;
import vaa.mdt.aptcoffee.utils.MyToast;

public class NhanVienActivity extends AppCompatActivity {
    public static final String MA_NGUOI_DUNG = "maNguoiDung";
    Toolbar toolBar;
    RecyclerView recyclerViewNhanVien;
    NguoiDungDAO nguoiDungDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nhan_vien);

        initToolBar();
        initView();
        nguoiDungDAO = new NguoiDungDAO(this);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        loadData();

    }

    private void initView() {
        recyclerViewNhanVien = findViewById(R.id.recyclerViewNhanVien);
    }

    private void initToolBar() {
        toolBar = findViewById(R.id.toolbarNhanVien);
        setSupportActionBar(toolBar);
    }

    private void loadData() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(NhanVienActivity.this, RecyclerView.VERTICAL, false);
        recyclerViewNhanVien.setLayoutManager(linearLayoutManager);
        NguoiDungAdapter nguoiDungAdapter = new NguoiDungAdapter(nguoiDungDAO.getAllPositionNhanVien(), new ItemNguoiDungOnClick() {
            @Override
            public void itemOclick(View view, NguoiDung nguoiDung) {
                PopupMenu popup = new PopupMenu(NhanVienActivity.this, view);
                popup.getMenuInflater()
                        .inflate(R.menu.menu_more_nhan_vien, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.menu_update) {
                            openUpdateNhanVienActivity(nguoiDung);
                        } else if (itemId == R.id.menu_delete) {
                            deleteNhanVien(nguoiDung);
                        } else if (itemId == R.id.menu_chitet) {
                            openChiTietNhanVienActivity(nguoiDung);
                        }
                        return true;
                    }
                });

                popup.show();

            }
        });
        recyclerViewNhanVien.setAdapter(nguoiDungAdapter);
    }

    private void openChiTietNhanVienActivity(NguoiDung nguoiDung) {
        Intent intent = new Intent(NhanVienActivity.this, ChiTietNhanVienActivity.class);
        intent.putExtra(MA_NGUOI_DUNG, nguoiDung.getMaNguoiDung());
        startActivity(intent);
        overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
    }

    private void openUpdateNhanVienActivity(NguoiDung nguoiDung) {
        Intent intent = new Intent(NhanVienActivity.this, CapNhatNhanVienActivity.class);
        intent.putExtra(MA_NGUOI_DUNG, nguoiDung.getMaNguoiDung());
        startActivity(intent);
        overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
    }

    private void deleteNhanVien(NguoiDung nguoiDung) {
        // Xoá nhân viên
        AlertDialog.Builder builder = new AlertDialog.Builder(NhanVienActivity.this, R.style.AlertDialogTheme);
        builder.setMessage("Xóa nhân viên " + nguoiDung.getHoVaTen() + "?");
        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (nguoiDungDAO.deleteNguoiDung(nguoiDung.getMaNguoiDung())) {
                    MyToast.successful(NhanVienActivity.this, "Xóa thành công");
                } else {
                    MyToast.error(NhanVienActivity.this, "Xóa không thành công");
                }
                loadData();
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_in_left, R.anim.anim_out_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_add) {
            startActivity(new Intent(NhanVienActivity.this, ThemNhanVienActivity.class));
            overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}