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
import com.google.bakingapp.Adpater.IngredientAdapter;
import com.google.bakingapp.Model.Ingredients;
import com.google.bakingapp.Model.Recipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Ahmed El-Mahdi on 7/27/2017.
 */

public class IngredientFragment extends Fragment {
    private static final String INGREDIENTS = "Ingredients";
    private RecyclerView recyclerView;
    private ArrayList<Ingredients> mIngredientses;
    private IngredientAdapter ingredientAdapter;
    private ProgressDialog pDialog;
    private Ingredients ingredients;
    private Recipe recipe;

    private TextView netError;
    private static final String TAG = DetailsActivity.class.getSimpleName();
    public IngredientFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ingredient, container, false);
        if (savedInstanceState != null) {

            if (savedInstanceState.containsKey(INGREDIENTS)) {
                mIngredientses = savedInstanceState.getParcelableArrayList(INGREDIENTS);

            }
        }
        netError =(TextView)rootView.findViewById(R.id.net_work_error_ing);
        Intent intent = getActivity().getIntent();
        recipe = intent.getParcelableExtra("detail_recipe");
        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);
        mIngredientses = new ArrayList<>();
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view_ingredient);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        ingredientAdapter = new IngredientAdapter( mIngredientses);
        recyclerView.setAdapter(ingredientAdapter);
        IngredientRequest();

        return rootView;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Ingredients> ingredients = ingredientAdapter.getIngredients();
        if (ingredients != null && !ingredients.isEmpty()) {
            outState.putParcelableArrayList(INGREDIENTS, ingredients);
        }
    }
    private void IngredientRequest() {
        RequestQueue queue = Volley.newRequestQueue(getContext());

        pDialog.setMessage("Loading  ...");

        showDialog();

        JsonArrayRequest req = new JsonArrayRequest(AppConfig.URL_RECIPE,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        netError.setVisibility(View.INVISIBLE);

                        Log.d(TAG, response.toString());
                        ArrayList<Ingredients> ingredientsArrayList = new ArrayList<>();

                        try {
                            JSONObject jsonObject = response.getJSONObject(recipe.getId()-1);
                            JSONArray array = jsonObject.getJSONArray("ingredients");
                            for (int j = 0; j < array.length(); j++) {
                                JSONObject object = array.getJSONObject(j);

                                ingredients = new Ingredients(object);
                                ingredientsArrayList.add(ingredients);
                            }

                            if (ingredientsArrayList!=null){
                                if (ingredientAdapter != null) {
                                    ingredientAdapter.add(ingredientsArrayList);
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
