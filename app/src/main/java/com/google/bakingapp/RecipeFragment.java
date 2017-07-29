package com.google.bakingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class RecipeFragment extends Fragment implements RecipeAdapter.RecipeOnClickHandler {

    private static final String RECIPES ="recipes" ;
    private RecyclerView recyclerView;
    private ArrayList<Recipe> mRecipes;
    private RecipeAdapter recipeAdapter;

    private ProgressDialog pDialog;
    private Recipe recipe;
    private static final String TAG = RecipeFragment.class.getSimpleName();

    private TextView netError;
    public RecipeFragment() {
        // Required empty public constructor
    }
    @Override
    public void onClickRecipe(Recipe id,int po) {

        Intent intent = new Intent(getContext(), DetailsActivity.class);
        intent.putExtra("detail_recipe",id);
        startActivity(intent);

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =inflater.inflate(R.layout.fragment_recipe, container, false);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(RECIPES)) {
                mRecipes = savedInstanceState.getParcelableArrayList(RECIPES);

            }
        }
        netError =(TextView)rootView.findViewById(R.id.net_work_error_recipe);

                mRecipes = new ArrayList<>();
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recipeAdapter = new RecipeAdapter( mRecipes, getContext(), this);
        recyclerView.setAdapter(recipeAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        RecipeRequest();
    }



    private void RecipeRequest() {
        RequestQueue queue = Volley.newRequestQueue(getContext());

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

                            if (recipeArrayList!=null){
                                if (recipeAdapter != null) {
                                    recipeAdapter.add(recipeArrayList);
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(),
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
