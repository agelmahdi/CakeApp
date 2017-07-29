package com.google.bakingapp.Adpater;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.bakingapp.Model.Steps;
import com.google.bakingapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ahmed El-Mahdi on 7/27/2017.
 */

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.Viewholder>  {
    private ArrayList<Steps> mSteps = new ArrayList<>();
    private final StepsOnClickHandler mStepOnClickHandler;

    public StepsAdapter(ArrayList<Steps> mSteps, StepsOnClickHandler mStepOnClickHandler) {
        this.mSteps=mSteps;
        this.mStepOnClickHandler = mStepOnClickHandler;
    }

    public interface StepsOnClickHandler {
        void onClickStep(Steps Step, int position);
    }
    @Override
    public StepsAdapter.Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.steps_list_item, parent, false);
        return new StepsAdapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(final StepsAdapter.Viewholder holder, int position) {

        final Steps steps = mSteps.get(position);

        holder.stepName.setText(steps.getShortDescription());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStepOnClickHandler.onClickStep(steps,holder.getAdapterPosition());

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
        public final View mView;
        public Viewholder(View itemView) {
            super(itemView);
            stepName = (TextView)itemView.findViewById(R.id.step_title);
            mView=itemView;
        }
    }
}
