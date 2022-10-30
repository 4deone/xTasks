package com.deone.extrmtasks.adapters;

import static com.deone.extrmtasks.tools.Ivtools.loadingImageWithPath;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deone.extrmtasks.R;
import com.deone.extrmtasks.modeles.Tache;
import com.deone.extrmtasks.tools.Xlistener;

import java.util.List;

/**
 *
 */
public class Sadapter extends RecyclerView.Adapter<Sadapter.Holder>{

    private final Context appContext;
    private Xlistener listener;
    private final List<Tache> tacheList;

    /**
     *
     * @param appContext
     * @param tacheList
     */
    public Sadapter(Context appContext, List<Tache> tacheList) {
        this.appContext = appContext;
        this.tacheList = tacheList;
    }

    @NonNull
    @Override
    public Sadapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_taches_list, parent, false);
        return  new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Sadapter.Holder holder, int position) {
        String tCover = tacheList.get(position).getTcover();
        loadingImageWithPath(holder.ivTachesLogoSettings, R.drawable.wild, tCover);
    }

    @Override
    public int getItemCount() {
        return tacheList.size();
    }

    /**
     *
     * @param listener
     */
    public void setListener(Xlistener listener) {
        this.listener = listener;
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {

        ImageView ivTachesLogoSettings;

        public Holder(@NonNull View itemView) {
            super(itemView);
            ivTachesLogoSettings = itemView.findViewById(R.id.ivTachesLogoSettings);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                listener.onItemClick(v, position);
            }
        }


        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                listener.onLongItemClick(v, position);
            }
            return true;
        }
    }
}
