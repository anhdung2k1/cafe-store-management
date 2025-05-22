package com.store.mycoffeestore.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.store.mycoffeestore.R;
import com.store.mycoffeestore.adapter.CategoryAdapter;
import com.store.mycoffeestore.adapter.OffersAdapter;
import com.store.mycoffeestore.adapter.PopularAdapter;
import com.store.mycoffeestore.api.ApiClient;
import com.store.mycoffeestore.api.ApiService;
import com.store.mycoffeestore.helper.NavigationHelper;
import com.store.mycoffeestore.model.ItemsModel;
import com.store.mycoffeestore.model.Product;
import com.store.mycoffeestore.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private final MainViewModel viewModel = new MainViewModel();

    private RecyclerView recyclerViewCategory, recyclerViewPopular, recyclerViewOffer;
    private ProgressBar progressBarCategory, progressBarPopular, progressBarOffer;
    private EditText searchInput;

    private final List<ItemsModel> searchResult = new ArrayList<>();
    private PopularAdapter searchAdapter;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initCategory();
        initPopular();
        initOffer();
        initSearch();

        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        NavigationHelper.setupBottomNavigation(this, nav, R.id.home_btn);
    }

    private void initViews() {
        recyclerViewCategory = findViewById(R.id.recyclerViewCategory);
        recyclerViewPopular = findViewById(R.id.recyclerViewPopular);
        recyclerViewOffer = findViewById(R.id.recyclerViewOffer);

        progressBarCategory = findViewById(R.id.progressBarCategory);
        progressBarPopular = findViewById(R.id.progressBarPopular);
        progressBarOffer = findViewById(R.id.progressBarOffer);

        searchInput = findViewById(R.id.editTextText);

        recyclerViewPopular.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void initCategory() {
        progressBarCategory.setVisibility(View.VISIBLE);
        viewModel.getCategory().observe(this, items -> {
            recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            recyclerViewCategory.setAdapter(new CategoryAdapter(items));
            progressBarCategory.setVisibility(View.GONE);
        });
        viewModel.loadCategory(getApplicationContext());
    }

    private void initPopular() {
        progressBarPopular.setVisibility(View.VISIBLE);
        viewModel.getPopular().observe(this, items -> {
            // chỉ gán nếu không đang trong trạng thái tìm kiếm
            if (TextUtils.isEmpty(searchInput.getText())) {
                recyclerViewPopular.setAdapter(new PopularAdapter(items));
            }
            progressBarPopular.setVisibility(View.GONE);
        });
        viewModel.loadPopular(getApplicationContext());
    }

    private void initOffer() {
        progressBarOffer.setVisibility(View.VISIBLE);
        viewModel.getOffer().observe(this, items -> {
            recyclerViewOffer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            recyclerViewOffer.setAdapter(new OffersAdapter(items));
            progressBarOffer.setVisibility(View.GONE);
        });
        viewModel.loadOffer(getApplicationContext());
    }

    private void initSearch() {
        searchAdapter = new PopularAdapter(searchResult);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) handler.removeCallbacks(searchRunnable);
                searchRunnable = () -> {
                    String query = s.toString().trim();
                    if (!TextUtils.isEmpty(query)) {
                        performSearch(query);
                    } else {
                        // nếu trống thì load lại popular mặc định
                        initPopular();
                    }
                };
                handler.postDelayed(searchRunnable, 500);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void performSearch(String query) {
        ApiService api = ApiClient.getSecuredApiService(this);
        api.getProductsByName(query).enqueue(new Callback<List<Map<String, Object>>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<List<Map<String, Object>>> call, @NonNull Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    searchResult.clear();
                    for (Map<String, Object> map : response.body()) {
                        Product product = Product.fromMap(map);
                        ItemsModel item = ItemsModel.fromProduct(product);
                        searchResult.add(item);
                    }
                    recyclerViewPopular.setAdapter(searchAdapter);
                    searchAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "Search failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Map<String, Object>>> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}