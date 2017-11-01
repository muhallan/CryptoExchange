package com.teamvan.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.teamvan.pojos.Coin;
import com.teamvan.pojos.Currency;
import com.teamvan.pojos.Exchange;
import com.teamvan.pojos.Utils;

import java.util.ArrayList;

/**
 * Created by allan on 01/11/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper instance;
    private static final String DATABASE_NAME = "cryptoexchange";
    private static final int DATABASE_VERSION = 1;
    private Context context;

    private interface ITEM extends BaseColumns {

        // currencies table
        String TABLE_CURRENCIES = "bitcoin_currencies";
        String CURRENCY_ID = "currency_id";
        String EXCHANGE_RATE = "exchange_rate";
        String TIMESTAMP_UPDATED = "timestamp_updated";
        String COIN_NAME = "coin_name";
    }

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
    //private static final String UNIQUE = "UNIQUE ON CONFLICT ABORT";

    private static final String CREATE_TABLE_CURRENCIES = CREATE_TABLE + ITEM.TABLE_CURRENCIES +
            " (" + ITEM._ID + " INTEGER PRIMARY KEY, " + ITEM.CURRENCY_ID + " INTEGER, " + ITEM.EXCHANGE_RATE
            + " TEXT, " + ITEM.TIMESTAMP_UPDATED + " TEXT, " + ITEM.COIN_NAME + " TEXT);";

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            db.execSQL(CREATE_TABLE_CURRENCIES);
        }catch(SQLException e){
            Log.e("Error creating database", e.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE + ITEM.TABLE_CURRENCIES);
        onCreate(db);
    }

    public long addCurrency(int currency_id, String exchange_rate, String timestamp, String coin_name) {

        ContentValues values = new ContentValues();
        values.put(ITEM.CURRENCY_ID, currency_id);
        values.put(ITEM.EXCHANGE_RATE, exchange_rate);
        values.put(ITEM.TIMESTAMP_UPDATED, timestamp);
        values.put(ITEM.COIN_NAME, coin_name);

        return getWritableDatabase().insert(ITEM.TABLE_CURRENCIES, null, values);
    }

    public ArrayList<Exchange> getCoinCurrencies (String coin_name) {

        // query to select currencies that belong to a given coin
        String query = "SELECT * FROM " + ITEM.TABLE_CURRENCIES + " WHERE " + ITEM.COIN_NAME
                + " = '" + coin_name + "'";
        Cursor cursor = getReadableDatabase().rawQuery(query, null);

        // arraylist to store the currencies
        ArrayList<Exchange> currencies_for_coin = new ArrayList<>();

        if (cursor.moveToFirst()) { // move the cursor to the starting position if it's not empty

            // get the coin details requested for
            ArrayList<Coin> our_coins = new Utils(this.context).getCoins();
            Coin current_coin = new Coin();

            for (int i = 0; i < our_coins.size(); i++) {
                if (our_coins.get(i).getName().equals(coin_name)) {
                    current_coin = our_coins.get(i);
                    break;
                }
            }

            // the currencies we are working with
            ArrayList<Currency> all_currencies = new Utils(this.context).getCurrencies();

            // get the details of exchanges found by looping through the cursor
            do {
                // get the current Currency
                int currency_id = cursor.getInt(1);
                Currency currency = new Currency();

                // loop through all the currencies and find the currency with the id got
                for (int i = 0; i < all_currencies.size(); i++) {
                    if (all_currencies.get(i).getId() == currency_id) {
                        currency = all_currencies.get(i);
                        break;
                    }
                }

                String exchange_rate = cursor.getString(2);
                String timestamp = cursor.getString(3);

                Exchange exchange = new Exchange(currency, current_coin, exchange_rate, timestamp);
                currencies_for_coin.add(exchange);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return currencies_for_coin;
    }

    public void deleteCurrency (int currency_id, String coin_name) {

        // query for a currency and delete it
        String sql = "DELETE FROM " + ITEM.TABLE_CURRENCIES + " WHERE " + ITEM.CURRENCY_ID + " = " +
                currency_id + " AND " + ITEM.COIN_NAME + " = '" + coin_name + "'";
        try{
            getWritableDatabase().execSQL(sql);
        } catch (SQLException e) {
            Log.e("Error deleting currency", e.toString());
        }
    }
}
