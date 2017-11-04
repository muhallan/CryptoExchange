package com.teamvan.pojos;

/**
 * Created by allan on 30/10/2017.
 */

public class Globals {
    public static final String baseUrl = "https://www.cryptocompare.com";
    public static final String baseUrl_for_exchange = "https://min-api.cryptocompare.com/data/price?";
    public static boolean timeout = false;
    public static final String bitcoin_name = "BTC";
    public static final String ethereum_name = "ETH";

    // for use to get the clicked currency in the fragments from the ExchangeActivity
    public static Exchange clickedExchange;
}
