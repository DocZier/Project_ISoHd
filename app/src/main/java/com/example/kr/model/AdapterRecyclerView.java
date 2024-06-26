package com.example.kr.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kr.R;
import com.example.kr.database.HardDriveData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterRecyclerView extends RecyclerView.Adapter<AdapterRecyclerView.ViewHolder> {

    private List<HardDriveData> hardDriveDataList;
    private final AdapterCallback callback;

    public AdapterRecyclerView(AdapterCallback callback) {
        this.hardDriveDataList = new ArrayList<>();
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hdd_recyclerview_unit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HardDriveData hardDriveData = hardDriveDataList.get(position);
        holder.modelTextView.setText(hardDriveData.getModel());
        double capacity = hardDriveData.getCapacity();
        holder.capacityTextView.setText(capacity / 1000 > 0 ? (capacity / 1000 + " TB") : (capacity + " GB"));

        holder.itemView.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            String currentDate = dateFormat.format(calendar.getTime());

            callback.saveHistoryData(hardDriveData, currentDate);

            callback.onShowBottomSheet(hardDriveData);
        });

    }

    @Override
    public int getItemCount() {
        return hardDriveDataList.size();
    }

    public void setHardDriveDataList(List<HardDriveData> hardDriveDataList) {
        this.hardDriveDataList = hardDriveDataList;
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView modelTextView;
        public TextView capacityTextView;
        public ViewSwitcher viewSwitcher;
        public LinearLayout infoLayout;
        public ImageButton favoriteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            this.modelTextView = itemView.findViewById(R.id.view_modelname);
            this.capacityTextView = itemView.findViewById(R.id.view_capacity);
            this.viewSwitcher = itemView.findViewById(R.id.viewSwitcher);
            this.infoLayout = itemView.findViewById(R.id.unit);
            this.favoriteButton = itemView.findViewById(R.id.act_add_favorite);
        }
    }
}

