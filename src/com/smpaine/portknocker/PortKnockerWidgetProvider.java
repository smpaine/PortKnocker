package com.smpaine.portknocker;

/**
 * Port Knocker A port knocking application for android Based off of the
 * original PortKnocking application by Alexis Robert Under GPL 3 License
 * http://www.gnu.org/licenses/gpl.txt
 * 
 * Copyright Stephen Paine 2009-11
 */

import com.smpaine.portknocker.R;
import android.app.PendingIntent;
import android.appwidget.*;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class PortKnockerWidgetProvider extends AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];

            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context, PortKnockerWidgetProvider.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            // Get the layout for the App Widget and attach an on-click listener to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setOnClickPendingIntent(R.id.Button01, pendingIntent);
            
            views.setTextViewText(R.id.TextView01, "appWidgetId="+Integer.toString(appWidgetId));

            // Tell the AppWidgetManager to perform an update on the current App Widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
