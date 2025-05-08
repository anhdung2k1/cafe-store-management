package com.store.mycoffeestore.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;

import com.store.mycoffeestore.model.CategoryModel;
import com.store.mycoffeestore.model.ItemsModel;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {

//    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    private final MutableLiveData<List<CategoryModel>> _category = new MutableLiveData<>();
    private final MutableLiveData<List<ItemsModel>> _popular = new MutableLiveData<>();
    private final MutableLiveData<List<ItemsModel>> _offer = new MutableLiveData<>();

    public LiveData<List<CategoryModel>> getCategory() {
        return _category;
    }

    public LiveData<List<ItemsModel>> getPopular() {
        return _popular;
    }

    public LiveData<List<ItemsModel>> getOffer() {
        return _offer;
    }
/*
    public void loadCategory() {
        firebaseDatabase.getReference("Category").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<CategoryModel> lists = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    CategoryModel list = childSnapshot.getValue(CategoryModel.class);
                    if (list != null) {
                        lists.add(list);
                    }
                }
                _category.setValue(lists);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error if needed
            }
        });
    }

    public void loadPopular() {
        firebaseDatabase.getReference("Items").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<ItemsModel> lists = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    ItemsModel list = childSnapshot.getValue(ItemsModel.class);
                    if (list != null) {
                        lists.add(list);
                    }
                }
                _popular.setValue(lists);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error if needed
            }
        });
    }

    public void loadOffer() {
        firebaseDatabase.getReference("Offers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<ItemsModel> lists = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    ItemsModel list = childSnapshot.getValue(ItemsModel.class);
                    if (list != null) {
                        lists.add(list);
                    }
                }
                _offer.setValue(lists);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error if needed
            }
        });
    }
*/
    // Simulating data for Categories
    public void loadCategory() {
        List<CategoryModel> categories = new ArrayList<>();
        categories.add(new CategoryModel("Coffee", 1));
        categories.add(new CategoryModel("Tea", 2));
        categories.add(new CategoryModel("Smoothies", 3));
        categories.add(new CategoryModel("Bakery", 4));

        _category.setValue(categories);
    }

    // Simulating data for Popular Items
    public void loadPopular() {
        List<ItemsModel> popularItems = new ArrayList<>();

        // Adding sample popular items
        ArrayList<String> picUrl = new ArrayList<>();
        picUrl.add("https://example.com/coffee_image_1.jpg");
        popularItems.add(new ItemsModel("Latte", "A smooth coffee with steamed milk", picUrl, 4.99, 4.5f, 0, "Special"));

        picUrl = new ArrayList<>();
        picUrl.add("https://example.com/coffee_image_2.jpg");
        popularItems.add(new ItemsModel("Cappuccino", "Espresso with steamed milk and foam", picUrl, 3.99, 4.7f, 0, "Classic"));

        picUrl = new ArrayList<>();
        picUrl.add("https://example.com/coffee_image_3.jpg");
        popularItems.add(new ItemsModel("Espresso", "Strong black coffee", picUrl, 2.99, 4.8f, 0, "Bold"));

        _popular.setValue(popularItems);
    }

    // Simulating data for Offers
    public void loadOffer() {
        List<ItemsModel> offers = new ArrayList<>();

        // Adding sample offers
        ArrayList<String> picUrl = new ArrayList<>();
        picUrl.add("https://example.com/offer_image_1.jpg");
        offers.add(new ItemsModel("Summer Special", "Get a 10% discount on all drinks", picUrl, 4.49, 4.2f, 0, "Offer"));

        picUrl = new ArrayList<>();
        picUrl.add("https://example.com/offer_image_2.jpg");
        offers.add(new ItemsModel("Buy One Get One Free", "On selected items", picUrl, 5.99, 4.0f, 0, "BOGO"));

        _offer.setValue(offers);
    }
}
