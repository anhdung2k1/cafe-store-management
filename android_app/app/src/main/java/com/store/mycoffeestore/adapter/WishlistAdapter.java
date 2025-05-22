package com.store.mycoffeestore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.store.mycoffeestore.R;
import com.store.mycoffeestore.model.Product;

import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {

    private final Context context;
    private List<Product> wishlist;
    private final OnWishlistActionListener listener;

    public interface OnWishlistActionListener {
        void onRemove(Product product);
    }

    public WishlistAdapter(Context context, List<Product> wishlist, OnWishlistActionListener listener) {
        this.context = context;
        this.wishlist = wishlist;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WishlistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_wishlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistAdapter.ViewHolder holder, int position) {
        Product product = wishlist.get(position);
        holder.productName.setText(product.getProductName());
        holder.productPrice.setText(String.format("$%.2f", product.getProductPrice()));

        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.coffee_sample)
                .into(holder.productImage);

        holder.wishlistIcon.setOnClickListener(v -> {
            listener.onRemove(product);
        });
    }

    @Override
    public int getItemCount() {
        return wishlist.size();
    }

    public void updateList(List<Product> updatedList) {
        this.wishlist = updatedList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage, wishlistIcon;
        TextView productName, productPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            wishlistIcon = itemView.findViewById(R.id.wishlistIcon);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
        }
    }
}