package com.store.mycoffeestore.helper;

import android.app.Activity;
import android.content.Intent;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.store.mycoffeestore.R;
import com.store.mycoffeestore.activity.CartActivity;
import com.store.mycoffeestore.activity.MainActivity;
import com.store.mycoffeestore.activity.ProfileActivity;


public class NavigationHelper {

    public static void setupBottomNavigation(final Activity activity, BottomNavigationView bottomNavigationView, int selectedItemId) {
        if (activity == null || bottomNavigationView == null) {
            return;
        }
        bottomNavigationView.setSelectedItemId(selectedItemId);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == selectedItemId) return true;

            if (itemId == R.id.home_btn) {
                activity.startActivity(new Intent(activity, MainActivity.class));
            } else if (itemId == R.id.profile_btn) {
                activity.startActivity(new Intent(activity, ProfileActivity.class));
//            } else if (itemId == R.id.payment_btn) {
//                activity.startActivity(new Intent(activity, PaymentActivity.class));
//            } else if (itemId == R.id.setting_btn) {
//                activity.startActivity(new Intent(activity, SettingActivity.class));
            } else if (itemId == R.id.cart_btn) {
                activity.startActivity(new Intent(activity, CartActivity.class));
            }
            activity.overridePendingTransition(0, 0);

            return true;
        });
    }
}
