package com.google.bakingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.bakingapp.Adpater.RecipeAdapter;
import com.google.bakingapp.Model.Recipe;
import com.google.bakingapp.Widget.RecipeWidgetProvider;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.RecipeOnClickHandler {
    private static final String RECIPE = "detail_recipe";
    private static final String RECIPES = "recipes";
    private RecyclerView recyclerView;
    private ArrayList<Recipe> mRecipes;
    private RecipeAdapter recipeAdapter;

    private ProgressDialog pDialog;
    private Recipe recipe;
    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView netError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(RECIPES)) {
                mRecipes = savedInstanceState.getParcelableArrayList(RECIPES);

            }
        }
        netError = (TextView) findViewById(R.id.net_work_error_recipe);

        mRecipes = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recipeAdapter = new RecipeAdapter(mRecipes, this, this);
        recyclerView.setAdapter(recipeAdapter);


    }

    @Override
    public void onStart() {
        super.onStart();
        RecipeRequest();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Recipe> recipes = recipeAdapter.getRecipes();
        if (recipes != null && !recipes.isEmpty()) {
            outState.putParcelableArrayList(RECIPES, recipes);
        }
    }

    @Override
    public void onClickRecipe(Recipe id, int po) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(id);
        prefsEditor.putString(RECIPES, json);
        prefsEditor.apply();
        RecipeWidgetProvider recipeWidgetProvider;

        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(RECIPE, id);

        startActivity(intent);

    }

    private void RecipeRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);

        pDialog.setMessage("Loading  ...");

        showDialog();

        JsonArrayRequest req = new JsonArrayRequest(AppConfig.URL_RECIPE,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        netError.setVisibility(View.INVISIBLE);

                        Log.d(TAG, response.toString());
                        ArrayList<Recipe> recipeArrayList = new ArrayList<>();

                        try {
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject jsonObject = response.getJSONObject(i);
                                recipe = new Recipe(jsonObject);
                                recipeArrayList.add(recipe);
                            }

                            if (recipeArrayList != null) {
                                if (recipeAdapter != null) {
                                    recipeAdapter.add(recipeArrayList);
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this,
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                        hideDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                netError.setVisibility(View.VISIBLE);
                hideDialog();
            }
        });

        // Adding request to request queue
        queue.add(req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
