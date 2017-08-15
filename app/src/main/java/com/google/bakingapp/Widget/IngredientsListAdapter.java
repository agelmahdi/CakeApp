package com.google.bakingapp.Widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.bakingapp.Model.Ingredients;
import com.google.bakingapp.Model.Recipe;
import com.google.bakingapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
* Created by Ahmed El-Mahdi on 8/7/2017.
*/
class IngredientsListAdapter implements RemoteViewsService.RemoteViewsFactory {
    private static final String RECIPES ="recipes" ;
    private Recipe recipe;
    private Ingredients ingredients;
    private Context mContext;
    private List<Ingredients> mIngredients;
    private int mAppWidgetId;
    private static final String INGREDIENT_WIDGET = "Ingredient";


    public IngredientsListAdapter(Context applicationContext) {

        mContext =applicationContext;
    }

    @Override
    public void onCreate() {
        mIngredients=new ArrayList<>();

    }

    @Override
    public void onDataSetChanged() {

        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        Gson gson = new Gson();
        String json = appSharedPrefs.getString(INGREDIENT_WIDGET, "");
        if (json.isEmpty()){
            Log.d(" No Ingredients ", json);
        }
        else {
            Type type = new TypeToken<List<Ingredients>>(){}.getType();
            mIngredients = gson.fromJson(json, type);
        }

       // IngredientRequest(recipe.getId()-1);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (mIngredients == null || mIngredients.isEmpty()) return 0;
        return mIngredients.size();


    }

    @Override
    public RemoteViews getViewAt(int position) {
        Ingredients ingredients = mIngredients.get(position);
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
        views.setTextViewText(R.id.widget_ingredient_name, ingredients.getIngredient());
        views.setTextViewText(R.id.widget_ingredient_measure, ingredients.getMeasure());
        views.setTextViewText(R.id.widget_ingredient_quantity, ingredients.getQuantity());

        Bundle extras = new Bundle();

        extras.putInt(RecipeWidgetProvider.EXTRA_ITEM, position);

        Intent fillInIntent = new Intent();

        fillInIntent.putExtra("homescreen_meeting",ingredients);

        fillInIntent.putExtras(extras);

        // Make it possible to distinguish the individual on-click

        // action of a given item

        views.setOnClickFillInIntent(R.id.widget_ingredient_name, fillInIntent);

        // Return the RemoteViews object.

        return views;
    }



    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }


}
