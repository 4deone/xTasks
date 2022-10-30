package com.deone.extrmtasks.vues;

import static com.deone.extrmtasks.tools.Constants.UID;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.deone.extrmtasks.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StockageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StockageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match

    // TODO: Rename and change types of parameters
    private String idIntent;

    public StockageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param idIntent Parameter 1.
     * @return A new instance of fragment StockageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StockageFragment newInstance(String idIntent) {
        StockageFragment fragment = new StockageFragment();
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
        View view = inflater.inflate(R.layout.fragment_stockage, container, false);
        return view;
    }
}