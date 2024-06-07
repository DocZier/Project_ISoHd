package com.example.kr.model;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.kr.database.AppDatabase;
import com.example.kr.database.HardDriveDao;
import com.example.kr.database.HardDriveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HDDViewModel extends AndroidViewModel {
    private HardDriveDao mDriveDao;
    private LiveData<List<HardDriveData>> Drivers;
    private LiveData<List<HardDriveData>> filteredDrivers;
    private final MediatorLiveData<List<HardDriveData>> sortedDrivers = new MediatorLiveData<>();
    public HDDViewModel(Application application)
    {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        mDriveDao = db.hardDriveDao();
        Drivers = mDriveDao.getAll();
        filteredDrivers = mDriveDao.getAll();

        sortedDrivers.addSource(Drivers, hardDriveData -> {
            List<HardDriveData> sortedList = new ArrayList<>(hardDriveData);
            sortedList.sort(Comparator.comparingDouble(HardDriveData::getCapacity).reversed());
            sortedDrivers.setValue(sortedList);
        });
    }

    public void favoriteMode()
    {
        sortedDrivers.removeSource(Drivers);

        Drivers = mDriveDao.getFavorite();
        filteredDrivers = mDriveDao.getFavorite();

        sortedDrivers.addSource(Drivers, hardDriveData -> {
            List<HardDriveData> sortedList = new ArrayList<>(hardDriveData);
            sortedList.sort(Comparator.comparingDouble(HardDriveData::getCapacity).reversed());
            sortedDrivers.setValue(sortedList);
        });
    }

    public LiveData<List<HardDriveData>> getSortedDrivers() {
        return sortedDrivers;
    }

    public void filterDrivers(ArrayList<String> manufactors, double minCapacity, double maxCapacity,
                              ArrayList<Double> formFactors, int minRotatingSpeed, int maxRotatingSpeed,
                              boolean favorite)
    {
        sortedDrivers.removeSource(Drivers);
        sortedDrivers.removeSource(filteredDrivers);

        filteredDrivers = mDriveDao.getFiltered(manufactors.toArray(new String[manufactors.size()]),
                    minCapacity, maxCapacity, formFactors.toArray(new Double[formFactors.size()]),
                    minRotatingSpeed, maxRotatingSpeed, favorite, true);

        sortedDrivers.addSource(filteredDrivers, hardDriveData -> {
                List<HardDriveData> sortedList = new ArrayList<>(hardDriveData);
                sortedList.sort(Comparator.comparingDouble(HardDriveData::getCapacity).reversed());
                sortedDrivers.setValue(sortedList);
        });
    }

    public void sort(boolean isTypeA,boolean isLow)
    {
        sortedDrivers.removeSource(filteredDrivers);
        sortedDrivers.removeSource(Drivers);

        filteredDrivers = Drivers;

        if(isTypeA)
        {
            if (isLow)
                sortedDrivers.addSource(filteredDrivers, hardDriveData -> {
                    List<HardDriveData> sortedList = new ArrayList<>(hardDriveData);
                    sortedList.sort(Comparator.comparing(HardDriveData::getModel).reversed());
                    sortedDrivers.setValue(sortedList);
                });
            else
                sortedDrivers.addSource(filteredDrivers, hardDriveData -> {
                    List<HardDriveData> sortedList = new ArrayList<>(hardDriveData);
                    sortedList.sort(Comparator.comparing(HardDriveData::getModel));
                    sortedDrivers.setValue(sortedList);
                });
        }
        else
        {
            if (isLow)
                sortedDrivers.addSource(filteredDrivers, hardDriveData -> {
                    List<HardDriveData> sortedList = new ArrayList<>(hardDriveData);
                    sortedList.sort(Comparator.comparingDouble(HardDriveData::getCapacity).reversed());
                    sortedDrivers.setValue(sortedList);
                });
            else
                sortedDrivers.addSource(filteredDrivers, hardDriveData -> {
                    List<HardDriveData> sortedList = new ArrayList<>(hardDriveData);
                    sortedList.sort(Comparator.comparingDouble(HardDriveData::getCapacity));
                    sortedDrivers.setValue(sortedList);
                });
        }
    }

    public void clearFilteredDrivers() {
        sortedDrivers.removeSource(filteredDrivers);
        sortedDrivers.removeSource(Drivers);

        filteredDrivers = Drivers;
        sortedDrivers.addSource(filteredDrivers, hardDriveData -> {
            List<HardDriveData> sortedList = new ArrayList<>(hardDriveData);
            sortedList.sort(Comparator.comparingDouble(HardDriveData::getCapacity).reversed());
            sortedDrivers.setValue(sortedList);
        });
    }

    public LiveData<List<HardDriveData>> searchHardDrives(String query) {
        List<HardDriveData> filteredList = sortedDrivers.getValue().stream()
                .filter(hardDriveData -> hardDriveData.getModel().toLowerCase().contains(query.toLowerCase()) ||
                        hardDriveData.getManufactor().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        MediatorLiveData<List<HardDriveData>> searchResults = new MediatorLiveData<>();
        searchResults.setValue(filteredList);
        return searchResults;
    }


    public void insert(HardDriveData driver) {
        mDriveDao.insert(driver);
    }

    public void update(HardDriveData driver) {
        mDriveDao.update(driver);
    }

    public void delete(HardDriveData driver) {
        mDriveDao.delete(driver);
    }

}
