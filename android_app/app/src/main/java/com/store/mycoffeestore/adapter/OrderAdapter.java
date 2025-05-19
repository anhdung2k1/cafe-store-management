package com.store.mycoffeestore.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.store.mycoffeestore.R;
import com.store.mycoffeestore.activity.OrderDetailActivity;
import com.store.mycoffeestore.model.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private final Context context;
    private List<Order> orders = new ArrayList<>();

    public OrderAdapter(Context context) {
        this.context = context;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setOrders(List<Order> newOrders) {
        if (newOrders != null) {
            this.orders = newOrders;
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_order, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, int position) {
        Order order = orders.get(position);

        Log.w("OrderAdapter", "Binding Order: " + order);
        Log.w("OrderAdapter", "Adapter item count: " + getItemCount());

        String cleanStatus = order.getOrderStatus() != null ? order.getOrderStatus().replace("\"", "") : "Unknown";

        holder.orderId.setText("Order #" + order.getOrderID());
        holder.status.setText(cleanStatus);
        holder.total.setText(String.format("$%.2f", order.getTotalAmount()));

        // Icon based on status
        if (cleanStatus.equalsIgnoreCase("Success")) {
            holder.icon.setImageResource(R.drawable.ic_success);
        } else if (cleanStatus.equalsIgnoreCase("Processing")) {
            holder.icon.setImageResource(R.drawable.ic_pending);
        } else {
            holder.icon.setImageResource(R.drawable.ic_order);
        }

        // Open OrderDetailActivity with full Order object
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra("order", order); // Requires Order implements Serializable
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView orderId, status, total;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.order_icon);
            orderId = itemView.findViewById(R.id.order_id);
            status = itemView.findViewById(R.id.order_status);
            total = itemView.findViewById(R.id.order_total);
        }
    }
}