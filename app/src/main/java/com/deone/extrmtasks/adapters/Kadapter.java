package com.deone.extrmtasks.adapters;

import static com.deone.extrmtasks.tools.Constants.KEYS;
import static com.deone.extrmtasks.tools.Constants.USERS;
import static com.deone.extrmtasks.database.Fbtools.deleteKey;
import static com.deone.extrmtasks.tools.Other.buildPathWithSlash;
import static com.deone.extrmtasks.preference.Sptools.readStringData;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.deone.extrmtasks.R;
import com.deone.extrmtasks.modeles.Key;

import java.util.List;

/**
 *
 */
public class Kadapter extends RecyclerView.Adapter<Kadapter.Holder> {
    private final Context appContext;
    private final List<Key> keyList;
    private final String myuid;
    private final String nKeys;

    /**
     *
     * @param keyList
     * @param myuid
     * @param nKeys
     */
    public Kadapter(Context appContext, List<Key> keyList, String myuid, String nKeys) {
        this.appContext = appContext;
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
            AlertDialog.Builder builder = new AlertDialog.Builder(appContext);
            builder.setTitle(appContext.getString(R.string.app_name_lite));
            builder.setMessage(appContext.getString(R.string.delete_key_word, key));
            builder.setNegativeButton(appContext.getString(R.string.non), null);
            builder.setPositiveButton(appContext.getString(R.string.oui), (dialogInterface, i) ->{
                deleteKey(buildPathWithSlash(USERS, myuid, KEYS, keyId), myuid, nKeys);
                keyList.remove(position);
                notifyItemRemoved(position);
                //holder.ibKeyItem.setEnabled(false);
            });
            builder.create().show();
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
