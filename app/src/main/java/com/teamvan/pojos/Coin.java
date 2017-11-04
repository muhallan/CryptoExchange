package com.teamvan.pojos;

import android.graphics.Bitmap;

/**
 * Created by allan on 30/10/2017.
 */

public class Coin {
    private Bitmap imageBitmap;
    private String name, coinName, fullName, url;

    public Coin() {
    }

    public Coin(Bitmap imageBitmap, String name, String coinName, String fullName, String url) {
        this.imageBitmap = imageBitmap;
        this.name = name;
        this.coinName = coinName;
        this.fullName = fullName;
        this.url = url;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
