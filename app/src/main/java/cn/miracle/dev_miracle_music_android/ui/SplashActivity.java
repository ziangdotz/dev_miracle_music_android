package cn.miracle.dev_miracle_music_android.ui;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;

import cn.miracle.dev_miracle_music_android.R;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";

    private LottieAnimationView mainSplashLottieAnimationView;
    private TextView nameAppTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(layoutParams);
        }

        setContentView(R.layout.activity_splash);

        bindView();
        initView();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void bindView() {
        mainSplashLottieAnimationView = findViewById(R.id.lottie_animation_view_main_splash);
        nameAppTextView = findViewById(R.id.textview_name_app);
    }

    public void initView() {
        mainSplashLottieAnimationView.setAnimation("json/hello.json");
        mainSplashLottieAnimationView.setSpeed(6);
        mainSplashLottieAnimationView.playAnimation();
        mainSplashLottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(SplashActivity.this, RequestPermissionActivity.class);
                startActivity(intent);
                SplashActivity.this.overridePendingTransition(R.anim.anim_alpha_in, R.anim.anim_alpha_out);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        nameAppTextView.setTextSize(11);
        nameAppTextView.setTextColor(ContextCompat.getColor(this, R.color.gray808080));
        nameAppTextView.setTypeface(Typeface.DEFAULT);
        nameAppTextView.setMaxLines(1);
        nameAppTextView.setText(R.string.app_name);
    }

}
