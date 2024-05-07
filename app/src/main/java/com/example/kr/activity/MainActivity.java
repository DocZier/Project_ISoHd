package com.example.kr.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.kr.R;
import com.example.kr.adapter.AdapterViewPager;
import com.example.kr.fragment.DatabaseFragment;
import com.example.kr.fragment.FavoriteFragment;
import com.example.kr.fragment.InfoFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ViewPager2 pagerMain;
    ArrayList<Fragment> fragments = new ArrayList<>();
    BottomNavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pagerMain = findViewById(R.id.pagerMain);
        navigationView = findViewById(R.id.bottomNav);

        fragments.add(new DatabaseFragment());
        fragments.add(new FavoriteFragment());
        fragments.add(new InfoFragment());

        AdapterViewPager adapterViewPager = new AdapterViewPager(this, fragments);
        //Set adapter
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
}