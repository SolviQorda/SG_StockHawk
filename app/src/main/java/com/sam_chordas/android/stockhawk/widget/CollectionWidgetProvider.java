package com.sam_chordas.android.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;

/**
 * Created by sorengoard on 01/11/16.
 */
public class CollectionWidgetProvider extends AppWidgetProvider {


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for(int appWidgetId : appWidgetIds) {
            Log.i("TAG", "update");
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.stock_collection_widget);

            //intent for MyStocks
//            Intent intent = new Intent(context, MyStocksActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
//            views.setOnClickPendingIntent(R.id.stock_collection_widget_list, pendingIntent);

            setRemoteAdapter(context, views);


            Intent widgetClickIntent = new Intent(context, MyStocksActivity.class);
            PendingIntent widgetClickPendingIntent = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(widgetClickIntent)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.stock_collection_widget_list, widgetClickPendingIntent);
            Log.v("LOGTAG", "LOG:clickintent" + widgetClickIntent);
//            views.setPendingIntentTemplate(R.id.stock_collection_widget_list, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);

        }
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent){
        super.onReceive(context, intent);
        //need a way to detect updates to the db
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(context, getClass())
        );
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.stock_collection_widget_list);
    }

    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.stock_collection_widget_list, new Intent(context, CollectionWidgetRemoteViewsService.class));
    }
}
