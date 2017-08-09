package com.google.bakingapp.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.bakingapp.MainActivity;
import com.google.bakingapp.R;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {
    private static final String RECIPE = "detail_recipe";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.recipe_app_widget);
            Intent adapterIntent = new Intent(context, IngredientListFactory.class);
            remoteViews.setRemoteAdapter(R.id.ingredient_listview, adapterIntent);



        Intent app = new Intent(context,MainActivity.class);
        PendingIntent appLauncher = PendingIntent.getActivity(context,0,app,0);
        remoteViews.setPendingIntentTemplate(R.id.ingredient_listview,appLauncher);
        Log.d("widget","updated AppWidget");
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,R.id.ingredient_listview);
        appWidgetManager.updateAppWidget(appWidgetId,remoteViews);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for(int widget:appWidgetIds){
            updateAppWidget(context,appWidgetManager,widget);
        }    }



    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if(intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)){
            updateAppWidget(context,AppWidgetManager.getInstance(context),intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,0));
        }
    }
    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

