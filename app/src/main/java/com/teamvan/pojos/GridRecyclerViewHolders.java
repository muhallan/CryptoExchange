package com.teamvan.pojos;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teamvan.cryptoexchange.R;

/**
 * Created by allan on 30/10/2017.
 */

class GridRecyclerViewHolders extends RecyclerView.ViewHolder {

    ImageView currencyImageIv;
    TextView currencyNameTv, currencyValueTv;
    LinearLayout wrapper;

    GridRecyclerViewHolders(View itemView) {
        super(itemView);

        // currency details that are loaded from the strings list
        currencyImageIv = itemView.findViewById(R.id.currency_imageIv);
        currencyNameTv = itemView.findViewById(R.id.currency_nameTv);
        currencyValueTv = itemView.findViewById(R.id.exchange_valueTv);
        wrapper = itemView.findViewById(R.id.currency_wrapperLL);
    }

}