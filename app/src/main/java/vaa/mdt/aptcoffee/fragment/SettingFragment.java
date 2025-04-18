package vaa.mdt.aptcoffee.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.io.InputStream;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import vaa.mdt.aptcoffee.MainActivity;
import vaa.mdt.aptcoffee.R;
import vaa.mdt.aptcoffee.dao.NguoiDungDAO;
import vaa.mdt.aptcoffee.model.NguoiDung;
import vaa.mdt.aptcoffee.ui.DoiMatKhauActivity;
import vaa.mdt.aptcoffee.ui.LienHeActivity;
import vaa.mdt.aptcoffee.ui.SignInActivity;
import vaa.mdt.aptcoffee.ui.ThietLapTaiKhoanActivity;
import vaa.mdt.aptcoffee.utils.ImageToByte;
import vaa.mdt.aptcoffee.utils.MyToast;
import pl.droidsonroids.gif.GifImageView;

public class SettingFragment extends Fragment implements View.OnClickListener {

    private static final int PICK_IMAGE = 1;
    public static final String MA_NGUOIDUNG = "MA_NGUOIDUNG";

    private TextView tvDanhGia, tvLienHe, tvThietLapTaiKhoan, tvDoiMatKhau, tvDangXuat, tvTenNguoiDung, tvChucVu, tvEmail;
    private CircleImageView civHinhAnh;
    private ImageView ivDoiHinhAnh;

    private MainActivity mainActivity;
    private NguoiDungDAO nguoiDungDAO;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        initView(view);

        mainActivity = (MainActivity) getActivity();
        nguoiDungDAO = new NguoiDungDAO(getContext());

        fillActivity();

        tvDanhGia.setOnClickListener(this);
        tvLienHe.setOnClickListener(this);
        tvThietLapTaiKhoan.setOnClickListener(this);
        tvDoiMatKhau.setOnClickListener(this);
        tvDangXuat.setOnClickListener(this);
        ivDoiHinhAnh.setOnClickListener(this);

        return view;
    }

    private void initView(View view) {
        tvDanhGia = view.findViewById(R.id.tvDanhGia);
        tvLienHe = view.findViewById(R.id.tvLienHe);
        tvThietLapTaiKhoan = view.findViewById(R.id.tvThietLapTaiKhoan);
        tvDoiMatKhau = view.findViewById(R.id.tvDoiMatKhau);
        tvDangXuat = view.findViewById(R.id.tvDangXuat);
        tvTenNguoiDung = view.findViewById(R.id.tvTenNguoiDung);
        tvChucVu = view.findViewById(R.id.tvChucVu);
        tvEmail = view.findViewById(R.id.tvEmail);
        civHinhAnh = view.findViewById(R.id.civHinhAnh);
        ivDoiHinhAnh = view.findViewById(R.id.ivDoiHinhAnh);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.ivDoiHinhAnh) {
            requestPermissionPickImage();
        } else if (viewId == R.id.tvDanhGia) {
            showRatingDialog();
        } else if (viewId == R.id.tvLienHe) {
            openActivity(LienHeActivity.class);
        } else if (viewId == R.id.tvThietLapTaiKhoan) {
            openThietLapTaiKhoanActivity();
        } else if (viewId == R.id.tvDoiMatKhau) {
            openDoiMatKhauActivity();
        } else if (viewId == R.id.tvDangXuat) {
            logout();
        }
    }

    private void fillActivity() {
        NguoiDung nguoiDung = getNguoiDung();
        if (nguoiDung != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(nguoiDung.getHinhAnh(), 0, nguoiDung.getHinhAnh().length);
            civHinhAnh.setImageBitmap(bitmap);
            tvTenNguoiDung.setText(nguoiDung.getHoVaTen());
            tvChucVu.setText(nguoiDung.getChucVu());
            tvEmail.setText(nguoiDung.getEmail());
        }
    }

    private NguoiDung getNguoiDung() {
        String maNguoiDung = mainActivity != null ? mainActivity.getKeyUser() : null;
        return maNguoiDung != null ? nguoiDungDAO.getByMaNguoiDung(maNguoiDung) : null;
    }

    private void requestPermissionPickImage() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_IMAGE);
        } else {
            openImagePicker();
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    private void updateAvatarUser() {
        NguoiDung nguoiDung = getNguoiDung();
        if (nguoiDung != null) {
            nguoiDung.setHinhAnh(ImageToByte.circleImageViewToByte(getContext(), civHinhAnh));
            if (nguoiDungDAO.updateNguoiDung(nguoiDung)) {
                MyToast.successful(getContext(), "Cập nhật ảnh đại diện thành công");
                fillActivity();
            } else {
                MyToast.error(getContext(), "Lỗi");
            }
        }
    }

    private void logout() {
        AlertDialog dialog = new AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
                .setMessage("Bạn có muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", null) // Đặt null trước
                .setNegativeButton("Huỷ", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.Brow1Primary));
            positiveButton.setOnClickListener(v -> {
                signOutGoogleAccount();
                openActivity(SignInActivity.class);
            });

            Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            negativeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.BrowPrimary));
        });

        dialog.show();
    }
    private void signOutGoogleAccount() {
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(requireContext(), GoogleSignInOptions.DEFAULT_SIGN_IN);
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                MyToast.successful(getContext(), "Đã đăng xuất tài khoản Google");
            } else {
                MyToast.error(getContext(), "Đăng xuất tài khoản Google thất bại");
            }
        });
    }


    private void openActivity(Class<?> activityClass) {
        Intent intent = new Intent(getContext(), activityClass);
        startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_left);
    }

    private void openThietLapTaiKhoanActivity() {
        Intent intent = new Intent(getContext(), ThietLapTaiKhoanActivity.class);
        intent.putExtra(MA_NGUOIDUNG, getNguoiDung().getMaNguoiDung());
        startActivity(intent);
    }

    private void openDoiMatKhauActivity() {
        Intent intent = new Intent(getContext(), DoiMatKhauActivity.class);
        intent.putExtra(MA_NGUOIDUNG, getNguoiDung().getMaNguoiDung());
        startActivity(intent);
    }

    private void showRatingDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.layout_danh_gia);

        RatingBar ratingBar = dialog.findViewById(R.id.rtbDanhGia);
        GifImageView gif = dialog.findViewById(R.id.imgGif);
        Button btnDanhGia = dialog.findViewById(R.id.btnDanhGia);
        TextView tvBoQua = dialog.findViewById(R.id.tvBoQua);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout((int) (getResources().getDisplayMetrics().widthPixels * 0.90), WindowManager.LayoutParams.WRAP_CONTENT);

        tvBoQua.setOnClickListener(v -> dialog.dismiss());
        ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) -> gif.setImageResource(rating <= 3 ? R.drawable.git_sad : R.drawable.gif_danh_gia));
        btnDanhGia.setOnClickListener(v -> {
            MyToast.successful(getContext(), "Đã đánh giá " + ratingBar.getRating() + " sao");
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            try (InputStream stream = requireContext().getContentResolver().openInputStream(data.getData())) {
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                civHinhAnh.setImageBitmap(bitmap);
                updateAvatarUser();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
