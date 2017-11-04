package com.teamvan.cryptoexchange;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.teamvan.databases.DBHelper;
import com.teamvan.databases.DownloadResultReceiver;
import com.teamvan.databases.DownloadService;
import com.teamvan.pojos.Exchange;
import com.teamvan.pojos.Globals;
import com.teamvan.pojos.NetworkController;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity implements DownloadResultReceiver.Receiver {

    DBHelper database;
    RequestQueue queue;
    String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Getting Instance of Volley Request Queue
        queue = NetworkController.getInstance(getApplicationContext()).getRequestQueue();
        database = DBHelper.getInstance(this);

        /* Starting Download Service */
        DownloadResultReceiver mReceiver = new DownloadResultReceiver(new Handler());
        mReceiver.setReceiver(this);

        // get the already stored exchanges for bitcoin
        ArrayList<Exchange> currencies_for_btc = database.getCoinCurrencies(Globals.bitcoin_name);

        // get the already stored exchanges for ethereum
        ArrayList<Exchange> currencies_for_eth = database.getCoinCurrencies(Globals.ethereum_name);

        // start a background service to update the exchange rates for currencies against bitcoin
        if (!currencies_for_btc.isEmpty()) {

            ArrayList<String> curr_codes = new ArrayList<>();
            for (Exchange anExchange : currencies_for_btc) {
                String currency_code = anExchange.getCurrency().getCode();
                curr_codes.add(currency_code);
            }

            Intent intent = new Intent(Intent.ACTION_SYNC, null, this, DownloadService.class);
            intent.putExtra("from_coin", Globals.bitcoin_name);
            intent.putExtra("to_currencies", curr_codes);
            intent.putExtra("receiver", mReceiver);
            intent.putExtra("requestId", 1);

            startService(intent);
            Log.e("start service", Globals.bitcoin_name);
        }

        // start a background service to update the exchange rates for currencies against ethereum
        if (!currencies_for_eth.isEmpty()) {

            ArrayList<String> curr_codes = new ArrayList<>();
            for (Exchange anExchange : currencies_for_eth) {
                String currency_code = anExchange.getCurrency().getCode();
                curr_codes.add(currency_code);
            }

            Intent intent = new Intent(Intent.ACTION_SYNC, null, this, DownloadService.class);
            intent.putExtra("from_coin", Globals.ethereum_name);
            intent.putExtra("to_currencies", curr_codes);
            intent.putExtra("receiver", mReceiver);
            intent.putExtra("requestId", 2);

            startService(intent);
            Log.e("start service", Globals.ethereum_name);
        }

        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case DownloadService.STATUS_RUNNING:
                break;
            case DownloadService.STATUS_FINISHED:

                Log.e("service finished", resultData.getString("from_coin"));

                break;
            case DownloadService.STATUS_ERROR:
                /* Handle the error */
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Log.e("service error", error);
                break;
        }
    }
}
