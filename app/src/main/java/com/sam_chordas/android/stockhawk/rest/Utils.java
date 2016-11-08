package com.sam_chordas.android.stockhawk.rest;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteOverTimeColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sam_chordas on 10/8/15.
 */
public class Utils {

  private static String LOG_TAG = Utils.class.getSimpleName();

  public static final String SH_PREFS = "StockHawkPreferences";
  public static boolean showPercent = true;

  public static ArrayList quoteJsonToContentVals(String JSON){
    ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
    JSONObject jsonObject;
    JSONArray resultsArray;
    try{
      jsonObject = new JSONObject(JSON);
      if (jsonObject != null && jsonObject.length() != 0){
        jsonObject = jsonObject.getJSONObject("query");
        int count = Integer.parseInt(jsonObject.getString("count"));
        if (count == 1){
          jsonObject = jsonObject.getJSONObject("results")
              .getJSONObject("quote");
          if(jsonObject.getString("Bid").equals("null")){
            return new ArrayList<>();
          }
          batchOperations.add(buildBatchOperation(jsonObject));
        } else{
          resultsArray = jsonObject.getJSONObject("results").getJSONArray("quote");

          if (resultsArray != null && resultsArray.length() != 0){
            for (int i = 0; i < resultsArray.length(); i++){
              jsonObject = resultsArray.getJSONObject(i);
              if(jsonObject.getString("Bid").equals("null")){
                return new ArrayList<>();
              }
              batchOperations.add(buildBatchOperation(jsonObject));
            }
          }
        }
      }
    } catch (JSONException e){
      Log.e(LOG_TAG, "String to JSON failed: " + e);
    }
    return batchOperations;
  }

  public static ArrayList quoteJsonToContentValsOverTime(String JSON) {
    ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
    JSONObject jsonObject = null;
    JSONArray jsonArray = null;
    try {
      jsonObject = new JSONObject(JSON);
      if(jsonObject != null && jsonObject.length()!=0){
        jsonObject = jsonObject.getJSONObject("query");
        int count = Integer.parseInt(jsonObject.getString("count"));
        if (count == 1)
        jsonArray = jsonObject.getJSONObject("results").getJSONArray("quote");
        if(jsonArray != null && jsonArray.length() != 0) {
          for (int i =0;i <jsonArray.length();i++){
            jsonObject = jsonArray.getJSONObject(i);
            batchOperations.add(buildOverTimeBatchOp(jsonObject));
          }
        }
      }
    } catch (JSONException e){
      Log.e(LOG_TAG, "String to JSON(historic) failed: "+ e);
    }
    return batchOperations;
  }

  public static String truncateBidPrice(String bidPrice){
//    if(bidPrice.equals(null)) {
      bidPrice = String.format("%.2f", Float.parseFloat(bidPrice));
      return bidPrice;
//    } else return "0";
  }

  public static String truncateChange(String change, boolean isPercentChange){
//    if(!change.equals("null")){
    String weight = change.substring(0,1);
    String ampersand = "";
    if (isPercentChange){
      ampersand = change.substring(change.length() - 1, change.length());
      change = change.substring(0, change.length() - 1);
    }
    change = change.substring(1, change.length());

    double round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
    change = String.format("%.2f", round);
    StringBuffer changeBuffer = new StringBuffer(change);
    changeBuffer.insert(0, weight);
    changeBuffer.append(ampersand);
    change = changeBuffer.toString();
      return change;
//    } else {
//      return "0";
//    }
  }

  public static ContentProviderOperation buildBatchOperation(JSONObject jsonObject){
    ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
        QuoteProvider.Quotes.CONTENT_URI);
    try {
      String change = jsonObject.getString("Change");
      builder.withValue(QuoteColumns.SYMBOL, jsonObject.getString("symbol"));
      builder.withValue(QuoteColumns.BIDPRICE, truncateBidPrice(jsonObject.getString("Bid")));
      builder.withValue(QuoteColumns.PERCENT_CHANGE, truncateChange(
          jsonObject.getString("ChangeinPercent"), true));
      builder.withValue(QuoteColumns.CHANGE, truncateChange(change, false));
      builder.withValue(QuoteColumns.ISCURRENT, 1);
      if (change.charAt(0) == '-'){
        builder.withValue(QuoteColumns.ISUP, 0);
      }else{
        builder.withValue(QuoteColumns.ISUP, 1);
      }

    } catch (JSONException e){
      e.printStackTrace();
    }
    return builder.build();
  }

  public static ContentProviderOperation buildOverTimeBatchOp(JSONObject object) {
    ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(QuoteProvider.QuotesOverTime.CONTENT_URI);
    try {
      builder.withValue(QuoteOverTimeColumns.SYMBOL, object.getString("Symbol"));
      builder.withValue(QuoteOverTimeColumns.DATE, object.getString("Date"));
      builder.withValue(QuoteOverTimeColumns.HIGH_PRICE, object.getString("High"));

    } catch (JSONException e) {
      e.printStackTrace();
    }
    return builder.build();
  }

  //Using a sharedPref to inform the user that they've entered a stock that doesnt exist
  public static void stockSearchRequest(Context context, String symbol, boolean result) {
    if (context != null) {
      SharedPreferences prefs = context.getSharedPreferences(SH_PREFS, 0);
      SharedPreferences.Editor editor = prefs.edit();
      if(result == true) {
        editor.putString(context.getString(R.string.pref_wrong_stock_symbol), "").apply();
      } else {
        editor.clear().apply();
        editor.putString(context.getString(R.string.pref_wrong_stock_symbol), "'" + symbol + "' ").apply();
      }
    }
  }

  }

