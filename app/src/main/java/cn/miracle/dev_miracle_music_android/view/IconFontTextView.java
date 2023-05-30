package cn.miracle.dev_miracle_music_android.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class IconFontTextView extends TextView {

    public IconFontTextView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public IconFontTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public IconFontTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        Typeface iconFontTypeface = Typeface.createFromAsset(context.getAssets(), "font/iconfont.ttf");
        setTypeface(iconFontTypeface);
    }

}
