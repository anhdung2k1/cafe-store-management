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
import com.store.mycoffeestore.helper.SizeSelectListener;

import java.util.List;

public class SizeAdapter extends RecyclerView.Adapter<SizeAdapter.ViewHolder> {

    private final Context context;
    private final List<String> items;
    private final SizeSelectListener listener;
    private int selectedPosition = -1;
    private int lastSelectedPosition = -1;

    public SizeAdapter(Context context, List<String> items, SizeSelectListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
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
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition == RecyclerView.NO_POSITION) return;

            lastSelectedPosition = selectedPosition;
            selectedPosition = currentPosition;

            notifyItemChanged(lastSelectedPosition);
            notifyItemChanged(selectedPosition);

            // ✅ Callback to notify selected size
            if (listener != null) {
                listener.onSizeSelected(items.get(currentPosition));
            }
        });

        if (selectedPosition == position) {
            holder.coffee.setBackgroundResource(R.drawable.orange_bg);
        } else {
            holder.coffee.setBackgroundResource(R.drawable.size_bg);
        }

        int imageSize = getSizeForPosition(position);
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

    private int getSizeForPosition(int position) {
        switch (position) {
            case 0: return dpToPx(45);
            case 1: return dpToPx(50);
            case 2: return dpToPx(55);
            case 3: return dpToPx(65);
            default: return dpToPx(70);
        }
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