package com.panic.security.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by david on 10/1/17.
 */

public class ImageConverter {
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        final int minSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, minSize, minSize);

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        final float cx = (float) (bitmap.getWidth() / 2.);
        final float cy = (float) (bitmap.getHeight() / 2.);
        final float radius = Math.min(cx, cy);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(cx, cy,radius, paint);//RoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode (new PorterDuffXfermode (PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap (bitmap, rect, rect, paint);

        return output;
    }
    public static Bitmap getMarkerBitmap(Bitmap bitmap) {
        Bitmap output = getRoundedCornerBitmap (bitmap);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        final float cx1 = 0;
        final float cx2 = bitmap.getWidth();
        final float cy = (float) (bitmap.getHeight());
        final float radius = Math.min((float)(bitmap.getHeight() / 2.), (float)(bitmap.getHeight() / 2.));

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        paint.setXfermode (new PorterDuffXfermode (PorterDuff.Mode.SRC_IN));
        float mx [] = {
                -1.0f,  0.0f,  0.0f,  1.0f,  0.0f,
                0.0f,  -1.0f,  0.0f,  1.0f,  0.0f,
                0.0f,  0.0f,  -1.0f,  1.0f,  0.0f,
                1.0f,  1.0f,  1.0f,  1.0f,  0.0f
        };
        ColorMatrix cm = new ColorMatrix(mx);

        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawCircle(cx1, cy,radius, paint);
        canvas.drawCircle(cx2, cy,radius, paint);
        canvas.drawBitmap (bitmap, rect, rect, paint);

        return output;
    }
}
