package com.deone.extrmtasks;

import static com.deone.extrmtasks.tools.Constants.APP_PREFS_LANGUE;
import static com.deone.extrmtasks.tools.Constants.APP_PREFS_MODE;
import static com.deone.extrmtasks.tools.Constants.EN;
import static com.deone.extrmtasks.tools.Constants.FRAGMENT_ACCOUNT;
import static com.deone.extrmtasks.tools.Constants.FRAGMENT_AUTH;
import static com.deone.extrmtasks.tools.Constants.FRAGMENT_CONF;
import static com.deone.extrmtasks.tools.Constants.FRAGMENT_GROUP;
import static com.deone.extrmtasks.tools.Constants.FRAGMENT_KEY;
import static com.deone.extrmtasks.tools.Constants.FRAGMENT_NOT;
import static com.deone.extrmtasks.tools.Constants.FRAGMENT_STOCKAGE;
import static com.deone.extrmtasks.tools.Constants.IDFRAGMENT;
import static com.deone.extrmtasks.tools.Constants.UID;
import static com.deone.extrmtasks.tools.Other.gotomain;
import static com.deone.extrmtasks.tools.Other.initLLanguage;
import static com.deone.extrmtasks.tools.Other.initThemeMode;
import static com.deone.extrmtasks.tools.Other.isStringEmpty;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.deone.extrmtasks.tools.Fbtools;
import com.deone.extrmtasks.tools.Sptools;
import com.deone.extrmtasks.vues.AccountFragment;
import com.deone.extrmtasks.vues.AutorisationFragment;
import com.deone.extrmtasks.vues.ConfidentialityFragment;
import com.deone.extrmtasks.vues.GroupFragment;
import com.deone.extrmtasks.vues.KeyFragment;
import com.deone.extrmtasks.vues.NotificationFragment;
import com.deone.extrmtasks.vues.StockageFragment;

public class TempActivity extends AppCompatActivity {

    private Fbtools fbtools;
    private String idFragmentIntent;
    private String idIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initApp();
        super.onCreate(savedInstanceState);
        checkUser();
    }

    private void initApp() {
        Sptools sptools = Sptools.getInstance(this);
        fbtools = Fbtools.getInstance(this);
        initThemeMode(sptools.readIntData(APP_PREFS_MODE, AppCompatDelegate.MODE_NIGHT_NO));
        initLLanguage(this, sptools.readStringData(APP_PREFS_LANGUE, EN));
        idFragmentIntent = getIntent().getStringExtra(IDFRAGMENT);
        idIntent = getIntent().getStringExtra(UID);
    }

    private void checkUser() {
        if(isStringEmpty(fbtools.getId())){
            gotomain(this);
        }else {
            initViews();
        }
    }

    private void initViews() {
        setContentView(R.layout.activity_temp);
        Toolbar toolbarTemp = findViewById(R.id.toolbarTemp);
        setSupportActionBar(toolbarTemp);

        Bundle bundle = new Bundle();
        bundle.putString(UID, idIntent);

        switch (idFragmentIntent){
            case FRAGMENT_ACCOUNT: loadFragment(AccountFragment.newInstance(idIntent));
                break;
            case FRAGMENT_AUTH: loadFragment(AutorisationFragment.newInstance(idIntent));
                break;
            case FRAGMENT_CONF: loadFragment(ConfidentialityFragment.newInstance(idIntent));
                break;
            case FRAGMENT_GROUP: loadFragment(GroupFragment.newInstance(idIntent));
                break;
            case FRAGMENT_KEY: loadFragment(KeyFragment.newInstance(idIntent));
                break;
            case FRAGMENT_NOT: loadFragment(NotificationFragment.newInstance(idIntent));
                break;
            case FRAGMENT_STOCKAGE: loadFragment(StockageFragment.newInstance(idIntent));
                break;
        }
    }

    /**
     *
     * @param fragment
     */
    private void loadFragment(Fragment fragment) {
        // create a FragmentManager
        FragmentManager fm = getSupportFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.frameLayoutTemp, fragment);
        fragmentTransaction.commit(); // save the changes
    }

}