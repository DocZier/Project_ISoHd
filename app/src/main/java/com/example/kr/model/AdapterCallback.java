package com.example.kr.model;

import com.example.kr.database.HardDriveData;

public interface AdapterCallback {
    void onShowBottomSheet(HardDriveData hardDriveData);
    void saveHistoryData(HardDriveData hardDriveData, String currentDate);
}
