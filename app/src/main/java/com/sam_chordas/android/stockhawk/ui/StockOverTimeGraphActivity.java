package com.sam_chordas.android.stockhawk.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteOverTimeColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.data.Stock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sorengoard on 26/10/16.
 */
public class StockOverTimeGraphActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = StockOverTimeGraphActivity.class.getSimpleName();
    private List<Stock> mStocks;
    private String stockSymbol;
    private LineChart stockChart;
    private static final int CURSOR_LOADER_ID = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        String stockSymbol = getIntent().getExtras().getString("symbol");

        stockChart = (LineChart) findViewById(R.id.linechart);

        getSupportLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[] {
                QuoteOverTimeColumns._ID,
                QuoteOverTimeColumns.SYMBOL,
                QuoteOverTimeColumns.DATE,
                QuoteOverTimeColumns.HIGH_PRICE
        };
                String selection = QuoteOverTimeColumns.SYMBOL + " = ?";
                String[] selectionArgs = new String[]{stockSymbol};
                String sortOrder = QuoteOverTimeColumns.DATE + " ASC";
                return new CursorLoader(this,
                        QuoteProvider.QuotesOverTime.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data){
        if(data.moveToFirst()){

            mStocks = new ArrayList<>();
            //xvalue is date
            //yvalue is high

            do{
                String date = data.getString(data.getColumnIndex(QuoteOverTimeColumns.DATE));
                String highPrice = data.getString(data.getColumnIndex(QuoteOverTimeColumns.HIGH_PRICE));
                Stock singleStockToAddToChart = new Stock();

                singleStockToAddToChart.setDate(date);
                singleStockToAddToChart.setHighPrice(highPrice);

                mStocks.add(singleStockToAddToChart);
                Log.v(LOG_TAG, "mStocks" + mStocks);

            }
            while(data.moveToNext());

            //Need to get stock values as entries List. Date as a numeric value?
            List<Entry> entries = new ArrayList<Entry>();
            for(int i = 0; i < mStocks.size();i++){
                float highPrice = Float.parseFloat(mStocks.get(i).getHighPrice());
                Log.v(LOG_TAG, "float value: $" + highPrice);
                float dateMarker = (float) i;
                Entry stock = new Entry(dateMarker, highPrice);
                entries.add(stock);

            }

            //line dataset
            LineDataSet dataSet = new LineDataSet(entries, "price ($)");
            LineData lineData = new LineData(dataSet);
            stockChart.setData(lineData);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    //accessing database for historical data

    //

    //draw chart
}
