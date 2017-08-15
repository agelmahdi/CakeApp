package com.google.bakingapp.Adpater;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.bakingapp.Model.Steps;
import com.google.bakingapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ahmed El-Mahdi on 7/27/2017.
 */

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.Viewholder>  {
    private ArrayList<Steps> mSteps = new ArrayList<>();
    private final StepsOnClickHandler mStepOnClickHandler;
    private Context context;

    public StepsAdapter(ArrayList<Steps> mSteps, StepsOnClickHandler mStepOnClickHandler,Context context) {
        this.mSteps=mSteps;
        this.mStepOnClickHandler = mStepOnClickHandler;
        this.context = context;
    }

    public interface StepsOnClickHandler {
        void onClickStep(Steps Step, ArrayList<Steps> list, int position);
    }
    @Override
    public StepsAdapter.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.steps_list_item, parent, false);
        return new StepsAdapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(final StepsAdapter.Viewholder holder, int position) {

        final Steps steps = mSteps.get(position);

        holder.stepName.setText(steps.getShortDescription());
        if (steps.getThumbnailURL().isEmpty()) { //url.isEmpty()
            Picasso.with(context)
                    .load(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(holder.stepImage);

        }else{
            Picasso.with(context)
                    .load(steps.getThumbnailURL())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(holder.stepImage);
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStepOnClickHandler.onClickStep(steps,mSteps,holder.getAdapterPosition());

            }
        });
    }
    public void add(List<Steps> list){
        mSteps.clear();
        mSteps.addAll(list);
        notifyDataSetChanged();
    }


    public ArrayList<Steps> getSteps() {
        return mSteps;
    }


    @Override
    public int getItemCount() {
        return mSteps.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView stepName;
        ImageView stepImage;
        public final View mView;
        public Viewholder(View itemView) {
            super(itemView);
            stepName = (TextView)itemView.findViewById(R.id.step_title);
            stepImage = (ImageView)itemView.findViewById(R.id.step_image);
            mView=itemView;
        }
    }
}
