package com.teamvan.pojos;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ProgressBar;

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

        // retrieve Strings and drawable arrays
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

    public ArrayList<Coin> getCoins() {
        ArrayList<Coin> both_coins = new ArrayList<>();

        Coin bitcoin = new Coin();
        bitcoin.setCoinName("Bitcoin");
        bitcoin.setFullName("Bitcoin (BTC)");
        bitcoin.setName("BTC");
        bitcoin.setImageUrl("/media/19633/btc.png");
        bitcoin.setUrl("/coins/btc/overview");

        Coin ethereum = new Coin();
        ethereum.setCoinName("Ethereum Classic");
        ethereum.setUrl("/coins/etc/overview");
        ethereum.setImageUrl("/media/20275/etc2.png");
        ethereum.setFullName("Ethereum Classic (ETC)");
        ethereum.setName("ETC");

        both_coins.add(bitcoin);
        both_coins.add(ethereum);

        return both_coins;
    }

    public String get_exchange_url(String from, ArrayList<String> to) {

        StringBuilder to_currencies = new StringBuilder();
        for (int i = 0; i < to.size(); i++) {
            to_currencies.append(to.get(i));
            to_currencies.append(",");
        }

        String currency_list_with_commas = to_currencies.toString();
        if (currency_list_with_commas.length() > 1) {
            currency_list_with_commas = currency_list_with_commas.
                    substring(0, currency_list_with_commas.length() - 1);
        }

        // build the url from the parameters
        return Globals.baseUrl_for_exchange + "fsym=" + from + "&tsyms=" + currency_list_with_commas;
    }

    public static ProgressBar showProgressBar(@NonNull final Activity activity) {

        final WindowManager wm = (WindowManager) activity.getApplicationContext().getSystemService(Activity.WINDOW_SERVICE);

        final ProgressBar progressBar = new ProgressBar(activity, null, android.R.attr.progressBarStyleLarge);
        progressBar.setIndeterminate(true);

        WindowManager.LayoutParams windowLayoutParams = new WindowManager.LayoutParams();
        windowLayoutParams.gravity = Gravity.CENTER;
        windowLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
        windowLayoutParams.token = activity.getWindow().getDecorView().getWindowToken();
        windowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        windowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        windowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;

        windowLayoutParams.format = PixelFormat.TRANSLUCENT;

        wm.addView(progressBar, windowLayoutParams);

        return progressBar;
    }
}
