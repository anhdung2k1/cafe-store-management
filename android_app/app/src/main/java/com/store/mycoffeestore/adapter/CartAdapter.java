package com.store.mycoffeestore.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.store.mycoffeestore.R;
import com.store.mycoffeestore.helper.ChangeNumberItemsListener;
import com.store.mycoffeestore.helper.ManagementCart;
import com.store.mycoffeestore.model.ItemsModel;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private ArrayList<ItemsModel> listItemSelected;
    private ManagementCart managementCart;
    private ChangeNumberItemsListener changeNumberItemsListener;

    public CartAdapter(ArrayList<ItemsModel> listItemSelected, Context context, ChangeNumberItemsListener listener) {
        this.listItemSelected = listItemSelected;
        this.managementCart = new ManagementCart(context);
        this.changeNumberItemsListener = listener;
    }

    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ItemsModel item = listItemSelected.get(position);

        holder.titleTxt.setText(item.getTitle());
        holder.feeEachItem.setText("$" + item.getPrice());
        holder.numberItemTxt.setText(String.valueOf(item.getNumberInCart()));
        holder.totalEachItem.setText("$" + Math.round(item.getPrice() * item.getNumberInCart()));

        Glide.with(holder.itemView.getContext())
                .load(item.getPicUrl().get(0))
                .apply(new RequestOptions().transform(new CenterCrop()))
                .into(holder.cartPicture);

        holder.plusCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                managementCart.plusItem(listItemSelected, position, new ChangeNumberItemsListener() {
                    @Override
                    public void onChanged() {
                        notifyDataSetChanged();
                        if (changeNumberItemsListener != null) {
                            changeNumberItemsListener.onChanged();
                        }
                    }
                });
            }
        });

        holder.minusCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                managementCart.minusItem(listItemSelected, position, new ChangeNumberItemsListener() {
                    @Override
                    public void onChanged() {
                        notifyDataSetChanged();
                        if (changeNumberItemsListener != null) {
                            changeNumberItemsListener.onChanged();
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItemSelected.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleTxt, feeEachItem, totalEachItem, numberItemTxt, minusCartBtn, plusCartBtn;
        ImageView cartPicture;

        public ViewHolder(View itemView) {
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
}
