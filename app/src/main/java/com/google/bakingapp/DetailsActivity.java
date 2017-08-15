package com.google.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.bakingapp.Model.Recipe;

public class DetailsActivity extends AppCompatActivity {


    private Recipe recipe;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        recipe = intent.getParcelableExtra("detail_recipe");
        Toolbar myToolbar = (Toolbar) findViewById(R.id.details_toolbar);
        if (findViewById(R.id.two_pane_layout_act) != null) {
            mTwoPane = true;
        }
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(recipe.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fragmentManager = getSupportFragmentManager();
        DetailsFragment recipeDetailsFragment = (DetailsFragment) fragmentManager
                .findFragmentById(R.id.recipe_details_container);
        if (recipeDetailsFragment == null) {
            recipeDetailsFragment = new DetailsFragment();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.recipe_details_container, recipeDetailsFragment);
            transaction.commit();
        }
        if (mTwoPane) {
            recipeDetailsFragment = new DetailsFragment();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.recipe_step_container, recipeDetailsFragment);
            transaction.commit();
        }

    }


}
