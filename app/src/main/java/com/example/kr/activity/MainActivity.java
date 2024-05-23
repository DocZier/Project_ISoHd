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
import com.example.kr.fragment.LoginFragment;
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pagerMain = findViewById(R.id.pagerMain);
        navigationView = findViewById(R.id.bottomNav);
        viewSwitcher = findViewById(R.id.view_switcher);

        fragments.add(new DatabaseFragment());
        fragments.add(new FavoriteFragment());
        fragments.add(new InfoFragment());

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
                        navigationView.setSelectedItemId(R.id.itFavorite);
                        break;
                    case 2:
                        navigationView.setSelectedItemId(R.id.itInfo);
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
                } else if (itemId == R.id.itFavorite) {
                    pagerMain.setCurrentItem(1);
                } else if (itemId == R.id.itInfo) {
                    pagerMain.setCurrentItem(2);
                }
                return true;
            }
        });
    }

    public void showLoginFragment() {
        viewSwitcher.showNext();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragments_container, new LoginFragment(), "login");
        fragmentTransaction.commit();

    }

    public void hideLoginFragment() {
        viewSwitcher.showNext();

        Fragment loginFragment = getSupportFragmentManager().findFragmentByTag("login");
        if (loginFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(loginFragment).commit();
        }

        ((DatabaseFragment) fragments.get(0)).updateFragment();
        ((FavoriteFragment) fragments.get(1)).updateFragment();
    }

    public void showSignupFragment() {
        viewSwitcher.showNext();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragments_container, new SignUpFragment(), "signup");
        fragmentTransaction.commit();

    }

    public void hideSignupFragment() {
        viewSwitcher.showNext();

        Fragment loginFragment = getSupportFragmentManager().findFragmentByTag("signup");
        if (loginFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(loginFragment).commit();
        }

        ((DatabaseFragment) fragments.get(0)).updateFragment();
        ((FavoriteFragment) fragments.get(1)).updateFragment();
    }

    public void showAccountFragment() {
        viewSwitcher.showNext();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragments_container, new AccountFragment(), "account");
        fragmentTransaction.commit();


    }

    public void hideAccountFragment()
    {
        viewSwitcher.showNext();

        Fragment accountFragment = getSupportFragmentManager().findFragmentByTag("account");
        if (accountFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(accountFragment).commit();
        }

        ((DatabaseFragment) fragments.get(0)).updateFragment();
        ((FavoriteFragment) fragments.get(1)).updateFragment();
    }

}