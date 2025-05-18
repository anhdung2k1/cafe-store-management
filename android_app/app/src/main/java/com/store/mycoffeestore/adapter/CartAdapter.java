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

    /**
     * Binds the data of a cart item to the corresponding ViewHolder, updating UI elements and handling image loading and quantity changes.
     *
     * Updates the item's title, price, quantity, and total price in the view. Loads the item's image if available, or sets a fallback image if not. Handles incrementing and decrementing the item quantity, updating the server and UI accordingly. Removes the item from the list if its quantity reaches zero.
     *
     * @param holder   the ViewHolder to bind data to
     * @param position the position of the item in the adapter's data set
     */
    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ItemsModel item = listItemSelected.get(position);

        holder.titleTxt.setText(item.getTitle());
        holder.feeEachItem.setText(String.format("$%.2f", item.getPrice()));
        holder.numberItemTxt.setText(String.valueOf(item.getNumberInCart()));
        holder.totalEachItem.setText(String.format("$%.2f", item.getPrice() * item.getNumberInCart()));

        Log.w("CartAdapter", "Image URL: " + item.getPicUrl());
        if (item.getPicUrl() != null && !item.getPicUrl().isEmpty()) {
            String imageUrl = item.getPicUrl().get(0);
            Log.w("CartAdapter", "Loading image: " + imageUrl);
            Glide.with(context)
                    .load(imageUrl)
                    .apply(new RequestOptions().transform(new CenterCrop()))
                    .into(holder.cartPicture);
        } else {
            Log.w("CartAdapter", "Loading fallback image");
            holder.cartPicture.setImageResource(R.drawable.coffee); // fallback image
        }

        holder.plusCartBtn.setOnClickListener(view -> {
            item.setNumberInCart(item.getNumberInCart() + 1);
            updateCartOnServer(item, position);
        });

        holder.minusCartBtn.setOnClickListener(view -> {
            int count = item.getNumberInCart();
            if (count > 1) {
                item.setNumberInCart(count - 1);
                updateCartOnServer(item, position);
            } else {
                item.setNumberInCart(0);
                updateCartOnServer(item, position);
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

    /**
     * Updates the cart item on the server with the latest quantity and details.
     *
     * Initiates an asynchronous API call to update the specified cart item for the user.
     * On success, refreshes the item in the adapter and notifies the quantity change listener if present.
     * On failure, displays a toast message and logs the error.
     *
     * @param item the cart item to update
     * @param position the position of the item in the adapter
     */
    private void updateCartOnServer(ItemsModel item, int position) {
        Product product = getProduct(item);
        Log.d("CartAdapter", "Updating cart: " + product.getProductName() + ", Qty=" + product.getProductQuantity());

        ApiService api = ApiClient.getSecuredApiService(context);
        Call<Map<String, Object>> call = api.updateCart(userId, product);

        call.enqueue(new Callback<>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
                notifyItemChanged(position);
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

    /**
     * Converts an {@link ItemsModel} object to a {@link Product} object for cart operations.
     *
     * Populates the product's fields with corresponding values from the item, including ID, name, model,
     * price, quantity, description, and type. If the item has a non-empty image URL list, sets the product's
     * image URL to the first entry.
     *
     * @param item the cart item to convert
     * @return a {@link Product} object populated with the item's data
     */
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

        if (item.getPicUrl() != null && !item.getPicUrl().isEmpty()) {
            product.setImageUrl(item.getPicUrl().get(0));
        }
        return product;
    }
}