package com.panic.security.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by david on 10/1/17.
 */

public class ImageConverter {
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        if(bitmap == null) {
            return null;
        }
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
        if(bitmap == null) {
            return null;
        }
        Bitmap output = getRoundedCornerBitmap (bitmap);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, output.getWidth(), output.getHeight());

        final float cx = output.getWidth();
        final float cy = output.getHeight();

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        paint.setXfermode (new PorterDuffXfermode (PorterDuff.Mode.CLEAR));
        Path path = new Path();
        path.moveTo((float)(cx / 2.), cy);
        path.lineTo(cx, (float)(cy / 2.));
        path.lineTo(cx, cy);
        path.lineTo((float)(cx / 2.), cy);
        path.close();
        canvas.drawPath(path, paint);
        path.moveTo((float)(cx / 2.), cy);
        path.lineTo(0, (float)(cy / 2.));
        path.lineTo(0, cy);
        path.lineTo((float)(cx / 2.), cy);
        path.close();
        canvas.drawPath(path, paint);
        paint.setXfermode (new PorterDuffXfermode (PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap (bitmap, rect, rect, paint);

        return output;
    }
}
