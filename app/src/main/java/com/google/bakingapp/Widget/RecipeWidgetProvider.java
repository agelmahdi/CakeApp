package com.google.bakingapp.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.bakingapp.DetailsActivity;
import com.google.bakingapp.R;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {
    private static final String RECIPE = "detail_recipe";
    public static final String EXTRA_ITEM = "com.google.bakingapp.EXTRA_ITEM";
  /*  static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        Intent intent = new Intent(context, IngredientListFactory.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.recipe_app_widget);
        rv.setRemoteAdapter( R.id.ingredient_listview, intent);
        Intent appIntent = new Intent(context, DetailsFragment.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setPendingIntentTemplate(R.id.ingredient_listview, appPendingIntent);
        rv.setEmptyView(R.id.ingredient_listview,R.id.empty_view);
        appWidgetManager.updateAppWidget(appWidgetId,rv);

    }*/

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int i = 0; i < appWidgetIds.length; ++i) {
            Intent intent = new Intent(context, IngredientListFactory.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.recipe_app_widget);
            rv.setRemoteAdapter(appWidgetIds[i], R.id.ingredient_listview, intent);
            Intent startActivityIntent = new Intent(context,DetailsActivity.class);
            PendingIntent startActivityPendingIntent = PendingIntent.getActivity(context, 0, startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.ingredient_listview, startActivityPendingIntent);
            rv.setEmptyView(R.id.ingredient_listview, R.id.empty_view);
            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);

    }



    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        if(intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)){
            int appWidgetIds[] = mgr.getAppWidgetIds(new ComponentName(context,RecipeWidgetProvider.class));
            mgr.notifyAppWidgetViewDataChanged(appWidgetIds,R.id.ingredient_listview);
            Log.d("received", intent.getAction());

        }
        super.onReceive(context, intent);

    }
    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

