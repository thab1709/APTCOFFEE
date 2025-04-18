package vaa.mdt.aptcoffee.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import vaa.mdt.aptcoffee.MainActivity;
import vaa.mdt.aptcoffee.R;
import vaa.mdt.aptcoffee.dao.NguoiDungDAO;
import vaa.mdt.aptcoffee.utils.Loading;
import vaa.mdt.aptcoffee.utils.MyToast;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient mGoogleSignInClient;
    private ImageView ivSignInGG;
    private Loading loading;
    private TextInputLayout tilUserName, tilPassword;
    private Button btnSignIn;
    private TextView tvSignUp;
    private NguoiDungDAO nguoiDungDAO;
    public static final String KEY_USER = "USER_EMAIL";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        ivSignInGG = findViewById(R.id.ivSignInGG);
        ivSignInGG.setOnClickListener(this);

        initGoogleSignIn();
        initView();
        nguoiDungDAO = new NguoiDungDAO(SignInActivity.this);
        loading = new Loading(SignInActivity.this);

        btnSignIn.setOnClickListener(this);
        tvSignUp.setOnClickListener(this);

        checkExistingSignIn();
    }

    private void initGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void checkExistingSignIn() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            navigateToMainActivity(account.getEmail(), account.getDisplayName());
        }
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    MyToast.successful(this, "Đăng nhập thành công với Google: " + account.getDisplayName());
                    navigateToMainActivity(account.getEmail(), account.getDisplayName());
                }
            } catch (ApiException e) {
                Log.e("SignInActivity", "Google Sign-In failed: " + e.getStatusCode());
                MyToast.error(this, "Đăng nhập Google thất bại.");
            }
        }
    }

    private void initView() {
        tilUserName = findViewById(R.id.tilUserName);
        tilPassword = findViewById(R.id.tilPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvSignUp = findViewById(R.id.tvSignUp);
    }

    private void loginSystem() {
        loading.startLoading();
        String username = getText(tilUserName);
        String password = getText(tilPassword);

        if (username.isEmpty() || password.isEmpty()) {
            MyToast.error(this, "Không được để trống tài khoản hoặc mật khẩu");
            loading.stopLoading();
            return;
        }

        if (nguoiDungDAO.checkLogin(username, password)) {
            MyToast.successful(this, "Đăng nhập thành công");
            navigateToMainActivity(username, "User");
        } else {
            MyToast.error(this, "Sai tài khoản hoặc mật khẩu");
            loading.stopLoading();
        }
    }

    private void navigateToMainActivity(String email, String name) {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        intent.putExtra("USER_EMAIL", email);
        intent.putExtra("USER_NAME", name);
        startActivity(intent);
        finish();
    }

    @NonNull
    private String getText(TextInputLayout textInputLayout) {
        return Objects.requireNonNull(textInputLayout.getEditText()).getText().toString().trim();
    }

    @Override

    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.ivSignInGG) {
            signInWithGoogle();
        } else if (viewId == R.id.btnSignIn) {
            loginSystem();
        } else if (viewId == R.id.tvSignUp) {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
        }
    }
}

