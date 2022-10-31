package com.deone.extrmtasks.adapters;

import static com.deone.extrmtasks.tools.Constants.FRAGMENT_ACCOUNT;
import static com.deone.extrmtasks.tools.Constants.IDFRAGMENT;
import static com.deone.extrmtasks.tools.Constants.UID;
import static com.deone.extrmtasks.tools.Ivtools.loadingImageWithPath;
import static com.deone.extrmtasks.tools.Other.formatLaDate;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deone.extrmtasks.R;
import com.deone.extrmtasks.TempActivity;
import com.deone.extrmtasks.modeles.Commentaire;
import com.deone.extrmtasks.tools.Fbtools;
import com.deone.extrmtasks.tools.Xlistener;

import java.util.List;

/**
 *
 */
public class Kadapter extends RecyclerView.Adapter<Kadapter.Holder> {

    private final Context appContext;
    private final List<String> stringList;

    /**
     *
     * @param appContext
     * @param stringList
     */
    public Kadapter(Context appContext, List<String> stringList) {
        this.appContext = appContext;
        this.stringList = stringList;
    }

    @NonNull
    @Override
    public Kadapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_key, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Kadapter.Holder holder, int position) {
        String key = stringList.get(position);

        holder.tvKeyItem.setText(key);
        holder.ibKeyItem.setOnClickListener(view -> {
            //deleteItem();
        });
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {

        ImageButton ibKeyItem;
        TextView tvKeyItem;

        public Holder(@NonNull View itemView) {
            super(itemView);
            ibKeyItem = itemView.findViewById(R.id.ibKeyItem);
            tvKeyItem = itemView.findViewById(R.id.tvKeyItem);
        }
    }
}
