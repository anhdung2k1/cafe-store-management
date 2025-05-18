package com.store.mycoffeestore.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import android.widget.TextView;
import com.google.android.material.imageview.ShapeableImageView;
import com.store.mycoffeestore.R;
import com.store.mycoffeestore.activity.DetailedActivity;
import com.store.mycoffeestore.model.ItemsModel;

import java.util.List;

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.ViewHolder> {

    private Context context;
    private List<ItemsModel> items;

    public OffersAdapter(List<ItemsModel> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_offer, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemsModel item = items.get(position);

        holder.titleTxt.setText(item.getTitle());
        holder.priceTxt.setText("$" + item.getPrice());

        Glide.with(holder.itemView.getContext())
                .load(item.getPicUrl().get(0))
                .into(holder.shapeableImageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), DetailedActivity.class);
            intent.putExtra("object", item); // item is ItemsModel (Parcelable)
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleTxt, priceTxt;
        ShapeableImageView shapeableImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            shapeableImageView = itemView.findViewById(R.id.shapeableImageView);
        }
    }
}
