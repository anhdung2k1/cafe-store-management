package com.store.mycoffeestore.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.store.mycoffeestore.R;
import com.store.mycoffeestore.api.ApiClient;
import com.store.mycoffeestore.api.ApiService;
import com.store.mycoffeestore.helper.ChangeNumberItemsListener;
import com.store.mycoffeestore.model.ItemsModel;
import com.store.mycoffeestore.model.Product;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private final ArrayList<ItemsModel> listItemSelected;
    private final Context context;
    private final ChangeNumberItemsListener changeNumberItemsListener;
    private final Long userId;

    public CartAdapter(ArrayList<ItemsModel> listItemSelected, Context context, Long userId, ChangeNumberItemsListener listener) {
        this.listItemSelected = listItemSelected;
        this.context = context;
        this.userId = userId;
        this.changeNumberItemsListener = listener;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_cart, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ItemsModel item = listItemSelected.get(position);

        holder.titleTxt.setText(item.getTitle());
        holder.feeEachItem.setText(String.format("$%.2f", item.getPrice()));
        holder.numberItemTxt.setText(String.valueOf(item.getNumberInCart()));
        holder.totalEachItem.setText(String.format("$%.2f", item.getPrice() * item.getNumberInCart()));

        if (!item.getPicUrl().isEmpty()) {
            Glide.with(context)
                    .load(item.getPicUrl().get(0))
                    .apply(new RequestOptions().transform(new CenterCrop()))
                    .into(holder.cartPicture);
        }

        holder.plusCartBtn.setOnClickListener(view -> {
            item.setNumberInCart(item.getNumberInCart() + 1);
            updateCartOnServer(item, true);
        });

        holder.minusCartBtn.setOnClickListener(view -> {
            int count = item.getNumberInCart();
            if (count > 1) {
                item.setNumberInCart(count - 1);
                updateCartOnServer(item, true);
            } else {
                updateCartOnServer(item, false);
                listItemSelected.remove(position);
                notifyItemRemoved(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItemSelected.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, feeEachItem, totalEachItem, numberItemTxt, minusCartBtn, plusCartBtn;
        ImageView cartPicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            feeEachItem = itemView.findViewById(R.id.feeEachItem);
            totalEachItem = itemView.findViewById(R.id.totalEachItem);
            numberItemTxt = itemView.findViewById(R.id.numberItemTxt);
            minusCartBtn = itemView.findViewById(R.id.minusCartBtn);
            plusCartBtn = itemView.findViewById(R.id.plusCartBtn);
            cartPicture = itemView.findViewById(R.id.cartPicture);
        }
    }

    private void updateCartOnServer(ItemsModel item, boolean isUpdate) {
        Product product = getProduct(item);
        Log.d("updateCartOnServer", "product: " + product);
        ApiService api = ApiClient.getSecuredApiService(context);
        Call<Map<String, Object>> call = isUpdate
                ? api.addToCart(userId, product)   // POST
                : api.updateCart(userId, product); // PATCH

        call.enqueue(new Callback<>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
                notifyDataSetChanged();
                if (changeNumberItemsListener != null) {
                    changeNumberItemsListener.onChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                Toast.makeText(context, "Failed to update cart", Toast.LENGTH_SHORT).show();
                Log.e("CartAdapter", "API error", t);
            }
        });
    }

    @NonNull
    private static Product getProduct(ItemsModel item) {
        Product product = new Product();
        product.setProductID(item.getProductID());
        product.setProductName(item.getTitle());
        product.setProductModel(item.getExtra() == null ? "DefaultProductModel" : item.getExtra());
        product.setProductPrice(item.getPrice());
        product.setProductQuantity(item.getNumberInCart());
        product.setProductDescription(item.getDescription());
        product.setProductType("Popular");

        if (!item.getPicUrl().isEmpty()) {
            product.setImageUrl(item.getPicUrl().get(0));
        }
        return product;
    }
}