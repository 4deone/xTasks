package com.deone.extrmtasks.adapters;

import static com.deone.extrmtasks.tools.Ivtools.loadingImageWithPath;
import static com.deone.extrmtasks.tools.Other.checkBeforeFormatData;
import static com.deone.extrmtasks.tools.Other.formatLaDate;
import static com.deone.extrmtasks.tools.Other.isStringEmpty;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deone.extrmtasks.R;
import com.deone.extrmtasks.modeles.Taches;
import com.deone.extrmtasks.tools.Xlistener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 *
 */
public class Tadapter extends RecyclerView.Adapter<Tadapter.Holder>{

    private final Context appContext;
    private Xlistener listener;
    private final List<Taches> tachesList;

    /**
     *
     * @param appContext
     * @param tachesList
     */
    public Tadapter(Context appContext, List<Taches> tachesList) {
        this.appContext = appContext;
        this.tachesList = tachesList;
    }

    @NonNull
    @Override
    public Tadapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_taches, parent, false);
        return  new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Tadapter.Holder holder, int position) {
        String cover = tachesList.get(position).getTcover();
        String titre = tachesList.get(position).getTtitre();
        String description = tachesList.get(position).getTdescription();
        String nComment = tachesList.get(position).getTncomment();
        String timestamp = tachesList.get(position).getTdate();
        loadingImageWithPath(holder.ivTachesLogo, holder.pbLoading, R.drawable.wild, cover);
        holder.tvTitre.setText(titre);
        holder.tvDescription.setText(description);
        holder.tvNcomment.setText(checkBeforeFormatData(appContext.getString(R.string.comments), nComment));
        holder.tvTdate.setText(formatLaDate(timestamp));
    }

    @Override
    public int getItemCount() {
        return tachesList.size();
    }

    /**
     *
     * @param listener
     */
    public void setListener(Xlistener listener) {
        this.listener = listener;
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView ivTachesLogo;
        ImageView ivSellectedItem;
        ProgressBar pbLoading;
        TextView tvJaime;
        TextView tvComment;
        TextView tvShare;
        TextView tvTitre;
        TextView tvDescription;
        TextView tvNcomment;
        TextView tvTdate;

        public Holder(@NonNull View itemView) {
            super(itemView);
            ivTachesLogo = itemView.findViewById(R.id.ivTachesLogo);
            ivSellectedItem = itemView.findViewById(R.id.ivSellectedItem);
            pbLoading = itemView.findViewById(R.id.pbLoading);
            tvJaime = itemView.findViewById(R.id.tvJaime);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvShare = itemView.findViewById(R.id.tvShare);
            tvTitre = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvNcomment = itemView.findViewById(R.id.tvNcomment);
            tvTdate = itemView.findViewById(R.id.tvTdate);
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
