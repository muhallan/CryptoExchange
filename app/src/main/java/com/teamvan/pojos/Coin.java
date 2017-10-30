package com.teamvan.pojos;

/**
 * Created by allan on 30/10/2017.
 */

public class Coin {
    private String imageUrl, name, coinName, fullName, url;

    public Coin() {
    }

    public Coin(String imageUrl, String name, String coinName, String fullName, String url) {
        this.imageUrl = imageUrl;
        this.name = name;
        this.coinName = coinName;
        this.fullName = fullName;
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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
