package com.teamvan.fragments;


import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.teamvan.cryptoexchange.R;
import com.teamvan.pojos.Currency;
import com.teamvan.pojos.GridRecyclerViewAdapter;
import com.teamvan.pojos.ItemOffsetDecoration;
import com.teamvan.pojos.Utils;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class BitcoinFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener  {

    RecyclerView rView;
    RequestQueue queue;
    GridRecyclerViewAdapter grid_adapter;
    private SwipeRefreshLayout swipeContainer;
    // DBHelper database;
    CoordinatorLayout coordinatorLayout;
    String TAG = this.getClass().getSimpleName();
    ItemOffsetDecoration itemDecoration;

    public BitcoinFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bitcoin, container, false);

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

        ArrayList<Currency> currencies = new Utils(getActivity()).getCurrencies();
        setupGridRecyclerView(rView, currencies);

        swipeContainer.post(new Runnable() {
            @Override
            public void run() {
                // swipeContainer.setRefreshing(true);

                // makePostRequest(url, index, false);
            }
        });


        return v;
    }

    private void setupGridRecyclerView(RecyclerView recyclerview, ArrayList<Currency> currencies) {
        // set the layout manager to grid
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);

        // change the span size to 1 for
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override public int getSpanSize(int position) {
                return (position == 0) ? 2 : 1;
            }
        });

        recyclerview.setLayoutManager(gridLayoutManager);

        grid_adapter = new GridRecyclerViewAdapter(getActivity(), currencies);
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
        //rView.removeItemDecoration(itemDecoration);
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
