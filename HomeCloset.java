package com.example.closetifiy_finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;
import java.util.List;

public class HomeCloset extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private HomeClosetAdapter adapter; // Adapter for managing fragments

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_closet, container, false);

        // Initialize UI elements
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        // Setup Tab Layout and ViewPager2
        setupTabs();

        // Setup Menu Button
        ImageView menu = view.findViewById(R.id.Menu);
        if (menu != null) {
            menu.setOnClickListener(v -> showBottomMenu());
        }

        return view;
    }

    private void setupTabs() {
        List<Fragment> fragments = new ArrayList<>();
        FragmentItems fragmentItems = new FragmentItems();
        fragmentItems.setMode(FragmentItems.MODE_HOME_CLOSET);
        fragments.add(fragmentItems);  // Your Items Fragment
        fragments.add(new fragment_outfits()); // Your Outfits Fragment
        fragments.add(new fragment_lookbbok()); // Your Lookbooks Fragment

        adapter = new HomeClosetAdapter(this, fragments);
        viewPager.setAdapter(adapter);

        // Attach TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Items");
                    break;
                case 1:
                    tab.setText("Outfits");
                    break;
                case 2:
                    tab.setText("Lookbooks");
                    break;
            }
        }).attach();
    }

    private void showBottomMenu() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = getLayoutInflater().inflate(R.layout.menubottom, null);

        NavigationView navigationView = bottomSheetView.findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(item -> {
            bottomSheetDialog.dismiss();
            return true;
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }
}