package vaa.mdt.aptcoffee.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.VideoView;

import vaa.mdt.aptcoffee.R;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    public static final int TIME_OUT = 4000;  // Thời gian delay là 4000ms (4 giây)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Khởi tạo VideoView để phát video
        VideoView videoView = findViewById(R.id.videoView);

        // Lấy URI của file video từ thư mục raw
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.manhinhchao);

        // Set URI vào VideoView
        videoView.setVideoURI(videoUri);

        // Chạy video khi sẵn sàng
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
            }
        });

        // Dừng video và chuyển sang SignInActivity sau 4 giây
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
                finish();  // Đóng SplashActivity sau khi chuyển màn hình
            }
        }, TIME_OUT);  // 4 giây
    }
}
