package com.teamvan.pojos;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.teamvan.cryptoexchange.R;

import java.util.ArrayList;

/**
 * Created by allan on 30/10/2017.
 */

public class Utils {

    private Context context;

    public Utils(Context context) {
        this.context = context;
    }

    public ArrayList<Currency> getCurrencies() {
        final ArrayList<Currency> imageItems = new ArrayList<>();
        // retrieve String drawable array
        TypedArray imgs = context.getResources().obtainTypedArray(R.array.currency_images);
        String[] names = context.getResources().getStringArray(R.array.currency_names);
        String[] codes = context.getResources().getStringArray(R.array.currency_codes);
        String[] symbols = context.getResources().getStringArray(R.array.currency_symbols_unicode_hex);
        for (int i = 0; i < imgs.length(); i++) {
            Bitmap bitmap = decodeSampledBitmapFromResource(context.getResources(), imgs.getResourceId(i, -1), 100, 100);
            imageItems.add(new Currency(i, names[i], codes[i], symbols[i], bitmap));

        }
        imgs.recycle();
        return imageItems;
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
}
