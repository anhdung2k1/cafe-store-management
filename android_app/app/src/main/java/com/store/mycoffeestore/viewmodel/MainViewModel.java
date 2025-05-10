package com.store.mycoffeestore.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.store.mycoffeestore.api.ApiClient;
import com.store.mycoffeestore.api.ApiService;
import com.store.mycoffeestore.model.CategoryModel;
import com.store.mycoffeestore.model.ItemsModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** @noinspection ALL*/
public class MainViewModel extends ViewModel {

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

    public void loadCategory(Context context) {
        ApiService api = ApiClient.getSecuredApiService(context);
        api.getProductCategories().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CategoryModel> categories = new ArrayList<>();
                    int id = 1;
                    for (String name : response.body()) {
                        categories.add(new CategoryModel(name, id++));
                    }
                    _category.setValue(categories);
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e("MainViewModel", "loadCategory error: " + t.getMessage());
            }
        });
    }

    public void loadPopular(Context context) {
        ApiService api = ApiClient.getSecuredApiService(context);
        api.getProductsByType("Popular").enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ItemsModel> items = convertToItemsModel(response.body());
                    _popular.setValue(items);
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                Log.e("MainViewModel", "loadPopular error: " + t.getMessage());
            }
        });
    }

    public void loadOffer(Context context) {
        ApiService api = ApiClient.getSecuredApiService(context);
        api.getProductsByType("Offer").enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ItemsModel> items = convertToItemsModel(response.body());
                    _offer.setValue(items);
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                Log.e("MainViewModel", "loadOffer error: " + t.getMessage());
            }
        });
    }

    // Convert JSON maps from API into ItemsModel objects
    private List<ItemsModel> convertToItemsModel(List<Map<String, Object>> data) {
        List<ItemsModel> items = new ArrayList<>();
        for (Map<String, Object> map : data) {
            ItemsModel item = new ItemsModel();
            item.setTitle((String) map.get("productName"));
            item.setDescription((String) map.get("productDescription"));
            item.setPrice((Double) map.get("productPrice"));
            item.setRating(((Number) map.getOrDefault("rating", 0)).floatValue());
            item.setExtra((String) map.getOrDefault("productModel", ""));
            item.setNumberInCart(0);

            List<String> picList = new ArrayList<>();
            if (map.containsKey("imageUrl")) {
                picList.add((String) map.get("imageUrl"));
            }
            item.setPicUrl(new ArrayList<>(picList));
            items.add(item);
        }
        return items;
    }
}