package cn.miracle.dev_miracle_music_android.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import cn.miracle.dev_miracle_music_android.R;

public class RequestPermissionActivity extends AppCompatActivity {

    private static final String TAG = "RequestPermissionActivity";

    private boolean isRequestPermissionRight = false;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_request_permission);

        checkAngPermission();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1:
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == 0) {
                        System.out.println("yes");
                        /* 授权时操作如下 */
                        isRequestPermissionRight = true;
                    } else {
                        /* 未授权时操作如下 */
                        isRequestPermissionRight = false;
                        Dialog mRequestPermissionDialog = new Dialog(this, R.style.CustomAlertDialog);
                        View view = View.inflate(this, R.layout.dialog_custom, null);

                        TextView tipTextView = view.findViewById(R.id.textview_tip);
                        RelativeLayout operationRelativeLayout = view.findViewById(R.id.relative_layout_operation);
                        TextView operationTextView = view.findViewById(R.id.textview_operation);

                        tipTextView.setTextSize(18);
                        tipTextView.setTextColor(getResources().getColor(R.color.black_70));
                        tipTextView.setTypeface(Typeface.DEFAULT);
                        tipTextView.setText("需要允许或授权才可使用");

                        GradientDrawable gradientDrawableOperationRelativeLayout = (GradientDrawable) operationRelativeLayout.getBackground();
                        gradientDrawableOperationRelativeLayout.setColor(ContextCompat.getColor(this, R.color.pinkFF708F));

                        operationTextView.setTextSize(15);
                        operationTextView.setTextColor(ContextCompat.getColor(this, R.color.white));
                        operationTextView.setTypeface(Typeface.DEFAULT);

                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                            // 选择拒绝
                            operationTextView.setText("去允许");
                            operationRelativeLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (mRequestPermissionDialog.isShowing()) {
                                        mRequestPermissionDialog.dismiss();
                                    }

                                    ActivityCompat.requestPermissions(RequestPermissionActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                                }
                            });

                        } else {
                            // 选择拒绝并勾选拒绝后不再询问
                            operationTextView.setText("去授权");
                            operationRelativeLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (mRequestPermissionDialog.isShowing()) {
                                        mRequestPermissionDialog.dismiss();
                                    }

                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivityForResult(intent, 2); // 调起应用设置页面
                                }
                            });

                        }

                        Window window = mRequestPermissionDialog.getWindow();
                        window.setGravity(Gravity.BOTTOM);
                        window.setContentView(view);
                        WindowManager.LayoutParams layoutParams = window.getAttributes();
                        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                        window.setAttributes(layoutParams);

                        mRequestPermissionDialog.setCanceledOnTouchOutside(false);
                        mRequestPermissionDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                                return i == KeyEvent.KEYCODE_BACK;
                            }
                        });
                        mRequestPermissionDialog.show();
                    }
                }

                break;
            default:
                break;
        }

        if (isRequestPermissionRight) {
            gotoAudioLocal();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            checkAngPermission();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public void checkAngPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            gotoAudioLocal();
        }
    }

    public void gotoAudioLocal() {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(RequestPermissionActivity.this, AudioLocalActivity.class);
        startActivity(intent);
        this.overridePendingTransition(R.anim.anim_alpha_in, R.anim.anim_alpha_out);
    }

}