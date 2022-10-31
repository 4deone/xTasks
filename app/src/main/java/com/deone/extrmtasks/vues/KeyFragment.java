package com.deone.extrmtasks.vues;

import static com.deone.extrmtasks.tools.Constants.UID;
import static com.deone.extrmtasks.tools.Fbtools.ecrireUneNouvelleKey;
import static com.deone.extrmtasks.tools.Fbtools.lireUnUtilisateurSpecifique;
import static com.deone.extrmtasks.tools.Fbtools.lireUnUtilisateurkeys;
import static com.deone.extrmtasks.tools.Other.buildProgressDialog;
import static com.deone.extrmtasks.tools.Other.isStringEmpty;
import static com.deone.extrmtasks.tools.Other.rvLayoutManager;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.deone.extrmtasks.HomeActivity;
import com.deone.extrmtasks.R;
import com.deone.extrmtasks.adapters.Kadapter;
import com.deone.extrmtasks.adapters.Tadapter;
import com.deone.extrmtasks.modeles.Tache;
import com.deone.extrmtasks.modeles.User;
import com.deone.extrmtasks.tools.Fbtools;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import kotlin.UInt;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link KeyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class KeyFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match

    // TODO: Rename and change types of parameters
    private Fbtools fbtools;
    private String idIntent;
    private RecyclerView rvKeyItem;
    private List<String> stringList;
    private EditText etvrlKeyItem;
    private long nKeys = 0;
    private final ValueEventListener vKeys = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            DisplayUserKeys(snapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    public KeyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param idIntent Parameter 1.
     * @return A new instance of fragment KeyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static KeyFragment newInstance(String idIntent) {
        KeyFragment fragment = new KeyFragment();
        Bundle args = new Bundle();
        args.putString(UID, idIntent);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idIntent = getArguments().getString(UID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_key, container, false);
        stringList = new ArrayList<>();
        rvKeyItem = view.findViewById(R.id.rvKeyItem);
        rvKeyItem.setLayoutManager(rvLayoutManager(getActivity(), 0, LinearLayoutManager.VERTICAL));
        etvrlKeyItem = view.findViewById(R.id.etvrlKeyItem);
        lireUnUtilisateurkeys(vKeys, idIntent);
        view.findViewById(R.id.ibrlKeyItem).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.ibrlKeyItem){
            verifSaisie();
        }
    }

    private void DisplayUserKeys(DataSnapshot snapshot) {
        nKeys = snapshot.getChildrenCount();
        stringList.clear();
        for (DataSnapshot ds : snapshot.getChildren()){
            stringList.add(ds.getValue(String.class));
            Kadapter kadapter = new Kadapter(getActivity(), stringList);
            rvKeyItem.setAdapter(kadapter);
        }
    }

    private void verifSaisie() {
        String key = etvrlKeyItem.getText().toString().trim();
        ProgressDialog pd = buildProgressDialog(getActivity(), getString(R.string.app_name), getString(R.string.verif_entries));
        pd.show();
        if (isStringEmpty(key)){
            pd.dismiss();
            Toast.makeText(getActivity(), getString(R.string.titre_error), Toast.LENGTH_SHORT).show();
            return;
        }
        ecrireUneNouvelleKey(pd, ""+key, ""+idIntent, ""+nKeys);
    }
}