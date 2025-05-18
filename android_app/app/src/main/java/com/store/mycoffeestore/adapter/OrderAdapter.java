package com.store.mycoffeestore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.store.mycoffeestore.R;
import com.store.mycoffeestore.model.Order;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private final Context context;
    private final List<Order> orders;

    /**
     * Constructs an OrderAdapter with the specified context and list of orders.
     *
     * @param orders the list of Order objects to display in the adapter
     */
    public OrderAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    /**
     * Creates and returns a new ViewHolder for an order item by inflating the corresponding layout.
     *
     * @param parent the parent ViewGroup into which the new view will be added
     * @param viewType the view type of the new view (unused)
     * @return a new ViewHolder instance for an order item
     */
    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_order, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds the data from the specified Order to the views in the provided ViewHolder.
     *
     * @param holder the ViewHolder containing the views to populate
     * @param position the position of the Order in the list
     */
    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.orderId.setText("Order #" + order.getOrderID());
        holder.status.setText(order.getOrderStatus());
        holder.total.setText(String.format("$%.2f", order.getTotalAmount()));

        // Optional: icon tint based on status
    }

    /**
     * Returns the total number of orders in the adapter.
     *
     * @return the number of orders displayed in the RecyclerView
     */
    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView orderId, status, total;

        /**
         * Initializes the ViewHolder by caching references to the order item UI components.
         *
         * @param itemView the view representing a single order item in the RecyclerView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.order_icon);
            orderId = itemView.findViewById(R.id.order_id);
            status = itemView.findViewById(R.id.order_status);
            total = itemView.findViewById(R.id.order_total);
        }
    }
}
