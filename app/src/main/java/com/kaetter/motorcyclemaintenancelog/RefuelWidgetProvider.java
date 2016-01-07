package com.kaetter.motorcyclemaintenancelog;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class RefuelWidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
	
		
		for (int i = 0; i < appWidgetIds.length; i++) {
			
		 AppWidgetManager mgr = AppWidgetManager.getInstance(context);
		 
		 int appWidgetId = appWidgetIds[i];
		 
		 Intent intent = new Intent(context,RefuelActivity.class); 
		 
		 intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

		 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 PendingIntent myPI = PendingIntent.getActivity(context, 0, intent, 0); 

		 RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.refuelwidget);
		 views.setOnClickPendingIntent(R.id.refuel_linear_layout, myPI); 
		 
		 mgr.updateAppWidget(appWidgetId, views); 
		 
		}

		
	}

	@Override
	public void onEnabled(Context context) {

		    
		super.onEnabled(context);
		
		
		
		
		
		
		
	}
	
	

}
