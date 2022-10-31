package com.deone.extrmtasks.adapters;

import static com.deone.extrmtasks.tools.Constants.KEYS;
import static com.deone.extrmtasks.tools.Constants.USERS;
import static com.deone.extrmtasks.tools.Fbtools.deleteKey;
import static com.deone.extrmtasks.tools.Other.buildPathWithSlash;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deone.extrmtasks.R;
import com.deone.extrmtasks.modeles.Key;

import java.util.List;

/**
 *
 */
public class Kadapter extends RecyclerView.Adapter<Kadapter.Holder> {

    private final List<Key> keyList;
    private final String myuid;
    private final String nKeys;

    /**
     *
     * @param keyList
     * @param myuid
     * @param nKeys
     */
    public Kadapter(List<Key> keyList, String myuid, String nKeys) {
        this.keyList = keyList;
        this.myuid = myuid;
        this.nKeys = nKeys;
    }

    @NonNull
    @Override
    public Kadapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_key, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Kadapter.Holder holder, int position) {
        String keyId = keyList.get(position).getKid();
        String key = keyList.get(position).getKmessage();

        holder.tvKeyItem.setText(key);
        holder.ibKeyItem.setOnClickListener(view -> {
            deleteKey(buildPathWithSlash(USERS, myuid, KEYS, keyId), myuid, nKeys);
        });
    }

    @Override
    public int getItemCount() {
        return keyList.size();
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
