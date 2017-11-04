package com.teamvan.cryptoexchange;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.teamvan.databases.DBHelper;
import com.teamvan.pojos.Coin;
import com.teamvan.pojos.Currency;
import com.teamvan.pojos.Exchange;
import com.teamvan.pojos.Globals;
import com.teamvan.pojos.NetworkController;
import com.teamvan.pojos.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ExchangeActivity extends AppCompatActivity {

    // get the clicked exchange rate
    final Exchange currentExchange = Globals.clickedExchange;
    // Instance of the progress action-view
    MenuItem myActionProgressItem;
    // boolean to monitor whether the exchange rate has been switched
    boolean flipped = false;
    Coin theCoin = currentExchange.getCoin();
    Currency theCurrency = currentExchange.getCurrency();

    TextView currencyName, coinName, exchangeRateTv, lastUpdatedTv;

    EditText fromCoinEt, toCurrencyEt;

    ImageView currencyIv, coinIv;

    RequestQueue queue;
    DBHelper database;
    String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Conversion");

        database = DBHelper.getInstance(this);

        // Getting Instance of Volley Request Queue
        queue = NetworkController.getInstance(this).getRequestQueue();

        exchangeRateTv = findViewById(R.id.exchange_rate_verboseTv);
        currencyName = findViewById(R.id.currencyNameTv);
        coinName = findViewById(R.id.coinNameTv);
        currencyIv = findViewById(R.id.currencyImageIv);
        coinIv = findViewById(R.id.coinImageIv);
        fromCoinEt = findViewById(R.id.id_from_coin_amountEt);
        toCurrencyEt = findViewById(R.id.to_currency_valueEt);
        lastUpdatedTv = findViewById(R.id.last_updatedTv);

        setup(flipped);

        // monitor the text added so as to display the exchange rate as the numbers are entered
        fromCoinEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString())) {
                    double input = Double.parseDouble(s.toString());
                    double exchange = Double.parseDouble(currentExchange.getExchangeRate());

                    double result = input * exchange;
                    toCurrencyEt.setText(numFormat(String.valueOf(result)));

                } else {
                    toCurrencyEt.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        SwitchCompat change = findViewById(R.id.switch_exchangeSc);
        change.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String current_exchange = currentExchange.getExchangeRate();
                double currExchange = Double.parseDouble(current_exchange);

                // reverse the exchange rate
                double newExchange = (double) 1 / currExchange;
                currentExchange.setExchangeRate(String.valueOf(newExchange));

                // change the value of flipped
                flipped = !flipped;
                setup(flipped);

            }
        });

    }

    // display the text based on whether the exchange rate was flipped or not
    private void setup(boolean flipped) {
        if (!flipped) {

            exchangeRateTv.setText("1 " + theCoin.getName() + " = " + numFormat(currentExchange.getExchangeRate()) + " " + theCurrency.getCode());
            currencyName.setText(theCurrency.getName());
            coinName.setText(theCoin.getCoinName());
            currencyIv.setImageBitmap(theCurrency.getImage());
            coinIv.setImageBitmap(theCoin.getImageBitmap());
            fromCoinEt.setText("1");
            toCurrencyEt.setText(numFormat(currentExchange.getExchangeRate()));
            lastUpdatedTv.setText("Last updated at " + currentExchange.getTimestampUpdated());

        } else {

            exchangeRateTv.setText("1 " + theCurrency.getCode() + " = " + numFormat(currentExchange.getExchangeRate()) + " " + theCoin.getName());
            currencyName.setText(theCoin.getCoinName());
            coinName.setText(theCurrency.getName());
            currencyIv.setImageBitmap(theCoin.getImageBitmap());
            coinIv.setImageBitmap(theCurrency.getImage());
            fromCoinEt.setText("1");
            toCurrencyEt.setText(numFormat(currentExchange.getExchangeRate()));
            lastUpdatedTv.setText("Last updated at " + currentExchange.getTimestampUpdated());

        }
    }

    // format the double decimal places so that they can fit on the textview and edittexts
    private String numFormat(String num) {

        double number = Double.parseDouble(num);
        double compare = 10000000;
        String st_result;
        if (number > compare) {
            DecimalFormat formatter = new DecimalFormat("0.######E0");
            st_result = formatter.format(number);
        } else if (number < 1) {
            DecimalFormat formatter = new DecimalFormat("0.######E0");
            st_result = formatter.format(number);
        } else {
            DecimalFormat simpler = new DecimalFormat("#.#####");
            st_result = simpler.format(number);
        }

        return st_result;
    }

    // method to update the exchange rate from this activity
    private void refresh (final MenuItem refresh) {

        final ArrayList<String> currency_name = new ArrayList<>();
        currency_name.add(theCurrency.getCode());

        final String url_to_api = new Utils(this).get_exchange_url(theCoin.getName(), currency_name);

        StringRequest stringRequest_refresh = new StringRequest(Request.Method.GET, url_to_api,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("response verify", response);

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                        Date date = new Date();
                        String updated_at = sdf.format(date);
                        String exchange_rate = "";
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            for (int i = 0; i < currency_name.size(); i++) {
                                exchange_rate = jsonResponse.getString(currency_name.get(i));

                                // update the database with the new exchange rates
                                database.updateExchanges(exchange_rate, updated_at, theCurrency.getId(), theCoin.getName());
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        double currExchange = Double.parseDouble(exchange_rate);

                        if (flipped) {
                            // reverse the exchange rate
                            currExchange = (double) 1 / currExchange;
                        }

                        // update the exchange rate object with current values
                        currentExchange.setExchangeRate(String.valueOf(currExchange));
                        currentExchange.setTimestampUpdated(updated_at);

                        // redisplay the views
                        setup(flipped);

                        // hide the loading progress bar
                        hideProgressBar();

                        // show the refresh icon
                        refresh.setVisible(true);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        String message = null;
                        String error = "";
                        if (volleyError instanceof NoConnectionError) {
                            message = "Cannot connect to the Internet. Please check your connection!";
                            error = "No internet connection";
                        } else if (volleyError instanceof NetworkError) {
                            message = "Cannot connect to the server. Please check your connection!";
                            error = "No internet connection";
                        } else if (volleyError instanceof ServerError) {
                            message = "The server could not be found. Please try again after some time!";
                        } else if (volleyError instanceof AuthFailureError) {
                            message = "Cannot connect to Internet. Please check your connection!";
                        } else if (volleyError instanceof ParseError) {
                            message = "Parsing error! Please try again after some time!!";
                        } else if (volleyError instanceof TimeoutError) {
                            message = "Connection Timeout! Please check your internet connection.";
                            error = "Poor internet connection";
                        }
                        Log.e("volley error", message);

                        Toast.makeText(ExchangeActivity.this, error, Toast.LENGTH_LONG).show();

                        // hide the loading progress bar
                        hideProgressBar();

                        // show the refresh icon
                        refresh.setVisible(true);

                    }
                });
        // Set the tag on the request.
        stringRequest_refresh.setTag(TAG);
        //Adding JsonArrayRequest to Request Queue
        queue.add(stringRequest_refresh);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.exchange_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.menu_refresh) {
            showProgressBar(item);
            refresh(item);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        myActionProgressItem = menu.findItem(R.id.myActionProgress);
        // Extract the action-view from the menu item
        ProgressBar v = (ProgressBar) myActionProgressItem.getActionView();
        v.setIndeterminate(true);
        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }

    public void showProgressBar(MenuItem item) {
        // Show progress item
        myActionProgressItem.setVisible(true);
        item.setVisible(false);
    }

    public void hideProgressBar() {
        // Hide progress item
        myActionProgressItem.setVisible(false);
    }
}
