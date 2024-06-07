package com.example.kr.activity;


import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.kr.R;
import com.example.kr.dialog.AboutCalculatorCustomDialog;
import com.example.kr.dialog.AboutFavoritesCustomDialog;
import com.example.kr.dialog.AboutHddsCustomDialog;
import com.example.kr.dialog.AboutHistoryCusomDialog;
import com.example.kr.dialog.AboutMapCustomDialog;
import com.example.kr.dialog.AboutProfileCustomDialog;
import com.example.kr.dialog.AboutResourcesCustomDialog;
import com.example.kr.dialog.AuthorCustomDialog;
import com.example.kr.dialog.InstructionCustomDialog;
import com.example.kr.fragment.AccountFragment;
import com.example.kr.fragment.DatabaseFragment;
import com.example.kr.fragment.FavoriteFragment;
import com.example.kr.fragment.HistoryFragment;
import com.example.kr.fragment.InfoFragment;
import com.example.kr.fragment.LoginFragment;
import com.example.kr.fragment.MainMenuFragment;
import com.example.kr.fragment.MapFragment;
import com.example.kr.fragment.SettingsFragment;
import com.example.kr.fragment.SignUpFragment;
import com.example.kr.model.AdapterViewPager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.yandex.mapkit.MapKitFactory;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ViewPager2 pagerMain;
    ArrayList<Fragment> fragments = new ArrayList<>();
    BottomNavigationView navigationView;
    ViewSwitcher viewSwitcher;
    private FragmentManager fragmentManager;

    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey("7443a304-1572-40ae-8d63-7f0d02cb01d0");
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

        requestLocationPermission();
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

    public void showCustomDialog(String type)
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");

        if (prev != null)
        {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        DialogFragment fragment = null;
        switch (type) {
            case "a_calculator":
                fragment = new AboutCalculatorCustomDialog();
                break;
            case "a_favorites":
                fragment = new AboutFavoritesCustomDialog();
                break;
            case "a_hdds":
                fragment = new AboutHddsCustomDialog();
                break;
            case "a_history":
                fragment = new AboutHistoryCusomDialog();
                break;
            case "a_map":
                fragment = new AboutMapCustomDialog();
                break;
            case "a_profile":
                fragment = new AboutProfileCustomDialog();
                break;
            case "a_resources":
                fragment = new AboutResourcesCustomDialog();
                break;
            case "author":
                fragment = new AuthorCustomDialog();
                break;
            case "instructions":
                fragment = new InstructionCustomDialog();
                break;
            default:
                return;
        }

        fragment.show(getSupportFragmentManager(), "dialog");
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
                fragment = new LoginFragment(null, null);
                break;
            case "signup":
                fragment = new SignUpFragment();
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
            update();
        }
    }

    public void login(String email, String password)
    {
        update();
        Fragment fragment = new LoginFragment(email, password);
        fragmentManager = getSupportFragmentManager();

        fragmentManager
                .beginTransaction()
                .replace(R.id.fragments_container, fragment, "login")
                .addToBackStack("login")
                .commit();
    }

    public void update()
    {
        ((AccountFragment) fragments.get(1)).updateFragment();
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                "android.permission.ACCESS_FINE_LOCATION")
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{"android.permission.ACCESS_FINE_LOCATION"},
                    PERMISSIONS_REQUEST_FINE_LOCATION);
        }
    }


}