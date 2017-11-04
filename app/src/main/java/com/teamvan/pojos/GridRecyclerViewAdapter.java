package com.teamvan.pojos;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teamvan.cryptoexchange.ExchangeActivity;
import com.teamvan.cryptoexchange.R;
import com.teamvan.databases.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by allan on 30/10/2017.
 */

public class GridRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Currency> currencies;
    private List<Exchange> currency_exchanges;

    private final int TYPE_GRID_HEADER = 0;

    private Context context;

    public GridRecyclerViewAdapter(Context context, List<Exchange> currency_exchanges) {
        currencies = new ArrayList<>();
        this.currency_exchanges = currency_exchanges;
        this.context = context;

        for (int i = 0; i < this.currency_exchanges.size(); i++) {
            Currency currency = currency_exchanges.get(i).getCurrency();
            currencies.add(currency);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_GRID_HEADER) { // if the position is the header
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_currency_list_header, null);
            return new HeaderViewHolder(layoutView);
        } else {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_currency_list_item, null);
            return new GridRecyclerViewHolders(layoutView);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (position != TYPE_GRID_HEADER) {
            // cast the viewHolder in order to use the GridRecyclerViewHolders
            GridRecyclerViewHolders holder = (GridRecyclerViewHolders) viewHolder;

            holder.currencyValueTv.setText(currencies.get(position - 1).getUnicode_hex() + " " + currency_exchanges.get(position - 1).getExchangeRate());

            // set the name and code of the currency
            holder.currencyNameTv.setText(currencies.get(position - 1).getName() + " (" + currencies.get(position - 1).getCode() + ")");

            // set the image of the currency
            holder.currencyImageIv.setImageBitmap(currencies.get(position - 1).getImage());

            // handle clicks on each card and take to the exchange rate screen
            holder.wrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Globals.clickedExchange = currency_exchanges.get(position - 1);
                    context.startActivity(new Intent(context, ExchangeActivity.class));
                }
            });
        }

    }

    // used to update the exchanges when a new currency is added
    public void notifyNewExchanges (ArrayList<Exchange> new_exchanges) {

        for (Exchange exchange: new_exchanges) {
            currency_exchanges.add(exchange);
            Currency currency = exchange.getCurrency();
            currencies.add(currency);
        }

        notifyItemInserted(this.currency_exchanges.size());
    }

    // force the UI update when the currency data is refreshed
    public void notifyChangedExchanges (String coin_name) {
        DBHelper database = DBHelper.getInstance(context);
        currency_exchanges = database.getCoinCurrencies(coin_name);

        currencies.clear();
        for (int i = 0; i < currency_exchanges.size(); i++) {
            Currency currency = currency_exchanges.get(i).getCurrency();
            currencies.add(currency);
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.currency_exchanges.size() + 1;
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        private TextView title;

        private HeaderViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleCurrenciesGridTv);
            title.setText("Click on a currency card to convert in that currency");
        }

    }

    @Override
    public int getItemViewType(int position) {

        int TYPE_GRID_REGULAR = 1;

        if (position == 0)
            return TYPE_GRID_HEADER;
        else
            return TYPE_GRID_REGULAR;
    }

}

