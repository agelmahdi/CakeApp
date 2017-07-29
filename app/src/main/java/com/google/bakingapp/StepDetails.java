package com.google.bakingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.google.bakingapp.Model.Steps;

public class StepDetails extends AppCompatActivity {
private Steps steps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_steps);
        Intent intent = getIntent();
        steps = intent.getParcelableExtra("detail_step");
        Toolbar myToolbar = (Toolbar) findViewById(R.id.step_details_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(steps.getShortDescription());
    }
}
