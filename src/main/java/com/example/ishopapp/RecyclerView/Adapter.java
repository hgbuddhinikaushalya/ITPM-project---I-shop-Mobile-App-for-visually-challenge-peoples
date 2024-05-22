package com.example.ishopapp.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ishopapp.R;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<MyViewHolder> {
    private final List<String> dataList1;
    private final List<String> dataList2;
    private final List<String> dataList3;
    private final List<String> dataList4;
    private final List<String> dataList5;

    public Adapter(List<String> dataList1,List<String> dataList2,List<String> dataList3,List<String> dataList4,List<String> dataList5) {

        this.dataList1 = dataList1;
        this.dataList2 = dataList2;
        this.dataList3 = dataList3;
        this.dataList4 = dataList4;
        this.dataList5 = dataList5;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String data1 = dataList1.get(position);
        String data2 = dataList2.get(position);
        String data3 = dataList3.get(position);
        String data4 = dataList4.get(position);
        String data5 = dataList5.get(position);
        holder.textView.setText(data1);
        holder.eachP.setText("Net (Rs.) " + data2);
        holder.quantity.setText("Qty: "+data3);
        holder.price.setText("Rs. "+data4);
        holder.discount.setText("Dis. "+data5);
    }

    @Override
    public int getItemCount() {
        return dataList1.size();
    }
}

