package com.deone.extrmtasks.adapters;

import static com.deone.extrmtasks.tools.Other.formatLaDate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deone.extrmtasks.R;
import com.deone.extrmtasks.modeles.Comment;
import com.deone.extrmtasks.tools.Xlistener;

import java.util.List;

/**
 *
 */
public class Cadapter extends RecyclerView.Adapter<Cadapter.Holder>{

    private final Context appContext;
    private Xlistener listener;
    private final List<Comment> commentList;

    /**
     *
     * @param appContext
     * @param commentList
     */
    public Cadapter(Context appContext, List<Comment> commentList) {
        this.appContext = appContext;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public Cadapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return  new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Cadapter.Holder holder, int position) {
        String uAvatar = commentList.get(position).getUavatar();
        String uNoms = commentList.get(position).getUnoms();
        String message = commentList.get(position).getCmessage();
        String timestamp = commentList.get(position).getCdate();

        //holder.ivUser.
        holder.tvUsername.setText(uNoms);
        holder.tvComment.setText(message);
        holder.tvDatecomment.setText(formatLaDate(timestamp));
    }

    @Override
    public int getItemCount() {
        return commentList.size();
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

        ImageView ivUser;
        TextView tvUsername;
        TextView tvComment;
        TextView tvDatecomment;

        public Holder(@NonNull View itemView) {
            super(itemView);
            ivUser = itemView.findViewById(R.id.ivAvatarUser);
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
