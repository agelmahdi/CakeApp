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
import com.google.bakingapp.Adpater.StepsAdapter;
import com.google.bakingapp.Model.Recipe;
import com.google.bakingapp.Model.Steps;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Ahmed El-Mahdi on 7/27/2017.
 */

public class StepsFragment extends Fragment implements StepsAdapter.StepsOnClickHandler{
    private static final String STEPS = "steps";
    private RecyclerView recyclerView;
    private ArrayList<Steps> mSteps;
    private StepsAdapter stepsAdapter;
    private Steps steps;
    private Recipe recipe;
    private ProgressDialog pDialog;

    private TextView netError;
    public StepsFragment() {
        // Required empty public constructor
    }
    @Override
    public void onClickStep(Steps Step, int position) {
        Intent intent = new Intent(getContext(), StepDetails.class);
        intent.putExtra("detail_step",Step);
        startActivity(intent);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_steps, container, false);
        if (savedInstanceState != null) {
        if (savedInstanceState.containsKey(STEPS)) {
            mSteps = savedInstanceState.getParcelableArrayList(STEPS);
        }
        }
        netError = (TextView)rootView.findViewById(R.id.net_work_error_steps);
        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);
        Intent intent = getActivity().getIntent();
        recipe = intent.getParcelableExtra("detail_recipe");
        // Inflate the layout for this fragment
        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);
        mSteps = new ArrayList<>();
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view_steps);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        stepsAdapter = new StepsAdapter( mSteps, this);
        recyclerView.setAdapter(stepsAdapter);
        StepsRequest();

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Steps> steps = stepsAdapter.getSteps();
        if (steps != null && !steps.isEmpty()) {
            outState.putParcelableArrayList(STEPS, steps);
        }
    }

    private void StepsRequest() {
        RequestQueue queue = Volley.newRequestQueue(getContext());

        pDialog.setMessage("Loading  ...");

        showDialog();

        JsonArrayRequest req = new JsonArrayRequest(AppConfig.URL_RECIPE,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        netError.setVisibility(View.INVISIBLE);
                        Log.d(TAG, response.toString());
                        ArrayList<Steps> stepsArrayList = new ArrayList<>();

                        try {


                            JSONObject jsonObject = response.getJSONObject(recipe.getId()-1);
                            JSONArray array = jsonObject.getJSONArray("steps");
                            for (int j = 0; j < array.length(); j++) {
                                JSONObject object = array.getJSONObject(j);

                                steps = new Steps(object);
                                stepsArrayList.add(steps);
                            }

                            if (stepsArrayList!=null){
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


}
