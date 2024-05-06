package com.example.kr;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

class HDDViewHolder extends RecyclerView.ViewHolder
{
    private TextView model;
    private TextView capacity;
    private TextView manufactor;
    private RecyclerViewInterface recyclerViewInterface;

    private HDDViewHolder(View view, RecyclerViewInterface recyclerViewInterface)
    {
        super(view);
        this.model = view.findViewById(R.id.view_modelname);
        this.capacity = view.findViewById(R.id.view_capacity);
        this.manufactor = view.findViewById(R.id.view_manufactorname);
        this.recyclerViewInterface = recyclerViewInterface;
    }

    public void bind(String man, String model, double cap) {
        this.manufactor.setText(man);
        this.model.setText(model);
        if(cap/1000>0)
            this.capacity.setText(cap/1000+" TB");
        else
            this.capacity.setText(cap+" GB");
    }

    public static HDDViewHolder create(ViewGroup parent, RecyclerViewInterface recyclerViewInterface)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hdd_recyclerview_unit, parent, false);

        return new HDDViewHolder(view, recyclerViewInterface);
    }
}
