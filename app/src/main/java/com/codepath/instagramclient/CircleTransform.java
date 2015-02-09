package com.codepath.instagramclient;

/**
 * Created by vibhalaljani on 2/6/15.
 * Borrowed initially from
 * http://stackoverflow.com/questions/26112150/android-create-circular-image-with-picasso/26112408#26112408
 *
 * This class is used to provide a CircleTransform to use with Picasso, so we can show the profile
 * picture as a rounded image, rather than a regular square image.
 *
 * Added by vlaljani: Code to draw the black border around the circle. Also found on stackoverflow
 * after some searching.
 */
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

import com.squareup.picasso.Transformation;

public class CircleTransform implements Transformation {
    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap,
                BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);

        squaredBitmap.recycle();

        Paint paint1 = new Paint();
        paint1.setColor(Color.BLACK);
        paint1.setStyle(Style.STROKE);
        paint1.setAntiAlias(true);
        paint1.setStrokeWidth(2);
        canvas.drawCircle(r, r, r, paint1);

        return bitmap;
    }

    @Override
    public String key() {
        return "circle";
    }
}