package com.sam_chordas.android.stockhawk.data;

/**
 * Created by sorengoard on 27/10/16.
 */
public class Stock {
    String stockSymbol;
    String highPrice;
    String date;

    public Stock() {}

    public String getStockSymbol() {return stockSymbol; }
    public void setStockSymbol(String symbol) {this.stockSymbol = symbol;}

    public String getHighPrice() {return highPrice; }
    public void setHighPrice(String high) {this.highPrice = high;}

    public String getDate() {return date;}
    public void setDate(String d) {this.date = d;}
}
