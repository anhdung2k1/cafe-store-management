package com.store.mycoffeestore.helper;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.store.mycoffeestore.R;
import com.store.mycoffeestore.activity.CartActivity;
import com.store.mycoffeestore.activity.MainActivity;
import com.store.mycoffeestore.activity.OrderActivity;
import com.store.mycoffeestore.activity.ProfileActivity;


public class NavigationHelper {

    public static void setupBottomNavigation(final Activity activity, BottomNavigationView bottomNavigationView, int selectedItemId) {
        if (activity == null || bottomNavigationView == null) {
            Log.w("NavigationHelper", "Did not find activity" + activity + "bottomNavigationView" + bottomNavigationView);
            return;
        }
        bottomNavigationView.setSelectedItemId(selectedItemId);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == selectedItemId) return true;

            Log.w("NavigationHelper", "itemId: " + itemId);
            if (itemId == R.id.home_btn) {
                Log.w("NavigationHelper", "Start activity Home");
                activity.startActivity(new Intent(activity, MainActivity.class));
            } else if (itemId == R.id.profile_btn) {
                Log.w("NavigationHelper", "Start activity Profile");
                activity.startActivity(new Intent(activity, ProfileActivity.class));
            } else if (itemId == R.id.order_btn) {
                activity.startActivity(new Intent(activity, OrderActivity.class));
//            } else if (itemId == R.id.setting_btn) {
//                activity.startActivity(new Intent(activity, SettingActivity.class));
            }
            activity.overridePendingTransition(0, 0);
            return true;
        });

        FloatingActionButton cartFab = activity.findViewById(R.id.cart_btn);
        if (cartFab != null) {
            cartFab.setOnClickListener(v -> {
                Log.w("NavigationHelper", "FAB Cart clicked");
                activity.startActivity(new Intent(activity, CartActivity.class));
            });
        } else {
            Log.w("NavigationHelper", "FAB cart_btn not found");
        }
    }
}
