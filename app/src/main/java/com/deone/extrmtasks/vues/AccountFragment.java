package com.deone.extrmtasks.vues;

import static com.deone.extrmtasks.tools.Constants.UID;
import static com.deone.extrmtasks.database.Fbtools.ecrireUnSignalementDuser;
import static com.deone.extrmtasks.database.Fbtools.lireUnUtilisateurSpecifique;
import static com.deone.extrmtasks.database.Fbtools.liretoutesmestaches;
import static com.deone.extrmtasks.picture.Ivtools.loadingImageWithPath;
import static com.deone.extrmtasks.tools.Other.formatLaDate;
import static com.deone.extrmtasks.tools.Other.isStringEmpty;
import static com.deone.extrmtasks.tools.Other.rvLayoutManager;
import static com.deone.extrmtasks.tools.Other.safeShowValue;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deone.extrmtasks.R;
import com.deone.extrmtasks.adapters.Sadapter;
import com.deone.extrmtasks.modeles.Signale;
import com.deone.extrmtasks.modeles.Tache;
import com.deone.extrmtasks.modeles.User;
import com.deone.extrmtasks.database.Fbtools;
import com.deone.extrmtasks.tools.Xlistener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment  implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    private Fbtools fbtools;
    private String idIntent;
    private String myuid;
    private ImageView ivafCover;
    private ImageView ivafAvatar;
    private TextView tvafFullname;
    private TextView tvafTelephone;
    private TextView tvafTasks;
    private TextView tvafFavoris;
    private TextView tvafLikes;
    private TextView tvafComments;
    private TextView tvafGroups;
    private TextView tvafSave;
    private TextView tvafDescription;
    private TextView tvafDateCreate;
    private TextView tvafListSignalNumber;
    private TextView tvafListTachesNumber;
    private RecyclerView rvafListTaches;
    private List<Tache> tacheList;
    private User user;
    private final ValueEventListener vUser = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for (DataSnapshot ds : snapshot.getChildren())
                displayUserInfos(ds);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };
    private final ValueEventListener vTasks = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            tacheList.clear();
            for (DataSnapshot ds : snapshot.getChildren())
                displayTasksInfos(ds);
            tvafListTachesNumber.setText(safeShowValue(""+tacheList.size()));
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };
    private final Xlistener xListener = new Xlistener() {
        @Override
        public void onItemClick(View view, int position) {

        }

        @Override
        public void onLongItemClick(View view, int position) {

        }
    };

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param idIntent Parameter 1.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String idIntent) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(UID, idIntent);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        requireActivity().setTitle(getString(R.string.user_account));
        fbtools = Fbtools.getInstance(getActivity());
        myuid = fbtools.getId();
        if (getArguments() != null) {
            idIntent = getArguments().getString(UID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        ivafCover = view.findViewById(R.id.ivafCover);
        ivafAvatar = view.findViewById(R.id.ivafAvatar);
        tvafFullname = view.findViewById(R.id.tvafFullname);
        tvafTelephone = view.findViewById(R.id.tvafTelephone);
        tvafTasks = view.findViewById(R.id.tvafTasks);
        tvafFavoris = view.findViewById(R.id.tvafFavoris);
        tvafLikes = view.findViewById(R.id.tvafLikes);
        tvafComments = view.findViewById(R.id.tvafComments);
        tvafGroups = view.findViewById(R.id.tvafGroups);
        tvafSave = view.findViewById(R.id.tvafSave);
        tvafDescription = view.findViewById(R.id.tvafDescription);
        tvafDateCreate = view.findViewById(R.id.tvafDateCreate);

        SwitchCompat swafNotification = view.findViewById(R.id.swafNotification);

        tacheList = new ArrayList<>();
        rvafListTaches = view.findViewById(R.id.rvafListTaches);
        rvafListTaches.setLayoutManager(rvLayoutManager(getActivity(), 0, LinearLayoutManager.HORIZONTAL));

        RelativeLayout rlafListSignalement = view.findViewById(R.id.rlafListSignalement);
        rlafListSignalement.setVisibility(idIntent.equals(myuid)?View.GONE:View.VISIBLE);

        tvafListSignalNumber = view.findViewById(R.id.tvafListSignalNumber);

        tvafListTachesNumber = view.findViewById(R.id.tvafListTachesNumber);

        TextView tvafBloquer = view.findViewById(R.id.tvafBloquer);
        tvafBloquer.setVisibility(idIntent.equals(myuid)?View.GONE:View.VISIBLE);

        TextView tvafDelete = view.findViewById(R.id.tvafDelete);
        tvafDelete.setVisibility(idIntent.equals(myuid)?View.VISIBLE:View.GONE);

        TextView tvafUserInfo = view.findViewById(R.id.tvafUserInfo);
        tvafUserInfo.setVisibility(idIntent.equals(myuid)?View.VISIBLE:View.GONE);

        lireUnUtilisateurSpecifique(vUser, idIntent);
        liretoutesmestaches(vTasks, idIntent);

        view.findViewById(R.id.tvafSendMessage).setOnClickListener(this);
        view.findViewById(R.id.tvafCall).setOnClickListener(this);
        view.findViewById(R.id.tvafNotification).setOnClickListener(this);

        tvafBloquer.setOnClickListener(this);
        tvafUserInfo.setOnClickListener(this);
        tvafDelete.setOnClickListener(this);
        tvafSave.setOnClickListener(this);
        tvafListSignalNumber.setOnClickListener(this);
        tvafTasks.setOnClickListener(this);
        tvafFavoris.setOnClickListener(this);
        tvafLikes.setOnClickListener(this);
        tvafComments.setOnClickListener(this);
        tvafGroups.setOnClickListener(this);
        tvafListTachesNumber.setOnClickListener(this);
        swafNotification.setOnCheckedChangeListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tvafSave)
            saveNewContact();
        else if (id == R.id.tvafListTachesNumber || id == R.id.tvafTasks)
            displayTasksList();
        else if (id == R.id.tvafGroups)
            displayGroupsList();
        else if (id == R.id.tvafComments)
            displayCommentsList();
        else if (id == R.id.tvafLikes)
            displayLikesList();
        else if (id == R.id.tvafFavoris)
            displayFavorisList();
        else if (id == R.id.tvafListSignalNumber)
            displaySignalsList();
        else if (id == R.id.tvafNotification)
            displayNotificationsList();
        else if (id == R.id.tvafSendMessage)
            displaySendMessageUser();
        else if (id == R.id.tvafCall)
            displayCallUser();
        else if (id == R.id.tvafBloquer)
            displayBloquerUser();
        else if (id == R.id.tvafUserInfo)
            displayUserInfo();
        else if (id == R.id.tvafDelete)
            displayDeleteAccount();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        int id = compoundButton.getId();
        if (id == R.id.swafNotification)
            if (b){

            } else {

            }
    }

    private void displayTasksInfos(DataSnapshot ds) {
        Tache tache = ds.getValue(Tache.class);
        tacheList.add(tache);
        Sadapter sadapter = new Sadapter(getActivity(), tacheList);
        rvafListTaches.setAdapter(sadapter);
        sadapter.setListener(xListener);
    }

    private void displayUserInfos(DataSnapshot ds) {
        user = ds.getValue(User.class);
        assert user != null;
        tvafFullname.setText(user.getUnoms());
        tvafDateCreate.setText(formatLaDate(user.getUdate()));
        tvafTelephone.setText(safeShowValue(user.getUtelephone()));
        tvafDescription.setText(isStringEmpty(user.getUdescription()) ?
                requireActivity().getString(R.string.no_description) : user.getUdescription());
        tvafTasks.setText(safeShowValue(user.getUntask()));
        tvafFavoris.setText(safeShowValue(user.getUnfavoris()));
        tvafLikes.setText(safeShowValue(user.getUnlikes()));
        tvafComments.setText(safeShowValue(user.getUncomments()));
        tvafGroups.setText(safeShowValue(user.getUngroups()));
        tvafListSignalNumber.setText(safeShowValue(displaySignalNumber()));
        tvafSave.setVisibility(isMyContact()?View.GONE:View.VISIBLE);
        loadingImageWithPath(ivafAvatar, R.drawable.russia, user.getUavatar());
        loadingImageWithPath(ivafCover, R.drawable.wild, user.getUcover());
    }

    private boolean isMyContact() {
        return false;
    }

    private String displaySignalNumber() {
        return "";
    }

    private void saveNewContact() {
    }

    private void displayTasksList() {
    }

    private void displayGroupsList() {
    }

    private void displayCommentsList() {
    }

    private void displayLikesList() {
    }

    private void displayFavorisList() {
    }

    private void displaySignalsList() {
        final Dialog dialogSignaler = new Dialog(getActivity());
        dialogSignaler.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSignaler.setContentView(R.layout.dialod_signal);

        TextView tvSignalTitle = dialogSignaler.findViewById(R.id.tvSignalTitle);
        tvSignalTitle.setText(getString(R.string.signaler_l_utilisateur, user.getUnoms()));
        EditText edtSignalRaison =  dialogSignaler.findViewById(R.id.edtSignalRaison);

        Button btSendSignal = dialogSignaler.findViewById(R.id.btSendSignal);
        btSendSignal.setOnClickListener(v -> {
            String value = edtSignalRaison.getText().toString().trim();
            if (isStringEmpty(value)){
                Toast.makeText(getActivity(), getString(R.string.reason_error), Toast.LENGTH_SHORT).show();
                return;
            }
            Signale signale = new Signale(""+myuid, value, "", user.getUid(), user.getUnoms(), user.getUavatar());
            ecrireUnSignalementDuser(signale);
            dialogSignaler.dismiss();
        });
        dialogSignaler.show();
    }

    private void displayNotificationsList() {
    }

    private void displaySendMessageUser() {
    }

    private void displayCallUser() {
    }

    private void displayBloquerUser() {
    }

    private void displayUserInfo() {
    }

    private void displayDeleteAccount() {
    }

}