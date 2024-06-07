package com.example.kr.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kr.R;
import com.example.kr.database.HardDriveData;
import com.example.kr.database.HistoryData;
import com.example.kr.decorator.DecoratorRecyclerView;

import java.util.ArrayList;

public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.ViewHolder> {

    private ArrayList<HistoryData> historyDataList;
    private RecyclerView outerRecyclerView;
    private AdapterCallback callback;
    public HistoryRecyclerViewAdapter(RecyclerView outerRecyclerView, AdapterCallback callback)
    {
        this.historyDataList = new ArrayList<>();
        this.callback = callback;
        this.outerRecyclerView = outerRecyclerView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_recyclerview_unit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        ArrayList<HardDriveData> hardDriveDataForDate = historyDataList.get(position).getHardDriveDataList();
        String date = historyDataList.get(position).getDate();

        holder.dateTextView.setText(date);

        AdapterRecyclerView innerAdapter = new AdapterRecyclerView(callback);
        innerAdapter.setHardDriveDataList(hardDriveDataForDate);


        holder.innerRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.innerRecyclerView.addItemDecoration(new DecoratorRecyclerView(12));
        holder.innerRecyclerView.setAdapter(innerAdapter);

        holder.innerRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1))
                {

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return historyDataList.size();
    }


    public void addHistoryDataList(HistoryData historyData)
    {
        this.historyDataList.add(historyData);
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTextView;
        public RecyclerView innerRecyclerView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.dateTextView = itemView.findViewById(R.id.data);
            this.innerRecyclerView = itemView.findViewById(R.id.hdds_recycler_view);
        }
    }
}