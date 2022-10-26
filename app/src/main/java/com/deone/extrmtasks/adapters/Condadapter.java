package com.deone.extrmtasks.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deone.extrmtasks.R;
import com.deone.extrmtasks.modeles.Condition;

import java.util.List;

/**
 *
 */
public class Condadapter extends RecyclerView.Adapter<Condadapter.Holder>{

    private final List<Condition> conditionList;

    /**
     *
     * @param conditionList
     */
    public Condadapter(List<Condition> conditionList) {
        this.conditionList = conditionList;
    }

    @NonNull
    @Override
    public Condadapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_condition, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Condadapter.Holder holder, int position) {
        String titre = conditionList.get(position).getTitre();
        String description = conditionList.get(position).getDescription();

        Log.e("Conditions -description", ""+description);

        holder.tvConditionTitre.setText(titre);
        holder.tvConditionDescription.setText(description);
    }

    @Override
    public int getItemCount() {
        return conditionList.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {

        TextView tvConditionTitre;
        TextView tvConditionDescription;

        public Holder(@NonNull View itemView) {
            super(itemView);
            tvConditionTitre = itemView.findViewById(R.id.tvConditionTitre);
            tvConditionDescription = itemView.findViewById(R.id.tvConditionDescription);
        }

    }
}
