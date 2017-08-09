package com.google.bakingapp.Widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.bakingapp.AppConfig;
import com.google.bakingapp.Model.Ingredients;
import com.google.bakingapp.Model.Recipe;
import com.google.bakingapp.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
* Created by Ahmed El-Mahdi on 8/7/2017.
*/
class IngredientsListAdapter implements RemoteViewsService.RemoteViewsFactory {
    private static final String INGREDIENTS = "Ingredients";
    private static final String RECIPES = "recipe";
    private Recipe recipe;
    private Ingredients ingredients;
    private Context mContext;
    private List<Ingredients> mIngredients = new ArrayList<>();
    Intent intent = new Intent();

    public IngredientsListAdapter(Context applicationContext) {
        mContext =applicationContext;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        Gson gson = new Gson();
        String json = appSharedPrefs.getString(RECIPES, "");
         recipe = gson.fromJson(json, Recipe.class);
        IngredientRequest();
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
        views.setTextViewText(R.id.widget_ingredient_measure, ingredients.getMeasure());
        return views;
    }

    private void IngredientRequest() {
        RequestQueue queue = Volley.newRequestQueue(mContext);



        JsonArrayRequest req = new JsonArrayRequest(AppConfig.URL_RECIPE,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.d(TAG, response.toString());

                        try {
                            JSONObject jsonObject = response.getJSONObject(recipe.getId()-1);
                            JSONArray array = jsonObject.getJSONArray("ingredients");
                            for (int j = 0; j < array.length(); j++) {
                                JSONObject object = array.getJSONObject(j);

                                ingredients = new Ingredients(object);
                                mIngredients.clear();
                                mIngredients.add(ingredients);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(mContext,
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        queue.add(req);
    }
    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
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
