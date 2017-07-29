package com.google.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.bakingapp.Adpater.ViewPagerAdapter;
import com.google.bakingapp.Model.Recipe;

public class DetailsActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private Recipe recipe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        recipe = intent.getParcelableExtra("detail_recipe");
        Toolbar myToolbar = (Toolbar) findViewById(R.id.details_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(recipe.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new IngredientFragment(), "Ingredients");
        adapter.addFragment(new StepsFragment(), "Steps");
        viewPager.setAdapter(adapter);
    }







}
