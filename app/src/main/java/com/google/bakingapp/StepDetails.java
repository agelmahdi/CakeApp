package com.google.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.bakingapp.Adpater.StepsAdapter;
import com.google.bakingapp.Model.Steps;

import java.util.ArrayList;

public class StepDetails extends AppCompatActivity implements StepsAdapter.StepsOnClickHandler {
    private Steps steps;
    private ArrayList<Steps> mSteps = new ArrayList<>();
    private RecyclerView recyclerViewVideos;
    private StepsAdapter videoAdapter;
    private FragmentManager fragmentManager;
    private DetailsStepFragment recipeStepFragment;
    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_steps);
        Intent intent = getIntent();
        steps = intent.getParcelableExtra("detail_step");
        mSteps = intent.getParcelableArrayListExtra("steps");
        Toolbar myToolbar = (Toolbar) findViewById(R.id.step_details_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        recyclerViewVideos = (RecyclerView) findViewById(R.id.recycler_view_videos);

        fragmentManager = getSupportFragmentManager();
        recipeStepFragment = (DetailsStepFragment) fragmentManager
                .findFragmentById(R.id.recipe_step_container);


        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewVideos.setLayoutManager(layoutManager);
        recyclerViewVideos.setHasFixedSize(true);

        videoAdapter = new StepsAdapter(new ArrayList<Steps>(), this, this);
        recyclerViewVideos.setAdapter(videoAdapter);
        if (mSteps != null) {
            if (videoAdapter != null) {
                videoAdapter.add(mSteps);
            }
        }
        if (steps == null) {
            Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (recipeStepFragment == null) {
            getSupportActionBar().setTitle(steps.getShortDescription());
            recipeStepFragment = DetailsStepFragment.newInstance(steps);
            transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.recipe_step_container, recipeStepFragment);
            transaction.commit();

        }


    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public void onClickStep(Steps Step, ArrayList<Steps> list, int position) {
        if (recipeStepFragment != null) {
            transaction = fragmentManager.beginTransaction();
            transaction.remove(recipeStepFragment).commit();
        }
        getSupportActionBar().setTitle(Step.getShortDescription());
        recipeStepFragment = DetailsStepFragment.newInstance(Step);
        FragmentTransaction transaction;
        transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.recipe_step_container, recipeStepFragment);
        transaction.commit();
    }
}
