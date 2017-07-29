package com.google.bakingapp.Adpater;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.bakingapp.Model.Ingredients;
import com.google.bakingapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ahmed El-Mahdi on 7/24/2017.
 */

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.Viewholder> {
    private ArrayList<Ingredients> mIngredientses = new ArrayList<>();

    public IngredientAdapter(ArrayList<Ingredients> mIngredientses) {
        this.mIngredientses=mIngredientses;
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ingredient_list_item, parent, false);
        return new IngredientAdapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(Viewholder holder, int position) {
        final Ingredients ingredients = mIngredientses.get(position);
        holder.quantity.setText( ingredients.getQuantity());
        holder.measure.setText(ingredients.getMeasure());
        holder.name.setText(ingredients.getIngredient());


    }

    public void add(List<Ingredients> list){
        mIngredientses.clear();
        mIngredientses.addAll(list);
        notifyDataSetChanged();
    }

    public ArrayList<Ingredients> getIngredients() {
        return mIngredientses;
    }
    @Override
    public int getItemCount() {
        return mIngredientses.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView name,measure,quantity;
        public Viewholder(View itemView) {
            super(itemView);
            this.quantity = (TextView)itemView.findViewById(R.id.ingredient_quantity);
            this.measure=(TextView)itemView.findViewById(R.id.ingredient_measure);
            this.name=(TextView)itemView.findViewById(R.id.ingredient_name);
        }
    }
}
