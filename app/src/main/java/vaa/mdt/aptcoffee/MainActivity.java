package vaa.mdt.aptcoffee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

import vaa.mdt.aptcoffee.adapter.ViewPagerMainAdapter;
import vaa.mdt.aptcoffee.dao.ThongBaoDAO;
import vaa.mdt.aptcoffee.model.ThongBao;
import vaa.mdt.aptcoffee.ui.SignInActivity;

public class MainActivity extends AppCompatActivity {
    private String keyUser = ""; // Mã người dùng
    private ViewPager2 vp2Main;
    private BottomNavigationView bnvMain;
    private View iconNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initViewPager2Main();
        initBottomNavigation();
        setKeyUser();
        showIconNotification();
    }

    private void initView() {
        bnvMain = findViewById(R.id.bnvMain);
        vp2Main = findViewById(R.id.viewPager2Main);
    }

    private void initViewPager2Main() {
        ViewPagerMainAdapter adapter = new ViewPagerMainAdapter(this);
        vp2Main.setUserInputEnabled(false);
        vp2Main.setOffscreenPageLimit(3);
        vp2Main.setAdapter(adapter);
    }

    private void initBottomNavigation() {
        bnvMain.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_home) {
                vp2Main.setCurrentItem(0, false);
            } else if (itemId == R.id.menu_search) {
                vp2Main.setCurrentItem(1, false);
            } else if (itemId == R.id.menu_notification) {
                vp2Main.setCurrentItem(2, false);
            } else if (itemId == R.id.menu_setting) {
                vp2Main.setCurrentItem(3, false);
            }

            checkStatusNotification();
            return true;
        });
    }

    @SuppressLint("InflateParams")
    private void showIconNotification() {
        BottomNavigationItemView itemView = bnvMain.findViewById(R.id.menu_notification);
        if (itemView != null) {
            iconNotification = getLayoutInflater().inflate(R.layout.layout_ic_thongbao, null);
            checkStatusNotification();
            itemView.addView(iconNotification);
        }
    }

    private void checkStatusNotification() {
        ThongBaoDAO thongBaoDAO = new ThongBaoDAO(this);
        ArrayList<ThongBao> listNotification = thongBaoDAO.getByTrangThaiChuaXem();

        if (iconNotification != null) {
            iconNotification.setVisibility(listNotification.isEmpty() ? View.GONE : View.VISIBLE);
        }
    }

    private void setKeyUser() {
        Intent intent = getIntent();
        if (intent != null) {
            keyUser = intent.getStringExtra(SignInActivity.KEY_USER);
        }
        if (keyUser == null) {
            keyUser = "";
        }
    }

    public String getKeyUser() {
        return keyUser;
    }

    @Override
    public void onBackPressed() {
        vp2Main.setCurrentItem(0, false); // Chuyển về trang đầu tiên trong ViewPager2
        bnvMain.setSelectedItemId(R.id.menu_home); // Đặt BottomNavigationView về mục "Home"
        super.onBackPressed(); // Gọi phương thức onBackPressed() của lớp cha
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkStatusNotification();
    }
}

