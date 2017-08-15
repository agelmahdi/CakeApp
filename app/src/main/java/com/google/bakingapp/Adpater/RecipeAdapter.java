package com.google.bakingapp.Adpater;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.bakingapp.Model.Recipe;
import com.google.bakingapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ahmed El-Mahdi on 7/23/2017.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
    private ArrayList<Recipe> mRecipes = new ArrayList<>();

    private Context mContext;
    private final RecipeOnClickHandler mRecipeOnClickHandler;
    public RecipeAdapter(ArrayList<Recipe> list, Context mContext, RecipeOnClickHandler mRecipeOnClickHandler) {
        this.mContext = mContext;
        this.mRecipes =list;
        this.mRecipeOnClickHandler = mRecipeOnClickHandler;
    }


    public interface RecipeOnClickHandler {
        void onClickRecipe(Recipe recipe,int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.recipe_list_item, parent, false);
        return new RecipeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {


        final Recipe recipe = mRecipes.get(position);
        holder.itemView.setTag(recipe.getId());
        holder.title.setText(recipe.getName());
        holder.servings.setText(recipe.getServings());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecipeOnClickHandler.onClickRecipe(recipe,holder.getAdapterPosition());
            }
        });
       /* Picasso.with(mContext)
                .load(recipe.getImage())
                .placeholder(R.drawable.placeholder)
                .into(holder.image);*/
        if (recipe.getImage().isEmpty()) { //url.isEmpty()
            Picasso.with(mContext)
                    .load(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(holder.image);

        }else{
            Picasso.with(mContext)
                    .load(recipe.getImage())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(holder.image);
        }

    }

    public void add(List<Recipe> recipes) {
        mRecipes.clear();
        mRecipes.addAll(recipes);
        notifyDataSetChanged();
    }
    public ArrayList<Recipe> getRecipes() {
        return mRecipes;
    }
    @Override
    public int getItemCount() {
        return mRecipes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView title, servings ;
        public ImageView image;

        public final View mView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.title = (TextView) itemView.findViewById(R.id.recipe_title);
            this.servings = (TextView)itemView.findViewById(R.id.recipe_servings);
            this.image = (ImageView)itemView.findViewById(R.id.recipe_image);
            mView=itemView;
        }


    }
}
