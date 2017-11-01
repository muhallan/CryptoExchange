package com.teamvan.fragments;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
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
import com.teamvan.cryptoexchange.R;
import com.teamvan.databases.DBHelper;
import com.teamvan.pojos.Currency;
import com.teamvan.pojos.Exchange;
import com.teamvan.pojos.Globals;
import com.teamvan.pojos.GridRecyclerViewAdapter;
import com.teamvan.pojos.ItemOffsetDecoration;
import com.teamvan.pojos.KeyPairBoolData;
import com.teamvan.pojos.MultiSpinnerSearch;
import com.teamvan.pojos.NetworkController;
import com.teamvan.pojos.SpinnerListener;
import com.teamvan.pojos.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class BitcoinFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener  {

    RecyclerView rView;
    RequestQueue queue;
    GridRecyclerViewAdapter grid_adapter;
    private SwipeRefreshLayout swipeContainer;
    DBHelper database;
    CoordinatorLayout coordinatorLayout;
    String TAG = this.getClass().getSimpleName();
    ItemOffsetDecoration itemDecoration;

    // currencies to be added to this fragment and coin
    ArrayList<Currency> currencies_to_add = new ArrayList<>();

    public BitcoinFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bitcoin, container, false);

        database = DBHelper.getInstance(getActivity());

        //Getting Instance of Volley Request Queue
        queue = NetworkController.getInstance(getActivity()).getRequestQueue();

        swipeContainer = v.findViewById(R.id.swipeContainer);

        coordinatorLayout = v.findViewById(R.id.coordinator_layout_cl);

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(this);

        rView = v.findViewById(R.id.home_recycler_viewRV);
        rView.setHasFixedSize(true);
        rView.setItemViewCacheSize(20);
        rView.setDrawingCacheEnabled(true);

        ArrayList<Exchange> coin_exchanges = database.getCoinCurrencies(Globals.bitcoin_name);
        setupGridRecyclerView(rView, coin_exchanges);

        swipeContainer.post(new Runnable() {
            @Override
            public void run() {
                // swipeContainer.setRefreshing(true);

                // makePostRequest(url, index, false);
            }
        });

        // floating action button to handle adding of new currency to be exchanged
        FloatingActionButton addCurrency = v.findViewById(R.id.addCurrencyFAB);
        addCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                // get the layoutinflater to inflate the custom layout for the alertdialog
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View alertLayout = inflater.inflate(R.layout.add_currency_layout, null);

                MultiSpinnerSearch currenciesSpinner = alertLayout.findViewById(R.id.currencyMSS);
                setupSizesSpinner(currenciesSpinner);

                // 1. Instantiate an AlertDialog.Builder with its constructor
                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                builder.setTitle("Add currency to exchange against Bitcoin (BTC)");

                // this is set the view from XML inside AlertDialog
                builder.setView(alertLayout);
                // disallow cancel of AlertDialog on click of back button and outside touch
                builder.setCancelable(false);

                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Do nothing here because we override this button later to change the close behaviour.
                        //However, we still need this because on older versions of Android unless we
                        //pass a handler the button doesn't get instantiated
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                // create the dialog and show it
                final AlertDialog dialog = builder.create();
                dialog.show();

                //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    Boolean wantToCloseDialog = false;

                    @Override
                    public void onClick(final View v) {

                        //Do stuff, possibly set wantToCloseDialog to true then...

                        if (currencies_to_add.isEmpty()) {
                            wantToCloseDialog = true;
                            Toast.makeText(getActivity(), "No currency selected", Toast.LENGTH_SHORT).show();
                        } else {
                            wantToCloseDialog = true;
                            final ProgressBar progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleSmall);
                            progressBar.setIndeterminate(true);
                            progressBar.setVisibility(View.VISIBLE);

                            ArrayList<String> to_add_currencies = new ArrayList<>();
                            for (int i = 0; i < currencies_to_add.size(); i++) {
                                to_add_currencies.add(currencies_to_add.get(i).getCode());
                            }

                            final String url_to_api = new Utils(getActivity()).get_exchange_url(Globals.bitcoin_name, to_add_currencies);

                            StringRequest stringRequest_verify = new StringRequest(Request.Method.GET, url_to_api,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {

                                            Log.e("response verify", response);
                                            progressBar.setVisibility(View.GONE);

                                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                                            Date date = new Date();
                                            String retrieved_at = sdf.format(date);

                                            try {
                                                JSONObject jsonResponse = new JSONObject(response);
                                                for (int i = 0; i < currencies_to_add.size(); i++) {
                                                    String exchange_rate = jsonResponse.getString(currencies_to_add.get(i).getCode());
                                                    database.addCurrency(currencies_to_add.get(i).getId(), exchange_rate, retrieved_at, Globals.bitcoin_name);
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
/*
                                            if (response.equals("late")) {
                                                // 1. Instantiate an AlertDialog.Builder with its constructor
                                                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                                builder.setTitle("Voucher Payment");
                                                // 2. Chain together various setter methods to set the dialog characteristics
                                                builder.setMessage("The voucher code you have entered is expired. Please enter another one.\nExpiration duration is 5 minutes.");
                                                // Add the buttons
                                                builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                                builder.setCancelable(false);

                                                // 3. Get the AlertDialog from create()
                                                AlertDialog dialog = builder.create();
                                                dialog.show();
                                            }*/
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError volleyError) {
                                            String message = null;
                                            if (volleyError instanceof NoConnectionError) {
                                                Toast.makeText(v.getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                                                message = "Cannot connect to Internet...Please check your connection!";
                                            } else if (volleyError instanceof NetworkError) {
                                                Toast.makeText(v.getContext(), "Please check your Internet connection", Toast.LENGTH_SHORT).show();
                                                message = "Cannot connect to the server...Please check your connection!";
                                            } else if (volleyError instanceof ServerError) {
                                                message = "The server could not be found. Please try again after some time!!";
                                            } else if (volleyError instanceof AuthFailureError) {
                                                message = "Cannot connect to Internet...Please check your connection!";
                                            } else if (volleyError instanceof ParseError) {
                                                message = "Parsing error! Please try again after some time!!";
                                            } else if (volleyError instanceof TimeoutError) {
                                                Toast.makeText(v.getContext(), "Please check your Internet connection", Toast.LENGTH_SHORT).show();
                                                message = "Connection TimeOut! Please check your internet connection.";
                                            }
                                            Log.e("volley error fav", message);

                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });
                            // Set the tag on the request.
                            stringRequest_verify.setTag(TAG);
                            //Adding JsonArrayRequest to Request Queue
                            queue.add(stringRequest_verify);
                        }
                        if (wantToCloseDialog)
                            dialog.dismiss();
                        //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
                    }

                });

                doKeepDialog(dialog);
            }
        });

        return v;
    }

    // Prevent dialog dismiss when orientation changes
    private void doKeepDialog(Dialog dialog) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
    }

    private void setupSizesSpinner (MultiSpinnerSearch currenciesSpinner) {

        // Getting array of Strings to Bind in Spinner
        final ArrayList<Currency> all_currencies = new Utils(getActivity()).getCurrencies();
        final List<KeyPairBoolData> listArray = new ArrayList<>();

        for (int i = 0; i < all_currencies.size(); i++) {

            KeyPairBoolData data = new KeyPairBoolData();
            data.setId(i + 1);
            data.setName((all_currencies.get(i).getName() + " - " + all_currencies.get(i).getCode()));
            data.setSelected(false);
            listArray.add(data);
        }


        /*
         * Search MultiSelection Spinner (With Search/Filter Functionality)
         *
         * Using MultiSpinnerSearch class
         */
        currenciesSpinner.setItems(listArray, -1, new SpinnerListener() {

            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {

                // your operation with code...
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isSelected()) {
                        Log.e(TAG, i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
                        Log.e(TAG, all_currencies.get(i).getName());
                        currencies_to_add.add(all_currencies.get(i));
                    }
                }

            }
        });
    }

    private void setupGridRecyclerView(RecyclerView recyclerview, ArrayList<Exchange> exchanges) {
        // set the layout manager to grid
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);

        // change the span size to 1 for
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override public int getSpanSize(int position) {
                return (position == 0) ? 2 : 1;
            }
        });

        recyclerview.setLayoutManager(gridLayoutManager);

        grid_adapter = new GridRecyclerViewAdapter(exchanges);
        itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.item_offset);
        recyclerview.addItemDecoration(itemDecoration);
        recyclerview.setAdapter(grid_adapter);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onStop() {
        super.onStop();
        if (queue != null) {
            queue.cancelAll(TAG);
        }

        rView.removeItemDecoration(itemDecoration);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    void refresh() {
        // rView.removeItemDecoration(itemDecoration);
        // Toast.makeText(getActivity(), "refreshed", Toast.LENGTH_SHORT).show();
        // setupGridRecyclerView(rView);
    }

    /**
     * SNACKBAR for No Internet Connection
     */
    private void toggleSnackBar(String msg, int duration) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, msg, duration);
        snackbar.show();
    }
}
