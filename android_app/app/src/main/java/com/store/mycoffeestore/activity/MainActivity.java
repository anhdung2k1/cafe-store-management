package com.store.mycoffeestore.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.store.mycoffeestore.R;
import com.store.mycoffeestore.adapter.CategoryAdapter;
import com.store.mycoffeestore.adapter.OffersAdapter;
import com.store.mycoffeestore.adapter.PopularAdapter;
import com.store.mycoffeestore.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel = new MainViewModel();

    private RecyclerView recyclerViewCategory, recyclerViewPopular, recyclerViewOffer;
    private ProgressBar progressBarCategory, progressBarPopular, progressBarOffer;
    private FloatingActionButton cartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initCategory();
        initPopular();
        initOffer();
        setupBottomMenu();
    }

    private void initViews() {
        recyclerViewCategory = findViewById(R.id.recyclerViewCategory);
        recyclerViewPopular = findViewById(R.id.recyclerViewPopular);
        recyclerViewOffer = findViewById(R.id.recyclerViewOffer);

        progressBarCategory = findViewById(R.id.progressBarCategory);
        progressBarPopular = findViewById(R.id.progressBarPopular);
        progressBarOffer = findViewById(R.id.progressBarOffer);

        cartBtn = findViewById(R.id.cartBtn);
    }

    private void setupBottomMenu() {
        cartBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            startActivity(intent);
        });
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
            recyclerViewPopular.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            recyclerViewPopular.setAdapter(new PopularAdapter(items));
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
}