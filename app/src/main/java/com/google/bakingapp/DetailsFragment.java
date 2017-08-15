package com.google.bakingapp;

import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.google.bakingapp.Adpater.StepsAdapter;
import com.google.bakingapp.Model.Ingredients;
import com.google.bakingapp.Model.Recipe;
import com.google.bakingapp.Model.Steps;
import com.google.bakingapp.Widget.RecipeWidgetProvider;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Ahmed El-Mahdi on 7/27/2017.
 */

public class DetailsFragment extends Fragment implements StepsAdapter.StepsOnClickHandler {
    private static final String INGREDIENTS = "Ingredients";
    private static final String INGREDIENT_WIDGET = "Ingredient";
    private static final String RECIPE = "detail_recipe";
    private static final String STEPS = "steps";
    private static final String SAVED_LAYOUT_MANAGER = "save";

    private RecyclerView recyclerViewIngredients;
    private RecyclerView recyclerViewSteps;
    private ArrayList<Steps> mSteps;
    private StepsAdapter stepsAdapter;
    private Steps steps;

    private ArrayList<Ingredients> mIngredientses;
    private IngredientAdapter ingredientAdapter;
    private ProgressDialog pDialog;
    private Ingredients ingredients;
    private Recipe recipe;
    private boolean mTwoPane;
    private TextView netError;
    private LinearLayoutManager layoutManager;
    private static Bundle mBundleRecyclerViewState;

    private static final String TAG = DetailsActivity.class.getSimpleName();

    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        if (rootView.findViewById(R.id.two_pane_layout) != null) {
            mTwoPane = true;
        }
        if (savedInstanceState != null) {


            if (savedInstanceState.containsKey(INGREDIENTS)) {
                mIngredientses = savedInstanceState.getParcelableArrayList(INGREDIENTS);

            } else if (savedInstanceState.containsKey(STEPS)) {
                mSteps = savedInstanceState.getParcelableArrayList(STEPS);
            }
        }
        netError = (TextView) rootView.findViewById(R.id.net_work_error_ing);
        recyclerViewSteps = (RecyclerView) rootView.findViewById(R.id.recycler_view_steps);
        Intent intent = getActivity().getIntent();
        recipe = intent.getParcelableExtra(RECIPE);
        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);
        mIngredientses = new ArrayList<>();
        recyclerViewIngredients = (RecyclerView) rootView.findViewById(R.id.recycler_view_ingredient);
        layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewIngredients.setLayoutManager(layoutManager);
        recyclerViewIngredients.setHasFixedSize(true);
        ingredientAdapter = new IngredientAdapter(mIngredientses);
        recyclerViewIngredients.setAdapter(ingredientAdapter);
        mSteps = new ArrayList<>();


        stepsAdapter = new StepsAdapter(mSteps, this, getContext());
        recyclerViewSteps.setAdapter(stepsAdapter);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.widget_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences appSharedPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                ArrayList<Ingredients> ingredients = ingredientAdapter.getIngredients();

                Gson gson = new Gson();
                String json = gson.toJson(ingredients);
                prefsEditor.putString(INGREDIENT_WIDGET, json);
                prefsEditor.apply();

                Intent intentUpdate = new Intent(getContext(), RecipeWidgetProvider.class);
                intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

                getContext().sendBroadcast(intentUpdate);

                Toast.makeText(getContext(), R.string.message_widget_added, Toast.LENGTH_LONG).show();
            }
        });

        return rootView;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ArrayList<Ingredients> ingredientslist
                = ingredientAdapter.getIngredients();

        if (ingredientslist != null && !ingredientslist.isEmpty()) {
            outState.putParcelableArrayList(INGREDIENTS, ingredientslist);
        }
        ArrayList<Steps> steps = stepsAdapter.getSteps();
        if (steps != null && !steps.isEmpty()) {
            outState.putParcelableArrayList(STEPS, steps);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        DetailsRequest();
    }

    @Override
    public void onPause() {
        super.onPause();

        // save RecyclerView state
        mBundleRecyclerViewState = new Bundle();
        Parcelable listState = recyclerViewSteps.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(SAVED_LAYOUT_MANAGER, listState);

    }

    @Override
    public void onResume() {
        super.onResume();

        // restore RecyclerView state
        if (mBundleRecyclerViewState != null) {
            Parcelable listState = mBundleRecyclerViewState.getParcelable(SAVED_LAYOUT_MANAGER);
            recyclerViewSteps.getLayoutManager().onRestoreInstanceState(listState);
        }

    }

    private void DetailsRequest() {
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
                        ArrayList<Steps> stepsArrayList = new ArrayList<>();

                        try {
                            JSONObject jsonObject = response.getJSONObject(recipe.getId() - 1);
                            JSONArray array = jsonObject.getJSONArray("ingredients");
                            for (int j = 0; j < array.length(); j++) {
                                JSONObject object = array.getJSONObject(j);

                                ingredients = new Ingredients(object);
                                ingredientsArrayList.add(ingredients);
                            }

                            if (ingredientsArrayList != null) {
                                if (ingredientAdapter != null) {
                                    ingredientAdapter.add(ingredientsArrayList);
                                }
                            }
                            JSONArray arrayStep = jsonObject.getJSONArray("steps");
                            for (int j = 0; j < arrayStep.length(); j++) {
                                JSONObject object = arrayStep.getJSONObject(j);

                                steps = new Steps(object);
                                stepsArrayList.add(steps);
                            }

                            if (stepsArrayList != null) {
                                if (stepsAdapter != null) {
                                    stepsAdapter.add(stepsArrayList);
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

    @Override
    public void onClickStep(Steps Step, ArrayList<Steps> list, int position) {
        if (!mTwoPane) {
            Intent intent = new Intent(getContext(), StepDetails.class);
            intent.putExtra("detail_step", Step);
            intent.putParcelableArrayListExtra("steps", stepsAdapter.getSteps());
            startActivity(intent);
        } else {
            DetailsStepFragment recipeStepFragment = DetailsStepFragment.newInstance(Step);
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.recipe_step_container, recipeStepFragment);
            transaction.commit();
        }

    }
}
