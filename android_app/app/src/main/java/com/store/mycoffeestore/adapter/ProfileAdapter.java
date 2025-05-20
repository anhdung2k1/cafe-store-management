package com.store.mycoffeestore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.store.mycoffeestore.R;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(String title, String updatedValue);
    }

    private final Context context;
    private final List<Object[]> itemList;
    private final OnItemClickListener listener;

    public ProfileAdapter(Context context, List<Object[]> itemList, OnItemClickListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_profile_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.ViewHolder holder, int position) {
        Object[] item = itemList.get(position);
        String title = (String) item[0];
        String value = (String) item[1];
        int iconResId = (int) item[2];
        boolean isAction = (boolean) item[3];

        holder.title.setText(title);
        holder.icon.setImageResource(iconResId);

        if (isAction) {
            holder.value.setVisibility(View.GONE);
            holder.editValue.setVisibility(View.GONE);
            holder.editButton.setVisibility(View.GONE);
            holder.title.setTextColor(context.getColor(R.color.black));
            holder.icon.setColorFilter(context.getColor(R.color.black));
        } else {
            holder.value.setVisibility(View.VISIBLE);
            holder.editValue.setVisibility(View.GONE);
            holder.editButton.setVisibility(View.VISIBLE);

            holder.value.setText(value);
            holder.editValue.setText(value);

            holder.title.setTextColor(context.getColor(R.color.black));
            holder.icon.setColorFilter(context.getColor(R.color.black));

            // Toggle edit/done
            holder.editButton.setOnClickListener(v -> {
                if (!holder.isEditing) {
                    // Bắt đầu edit
                    holder.value.setVisibility(View.GONE);
                    holder.editValue.setVisibility(View.VISIBLE);
                    holder.editButton.setImageResource(R.drawable.ic_done);
                } else {
                    // Xác nhận sửa
                    String updatedText = holder.editValue.getText().toString().trim();
                    if (updatedText.isEmpty()) {
                        holder.editValue.setError("Field cannot be empty");
                        return;
                    }
                    holder.value.setText(updatedText);
                    holder.value.setVisibility(View.VISIBLE);
                    holder.editValue.setVisibility(View.GONE);
                    holder.editButton.setImageResource(R.drawable.ic_edit);
                    holder.isEditing = false;

                    // Cập nhật lại itemList
                    itemList.set(holder.getAdapterPosition(), new Object[]{title, updatedText, iconResId, false});

                    if (listener != null) {
                        listener.onItemClick(title, updatedText);
                    }
                }
                holder.isEditing = !holder.isEditing;
            });
        }

        // Bấm cả item (tuỳ chọn)
        holder.itemView.setOnClickListener(v -> {
            if (isAction && listener != null) {
                listener.onItemClick(title, "");
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon, editButton;
        TextView title, value;
        EditText editValue;
        boolean isEditing = false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.profile_icon);
            title = itemView.findViewById(R.id.profile_title);
            value = itemView.findViewById(R.id.profile_value);
            editValue = itemView.findViewById(R.id.profile_edit);
            editButton = itemView.findViewById(R.id.edit_button);
        }
    }
}