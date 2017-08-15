package com.google.bakingapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Ahmed El-Mahdi on 7/23/2017.
 */

public class Recipe implements Parcelable {

    private int id;
    private String name;
    private String servings;
    private String image;

    protected Recipe(Parcel in) {
        id = in.readInt();
        name = in.readString();
        servings = in.readString();
        image = in.readString();
        ingredient = in.createTypedArrayList(Ingredients.CREATOR);
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public List<Ingredients> getIngredient() {
        return ingredient;
    }

    private List<Ingredients> ingredient;


    public Recipe(JSONObject recipe) throws JSONException {

        this.id = recipe.getInt("id");
        this.name = recipe.getString("name");
        this.servings = recipe.getString("servings");
        this.image = recipe.getString("image");
    }


    public int getId() {
        return id;
    }


    public String getImage() {
        return image;
    }

    public String getServings() {
        return servings;
    }

    public String getName() {
        return name;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(servings);
        dest.writeString(image);
        dest.writeTypedList(ingredient);
    }
}
