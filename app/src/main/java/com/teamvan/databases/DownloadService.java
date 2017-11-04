package com.teamvan.databases;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.teamvan.pojos.Currency;
import com.teamvan.pojos.Exchange;
import com.teamvan.pojos.Globals;
import com.teamvan.pojos.Utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import static android.R.id.list;
import static com.teamvan.cryptoexchange.R.string.from;

/**
 * Created by Muhwezi Allan on 12/19/2016.
 */
public class DownloadService extends IntentService {
    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    private static final String TAG = "DownloadService";

    DBHelper database;

    public DownloadService() {
        super(DownloadService.class.getName());
    }

    public DownloadService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        String coin = intent.getStringExtra("from_coin");
        ArrayList<String> to_currency_list = intent.getStringArrayListExtra("to_currencies");

        Bundle bundle = new Bundle();

        database = DBHelper.getInstance(this);

        if (!TextUtils.isEmpty(coin) && !to_currency_list.isEmpty()) {
            /* Update UI: Download Service is Running */
            receiver.send(STATUS_RUNNING, Bundle.EMPTY);
            Log.e(TAG, "service running started");
            Log.e(TAG, "coin - " + coin + ": - currency - " + to_currency_list);

            try {
                Log.e(TAG, "service running inside try");
                getDatabaseInfo(this, coin, to_currency_list);

            } catch (Exception e) {

                /* Sending error message back to activity */
                bundle.putString(Intent.EXTRA_TEXT, e.toString());
                receiver.send(STATUS_ERROR, bundle);
                Log.e("downloadservice", "- " + coin + " - " + e.toString());
                e.printStackTrace();
            }
        }
        Log.e(TAG, "Service Stopping!");
    }

    public String performGetCall(String requestURL) {

        URL url;
        String response = "";
        try {
            //Create a URL object holding our url
            url = new URL(requestURL);

            //Create a connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(25000);
            conn.setConnectTimeout(25000);
            conn.setRequestMethod("GET");

            //Connect to our url
            conn.connect();

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;

                //Create a new InputStreamReader and a new buffered reader
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
                //Close our InputStream and Buffered reader
                br.close();

            } else {
                response = "";
            }

        } catch (java.net.SocketTimeoutException e) {
            Globals.timeout = true;
            Log.e("socket timeout", "socket");
            e.printStackTrace();
            return "{}";
        } catch (java.io.IOException e) {
            Log.e("ioexception", "io");
            e.printStackTrace();
            Globals.timeout = true;
            return "{}";
        } catch (Exception e) {
            Log.e("other exception", "e");
            e.printStackTrace();
            Globals.timeout = true;
            return "{}";
        }

        return response;
    }


    public void getDatabaseInfo(Context context, String from_coin, ArrayList<String> to_currency_strings) {
        final String url_to_api = new Utils(context).get_exchange_url(from_coin, to_currency_strings);

        String result;

        result = performGetCall(url_to_api);
        Log.e(TAG + " online:", url_to_api + " - Result: " + result);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        String updated_at = sdf.format(date);

        ArrayList<Currency> all_currencies = new Utils(context).getCurrencies();
        try {
            //parse JSON data
            JSONObject jsonResponse = new JSONObject(result);
            for (int i = 0; i < to_currency_strings.size(); i++) {
                String exchange_rate = jsonResponse.getString(to_currency_strings.get(i));
                int curr_id = 0;
                // get the id of the currency updated
                for (Currency currency : all_currencies) {
                    if (currency.getCode().equals(to_currency_strings.get(i))) {
                        curr_id = currency.getId();
                    }
                }

                // update the database with the new exchange rate
                database.updateExchanges(exchange_rate, updated_at, curr_id, from_coin);
            }

            Log.e(TAG + "json", from_coin.toLowerCase());


        } catch (Exception e) {
            Log.e("ERROR", "Error while retrieving from online db. " + e.getMessage());
            e.printStackTrace();
        }
    }

}
