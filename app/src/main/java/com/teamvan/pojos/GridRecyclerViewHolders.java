package com.teamvan.pojos;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.teamvan.cryptoexchange.R;

/**
 * Created by allan on 30/10/2017.
 */

class GridRecyclerViewHolders extends RecyclerView.ViewHolder {

    ImageView currencyImageIv;
    TextView currencyNameTv, currencyValueTv;

    GridRecyclerViewHolders(View itemView) {
        super(itemView);

        // currency details that are loaded from the strings list
        currencyImageIv = itemView.findViewById(R.id.currency_imageIv);
        currencyNameTv = itemView.findViewById(R.id.currency_nameTv);
        currencyValueTv = itemView.findViewById(R.id.exchange_valueTv);

        // handle clicks on each card and take to the exchange rate screen
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

}