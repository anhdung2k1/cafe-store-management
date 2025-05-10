package com.store.mycoffeestore.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.google.android.material.imageview.ShapeableImageView;

import com.store.mycoffeestore.R;
import com.store.mycoffeestore.activity.DetailedActivity;
import com.store.mycoffeestore.model.ItemsModel;

import java.util.List;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.ViewHolder> {

    private Context context;
    private List<ItemsModel> items;

    public PopularAdapter(List<ItemsModel> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_popular, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemsModel item = items.get(position);

        holder.titleTxt.setText(item.getTitle());
        holder.priceTxt.setText("$" + item.getPrice());
        holder.ratingBar.setRating(item.getRating());
        holder.extraTxt.setText(item.getExtra());

        Glide.with(holder.itemView.getContext())
                .load(item.getPicUrl().get(0))
                .into(holder.shapeableImageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), DetailedActivity.class);
            intent.putExtra("object", item);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleTxt, extraTxt, priceTxt;
        RatingBar ratingBar;
        ShapeableImageView shapeableImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            extraTxt = itemView.findViewById(R.id.extraTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            shapeableImageView = itemView.findViewById(R.id.shapeableImageView);
        }
    }
}
