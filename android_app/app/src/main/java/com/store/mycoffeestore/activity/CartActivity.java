package com.store.mycoffeestore.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.store.mycoffeestore.R;
import com.store.mycoffeestore.adapter.CartAdapter;
import com.store.mycoffeestore.helper.ChangeNumberItemsListener;
import com.store.mycoffeestore.helper.ManagementCart;

public class CartActivity extends AppCompatActivity {

    private ManagementCart management;
    private double tax = 0.0;

    private RecyclerView rvCartView;
    private ImageView ivBack;
    private TextView subTotalPriceTxt, totalTaxPriceTxt, deliveryPriceTxt, totalPriceTxt;
    private EditText etCoupon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        management = new ManagementCart(this);

        initViews();
        calculateCart();
        setVariable();
        initCartList();
    }

    private void initViews() {
        rvCartView = findViewById(R.id.rvCartView);
        ivBack = findViewById(R.id.ivBack);
        subTotalPriceTxt = findViewById(R.id.subTotalPriceTxt);
        totalTaxPriceTxt = findViewById(R.id.totalTaxPriceTxt);
        deliveryPriceTxt = findViewById(R.id.deliveryPriceTxt);
        totalPriceTxt = findViewById(R.id.totalPriceTxt);
        etCoupon = findViewById(R.id.etCoupen);
    }

    private void initCartList() {
        rvCartView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvCartView.setAdapter(new CartAdapter(
                management.getListCart(),
                this,
                new ChangeNumberItemsListener() {
                    @Override
                    public void onChanged() {
                        calculateCart();
                    }
                }
        ));
    }

    private void setVariable() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void calculateCart() {
        double percentTax = 0.02;
        double delivery = 15.0;

        tax = Math.round((management.getTotalFee() * percentTax) * 100) / 100.0;
        double total = Math.round((management.getTotalFee() + tax + delivery) * 100) / 100.0;
        double itemTotal = Math.round(management.getTotalFee() * 100) / 100.0;

        subTotalPriceTxt.setText("$" + itemTotal);
        totalTaxPriceTxt.setText("$" + tax);
        deliveryPriceTxt.setText("$" + delivery);
        totalPriceTxt.setText("$" + total);
    }
}
