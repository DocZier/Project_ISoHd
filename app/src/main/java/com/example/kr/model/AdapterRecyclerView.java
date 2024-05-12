package com.example.kr.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kr.R;
import com.example.kr.database.HardDriveData;

import java.util.ArrayList;
import java.util.List;

public class AdapterRecyclerView extends RecyclerView.Adapter<AdapterRecyclerView.ViewHolder> {

    private List<HardDriveData> hardDriveDataList;

    public AdapterRecyclerView()
    {
        hardDriveDataList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hdd_recyclerview_unit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HardDriveData hardDriveData = hardDriveDataList.get(position);
        holder.modelTextView.setText(hardDriveData.getModel());
        double capacity =hardDriveData.getCapacity();
        if(capacity/1000>0)
            holder.capacityTextView.setText(capacity/1000+" TB");
        else
            holder.capacityTextView.setText(capacity+" TB");
    }

    @Override
    public int getItemCount()
    {
        return hardDriveDataList.size();
    }

    public void setHardDriveDataList(List<HardDriveData> hardDriveDataList)
    {
        this.hardDriveDataList = hardDriveDataList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView modelTextView;
        public TextView capacityTextView;

        public ViewHolder(View itemView)
        {
            super(itemView);
            this.modelTextView = itemView.findViewById(R.id.view_modelname);
            this.capacityTextView = itemView.findViewById(R.id.view_capacity);
        }
    }
}
