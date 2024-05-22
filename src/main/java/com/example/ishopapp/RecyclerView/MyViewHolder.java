package com.example.ishopapp.RecyclerView;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ishopapp.R;

class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView textView,eachP,quantity,price,discount;


    public MyViewHolder(View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.text);
        eachP = itemView.findViewById(R.id.eachPrice);
        quantity = itemView.findViewById(R.id.quantity);
        price = itemView.findViewById(R.id.price);
        discount = itemView.findViewById(R.id.discount);
    }
}

