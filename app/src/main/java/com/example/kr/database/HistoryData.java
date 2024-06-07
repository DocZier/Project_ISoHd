package com.example.kr.database;

import java.util.ArrayList;

public class HistoryData {
    private String date;
    private ArrayList<HardDriveData> hardDriveDataList;

    public HistoryData(String date, ArrayList<HardDriveData> hardDriveDataList) {
        this.date = date;
        this.hardDriveDataList = hardDriveDataList;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<HardDriveData> getHardDriveDataList() {
        return hardDriveDataList;
    }

    public void setHardDriveDataList(ArrayList<HardDriveData> hardDriveDataList)
    {
        this.hardDriveDataList = hardDriveDataList;
    }
}
