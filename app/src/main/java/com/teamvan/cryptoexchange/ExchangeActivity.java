package com.teamvan.cryptoexchange;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.teamvan.pojos.Coin;
import com.teamvan.pojos.Currency;
import com.teamvan.pojos.Exchange;
import com.teamvan.pojos.Globals;

import java.text.DecimalFormat;

public class ExchangeActivity extends AppCompatActivity {

    // get the clicked exchange rate
    final Exchange currentExchange = Globals.clickedExchange;
    // Instance of the progress action-view
    MenuItem myActionProgressItem;
    // boolean to monitor whether the exchange rate has been switched
    boolean flipped = false;
    Coin theCoin = currentExchange.getCoin();
    Currency theCurrency = currentExchange.getCurrency();

    TextView currencyName, coinName, exchangeRateTv;

    EditText fromCoinEt, toCurrencyEt;

    ImageView currencyIv, coinIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Conversion");

        exchangeRateTv = findViewById(R.id.exchange_rate_verboseTv);
        currencyName = findViewById(R.id.currencyNameTv);
        coinName = findViewById(R.id.coinNameTv);
        currencyIv = findViewById(R.id.currencyImageIv);
        coinIv = findViewById(R.id.coinImageIv);
        fromCoinEt = findViewById(R.id.id_from_coin_amountEt);
        toCurrencyEt = findViewById(R.id.to_currency_valueEt);

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

        } else {

            exchangeRateTv.setText("1 " + theCurrency.getCode() + " = " + numFormat(currentExchange.getExchangeRate()) + " " + theCoin.getName());
            currencyName.setText(theCoin.getCoinName());
            coinName.setText(theCurrency.getName());
            currencyIv.setImageBitmap(theCoin.getImageBitmap());
            coinIv.setImageBitmap(theCurrency.getImage());
            fromCoinEt.setText("1");
            toCurrencyEt.setText(numFormat(currentExchange.getExchangeRate()));

        }
    }

    // format the double decimal places so that they can fit on the textview and edittexts
    private String numFormat(String num) {
        double number = Double.parseDouble(num);
        double compare = 10000000;
        String st_result;
        if (number > compare ) {
            DecimalFormat formatter = new DecimalFormat("0.######E0");
            st_result = formatter.format(number);
        } else if (number < 1) {
            DecimalFormat formatter = new DecimalFormat("0.######E0");
            st_result = formatter.format(number);
        }
        else {
            DecimalFormat simpler = new DecimalFormat("#.#####");
            st_result = simpler.format(number);
        }

        return st_result;
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
