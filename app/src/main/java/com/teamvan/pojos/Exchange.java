package com.teamvan.pojos;

/**
 * Created by allan on 01/11/2017.
 */

public class Exchange {
    private Currency currency;
    private Coin coin;
    private String exchangeRate;
    private String timestampUpdated;

    public Exchange() {
    }

    public Exchange(Currency currency, Coin coin, String exchangeRate, String timestampUpdated) {
        this.currency = currency;
        this.coin = coin;
        this.exchangeRate = exchangeRate;
        this.timestampUpdated = timestampUpdated;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Coin getCoin() {
        return coin;
    }

    public void setCoin(Coin coin) {
        this.coin = coin;
    }

    public String getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(String exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public String getTimestampUpdated() {
        return timestampUpdated;
    }

    public void setTimestampUpdated(String timestampUpdated) {
        this.timestampUpdated = timestampUpdated;
    }
}
