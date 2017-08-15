package com.google.bakingapp.Widget;

import android.content.Intent;
import android.widget.RemoteViewsService;


/**
 * Created by Ahmed El-Mahdi on 8/2/2017.
 */

public class IngredientListFactory extends RemoteViewsService {


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new IngredientsListAdapter(this.getApplicationContext());
    }
}

