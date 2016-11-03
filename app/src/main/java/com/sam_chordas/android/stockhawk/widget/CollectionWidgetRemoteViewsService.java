package com.sam_chordas.android.stockhawk.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteDatabase;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

/**
 * Created by sorengoard on 01/11/16.
 */
public class CollectionWidgetRemoteViewsService extends RemoteViewsService {
    public final String LOG_TAG = CollectionWidgetRemoteViewsService.class.getSimpleName();

    private static final String STOCK_SYMBOL = "stock_symbol";
    private static final String PARENT_WIDGET = "widget";
    private static final String ACTIVITY = "activity";

    private Context mContext;

    private static final String[] STOCK_COLUMNS = {
            QuoteDatabase.QUOTES + "." + QuoteColumns._ID,
            QuoteColumns.SYMBOL,
            QuoteColumns.BIDPRICE,
            QuoteColumns.PERCENT_CHANGE,
            QuoteColumns.ISUP,
            QuoteColumns.CHANGE
    };

    static final int INDEX_STOCK_ID = 0;
    static final int INDEX_STOCK_SYMBOL = 1;
    static final int INDEX_STOCK_BIDPRICE = 2;
    static final int INDEX_STOCK_PERCENTCHANGE = 3;
    static final int INDEX_STOCK_ISUP = 4;
    static final int INDEX_STOCK_CHANGE = 5;


    @Override
    public RemoteViewsFactory onGetViewFactory(final Intent intent){
        return new RemoteViewsFactory() {
            private Cursor data = null;


            @Override
            public void onCreate() {
            }

            @Override
            public void onDataSetChanged() {
                if (data!=null){
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();
                //need uri
                data = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                        STOCK_COLUMNS,
                        null,
                        null,
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data!=null){
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return 0;
            }

            @Override
            public RemoteViews getViewAt(int position) {

                if(position == AdapterView.INVALID_POSITION || data == null || !data.moveToPosition(position)){
                    return null;
                }

                //want to loop through the stocks that exist and write them to the list
                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.list_item_quote);
                 String symbol = data.getString(data.getColumnIndex(QuoteColumns.SYMBOL));
                    String bidPrice = data.getString(data.getColumnIndex(QuoteColumns.BIDPRICE));
                    String change = data.getString(data.getColumnIndex(QuoteColumns.CHANGE));
                    remoteViews.setTextViewText(R.id.stock_symbol, symbol);
                    remoteViews.setTextViewText(R.id.bid_price, bidPrice);
                    remoteViews.setTextViewText(R.id.change, change);

                    int isUp = data.getInt(data.getColumnIndex(QuoteColumns.ISUP));
                    if(isUp == 0){
                        remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
                    } else {
                        remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
                    }

                    Intent graphActivityIntent = new Intent();
                    graphActivityIntent.setData(QuoteProvider.Quotes.withSymbol(symbol));
                    graphActivityIntent.putExtra(STOCK_SYMBOL, symbol);
                    graphActivityIntent.putExtra(ACTIVITY ,PARENT_WIDGET);
                    //doublecheck this is the right id
                    remoteViews.setOnClickFillInIntent(R.id.stock_collection_widget_list, graphActivityIntent);
                return remoteViews;
            }

            @Override
            public RemoteViews getLoadingView() {

                return new RemoteViews(getPackageName(), R.layout.list_item_quote);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {

                if (data.moveToPosition(position)) {
                    return data.getInt(INDEX_STOCK_ID);
                }
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
