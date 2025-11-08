package com.example.englishlearningapp.view.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.view.adapter.ViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        init();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0); // KHÔNG ÁP DỤNG PADDING DƯỚI
            return insets;
        });

        // Bắt sự kiện vuốt ViewPager -> đổi tab trên BottomNavigation
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mBottomNavigationView.setSelectedItemId(R.id.navigation_home);
                        break;
                    case 1:
                        mBottomNavigationView.setSelectedItemId(R.id.navigation_word);
                        break;
                    case 2:
                        mBottomNavigationView.setSelectedItemId(R.id.navigation_account);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        // 🔹 Bắt sự kiện nhấn vào item trên BottomNavigation -> đổi trang trong ViewPager
        mBottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(0, true);
                    return true;
                case R.id.navigation_word:
                    viewPager.setCurrentItem(1, true);
                    return true;
                case R.id.navigation_account:
                    viewPager.setCurrentItem(2, true);
                    return true;
            }
            return false;
        });
    }

    private void init() {
        viewPager = findViewById(R.id.viewPager);
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        ViewPagerAdapter viewPagerAdapter =
                new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(0);
    }
}
