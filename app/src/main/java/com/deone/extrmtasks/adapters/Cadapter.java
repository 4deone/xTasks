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
public class Cadapter extends RecyclerView.Adapter<Cadapter.Holder> {

    private final Context appContext;
    private Xlistener listener;
    private final List<Commentaire> commentaireList;
    private String myuid;

    /**
     *
     * @param appContext
     * @param commentaireList
     */
    public Cadapter(Context appContext, List<Commentaire> commentaireList) {
        this.appContext = appContext;
        this.commentaireList = commentaireList;
        this.myuid = Fbtools.getInstance(appContext).getId();
    }

    @NonNull
    @Override
    public Cadapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return  new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Cadapter.Holder holder, int position) {
        String uid = commentaireList.get(position).getUid();
        String uAvatar = commentaireList.get(position).getUavatar();
        String uNoms = commentaireList.get(position).getUnoms();
        String message = commentaireList.get(position).getCmessage();
        String timestamp = commentaireList.get(position).getCdate();

        loadingImageWithPath(holder.ivAvatarUser, R.drawable.russia, uAvatar);
        holder.ivAvatarUser.setOnClickListener(view -> {
            if (!myuid.equals(uid)) {
                Intent intent = new Intent(appContext, TempActivity.class);
                intent.putExtra(UID, uid);
                intent.putExtra(IDFRAGMENT, FRAGMENT_ACCOUNT);
                appContext.startActivity(intent);
            }
        });

        holder.tvUsername.setText(myuid.equals(uid)?appContext.getString(R.string.you):uNoms);
        holder.tvUsername.setOnClickListener(view -> {
            if (!myuid.equals(uid)) {
                Intent intent = new Intent(appContext, TempActivity.class);
                intent.putExtra(UID, uid);
                intent.putExtra(IDFRAGMENT, FRAGMENT_ACCOUNT);
                appContext.startActivity(intent);
            }
        });

        holder.tvComment.setText(message);
        holder.tvDatecomment.setText(formatLaDate(timestamp));
    }

    @Override
    public int getItemCount() {
        return commentaireList.size();
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

        ImageView ivAvatarUser;
        TextView tvUsername;
        TextView tvComment;
        TextView tvDatecomment;

        public Holder(@NonNull View itemView) {
            super(itemView);
            ivAvatarUser = itemView.findViewById(R.id.ivAvatarUser);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvDatecomment = itemView.findViewById(R.id.tvDatecomment);
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
