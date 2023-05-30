package cn.miracle.dev_miracle_music_android.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class CommonUtils {

    public static String formatDurationToTime(long duration) {
        if (duration / 1000 % 60 < 10) {
            return (duration / 1000 / 60) + ":0" + (duration / 1000 % 60);
        } else {
            return (duration / 1000 / 60) + ":" + (duration / 1000 % 60);
        }
    }

    public static Bitmap getRoundedCornerCustomDPBitmap(Bitmap sourceBitmap, float roundCustom) {
        try {
            Bitmap targetBitmap = Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(targetBitmap);
            Paint paint = new Paint();
            paint.setAntiAlias(true);

            Rect rect = new Rect(0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight());
            RectF rectF = new RectF(rect);

            canvas.drawARGB(0, 0, 0, 0);
            canvas.drawRoundRect(rectF, roundCustom, roundCustom, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(sourceBitmap, rect, rect, paint);

            return targetBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
