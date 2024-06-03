package com.example.kr.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.kr.R;
import com.example.kr.fragment.AccountFragment;
import com.example.kr.fragment.HistoryFragment;
import com.example.kr.fragment.LoginFragment;
import com.example.kr.fragment.MainMenuFragment;
import com.example.kr.fragment.MapFragment;
import com.example.kr.fragment.SettingsFragment;
import com.example.kr.fragment.SignUpFragment;
import com.example.kr.model.AdapterViewPager;
import com.example.kr.fragment.DatabaseFragment;
import com.example.kr.fragment.FavoriteFragment;
import com.example.kr.fragment.InfoFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ViewPager2 pagerMain;
    ArrayList<Fragment> fragments = new ArrayList<>();
    BottomNavigationView navigationView;
    ViewSwitcher viewSwitcher;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pagerMain = findViewById(R.id.pagerMain);
        navigationView = findViewById(R.id.bottomNav);
        viewSwitcher = findViewById(R.id.view_switcher);

        fragments.add(new MainMenuFragment());
        fragments.add(new AccountFragment());
        fragments.add(new SettingsFragment());

        AdapterViewPager adapterViewPager = new AdapterViewPager(this, fragments);

        pagerMain.setAdapter(adapterViewPager);
        pagerMain.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position)
            {
                switch (position)
                {
                    case 0:
                        navigationView.setSelectedItemId(R.id.itHome);
                        break;
                    case 1:
                        navigationView.setSelectedItemId(R.id.itAccount);
                        break;
                    case 2:
                        navigationView.setSelectedItemId(R.id.itSettings);
                        break;
                }
                super.onPageSelected(position);
            }
        });
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.itHome) {
                    pagerMain.setCurrentItem(0);
                } else if (itemId == R.id.itAccount) {
                    pagerMain.setCurrentItem(1);
                } else if (itemId == R.id.itSettings) {
                    pagerMain.setCurrentItem(2);
                }
                return true;
            }
        });
    }

    public void setCurrentPage(int i)
    {
        pagerMain.setCurrentItem(i);

        switch (i)
        {
            case 0:
                navigationView.setSelectedItemId(R.id.itHome);
                break;
            case 1:
                navigationView.setSelectedItemId(R.id.itAccount);
                break;
            case 2:
                navigationView.setSelectedItemId(R.id.itSettings);
                break;
        }

    }

    public void showFragment(String type) {
        Fragment fragment = null;
        switch (type) {
            case "database":
                fragment = new DatabaseFragment();
                break;
            case "history":
                fragment = new HistoryFragment();
                break;
            case "favorite":
                fragment = new FavoriteFragment();
                break;
            case "info":
                fragment = new InfoFragment();
                break;
            case "map":
                fragment = new MapFragment();
                break;
            case "login":
                fragment = new LoginFragment();
                ((AccountFragment) fragments.get(1)).updateFragment();
                break;
            case "signup":
                fragment = new SignUpFragment();
                ((AccountFragment) fragments.get(1)).updateFragment();
                break;
            default:
                return;
        }

        fragmentManager = getSupportFragmentManager();

        fragmentManager
                .beginTransaction()
                .replace(R.id.fragments_container, fragment, type)
                .addToBackStack(type)
                .commit();

        viewSwitcher.showNext();
    }

    public void hideFragment() {
        if (fragmentManager.getBackStackEntryCount() > 0)
        {
            fragmentManager.popBackStack();
            viewSwitcher.showPrevious();
        }
    }


}