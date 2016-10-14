package co.edu.udea.compumovil.grupo07.lab3weather;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by dfrancisco.hernandez on 27/09/16.
 */

public class MyWidgetProvider extends AppWidgetProvider {
    private static final String ACTION_CLICK = "ACTION_CLICK";
    private TextView city, temperature, humidity, description;
    private ImageView imClima;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        // Get all ids
        ComponentName thisWidget = new ComponentName(context, MyWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {
            // create some random data
            int number = (new Random().nextInt(100));

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            Log.w("WidgetExample", String.valueOf(number));
            // Set the text

            remoteViews.setTextViewText(R.id.ciudad1, String.valueOf(number));
            remoteViews.setTextViewText(R.id.temperatura1, String.valueOf(number));
            remoteViews.setTextViewText(R.id.humedad1, String.valueOf(number));
            remoteViews.setTextViewText(R.id.clima_image1, String.valueOf(number));
            // Register an onClickListener
            Intent intent = new Intent(context, MyWidgetProvider.class);

            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.ciudad1, pendingIntent);
            remoteViews.setOnClickPendingIntent(R.id.temperatura1, pendingIntent);
            remoteViews.setOnClickPendingIntent(R.id.humedad1, pendingIntent);
            remoteViews.setOnClickPendingIntent(R.id.clima_image1, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }

    }
}
