package com.teamvan.pojos;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teamvan.cryptoexchange.R;

import java.util.List;

/**
 * Created by allan on 30/10/2017.
 */

public class GridRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Currency> currencies;
    private Context context;

    private final int TYPE_GRID_HEADER = 0;

    // DBHelper database;

    public GridRecyclerViewAdapter(Context context, List<Currency> itemList) {
        this.currencies = itemList;
        this.context = context;
        // database = DBHelper.getInstance(context);

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
        //holder.productImage.setImageUrl(Globals.url_to_product_images + products.get(position).getImageOne(), NetworkController.getInstance(context).getImageLoader());
        //Glide.with(context).load(Globals.url_to_product_images + products.get(position).getImageOne()).fitCenter().into(holder.productImage);

        if (position != TYPE_GRID_HEADER) {
            // cast the viewHolder in order to use the GridRecyclerViewHolders
            GridRecyclerViewHolders holder = (GridRecyclerViewHolders) viewHolder;

            holder.currencyValueTv.setText("$3000");

            // set the name and code of the currency
            holder.currencyNameTv.setText(currencies.get(position - 1).getName() + " (" + currencies.get(position - 1).getCode() + ")");

            // set the image of the currency
            holder.currencyImageIv.setImageBitmap(currencies.get(position - 1).getImage());
        }

    }

    @Override
    public int getItemCount() {
        return this.currencies.size() + 1;
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

