package com.store.mycoffeestore.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import com.store.mycoffeestore.R;

import java.util.List;

public class SizeAdapter extends RecyclerView.Adapter<SizeAdapter.ViewHolder> {

    private final Context context;
    private final List<String> items;
    private int selectedPosition = -1;
    private int lastSelectedPosition = -1;

    public SizeAdapter(Context context, List<String> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public SizeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_size, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SizeAdapter.ViewHolder holder, int position) {
        holder.root.setOnClickListener(v -> {
            lastSelectedPosition = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(lastSelectedPosition);
            notifyItemChanged(selectedPosition);
        });

        if (selectedPosition == position) {
            holder.coffee.setBackgroundResource(R.drawable.orange_bg);
        } else {
            holder.coffee.setBackgroundResource(R.drawable.size_bg);
        }

        int imageSize;
        switch (position) {
            case 0:
                imageSize = dpToPx(45);
                break;
            case 1:
                imageSize = dpToPx(50);
                break;
            case 2:
                imageSize = dpToPx(55);
                break;
            case 3:
                imageSize = dpToPx(65);
                break;
            default:
                imageSize = dpToPx(70);
                break;
        }

        ViewGroup.LayoutParams layoutParams = holder.coffee.getLayoutParams();
        layoutParams.width = imageSize;
        layoutParams.height = imageSize;
        holder.coffee.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private int dpToPx(int dp) {
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics()));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout root;
        ImageView coffee;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.sizeLayout);
            coffee = itemView.findViewById(R.id.coffee);
        }
    }
}
