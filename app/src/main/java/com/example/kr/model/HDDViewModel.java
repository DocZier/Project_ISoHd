package com.example.kr.model;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.kr.database.AppDatabase;
import com.example.kr.database.HardDriveDao;
import com.example.kr.database.HardDriveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;


public class HDDViewModel extends AndroidViewModel
{
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

    public LiveData<List<HardDriveData>> getDrivers()
    {
        return Drivers;
    }

    public LiveData<List<HardDriveData>> getSortedDrivers() {
        return sortedDrivers;
    }

    public void filterDrivers(ArrayList<String> manufactors, double minCapacity, double maxCapacity,
                              ArrayList<String> interfaces, ArrayList<String> formFactors,
                              int minRotatingSpeed, int maxRotatingSpeed)
    {
        filteredDrivers = mDriveDao.getFiltered(manufactors.toArray(new String[manufactors.size()]),
                minCapacity, maxCapacity, //interfaces.toArray(new String[interfaces.size()]),
                formFactors.toArray(new String[formFactors.size()]), minRotatingSpeed, maxRotatingSpeed);

        sortedDrivers.removeSource(Drivers);

        sortedDrivers.addSource(filteredDrivers, hardDriveData -> {
            List<HardDriveData> sortedList = new ArrayList<>(hardDriveData);
            sortedList.sort(Comparator.comparingDouble(HardDriveData::getCapacity).reversed());
            sortedDrivers.setValue(sortedList);
        });
    }

    public void clearFilteredDrivers()
    {
        sortedDrivers.removeSource(filteredDrivers);

        sortedDrivers.addSource(Drivers, hardDriveData -> {
            List<HardDriveData> sortedList = new ArrayList<>(hardDriveData);
            sortedList.sort(Comparator.comparingDouble(HardDriveData::getCapacity).reversed());
            sortedDrivers.setValue(sortedList);
        });
    }

    LiveData<List<HardDriveData>> getManufactors(String man)
    {
        return mDriveDao.findByManufactor(man);
    }

    LiveData<List<HardDriveData>> getCapacity(double min, double max)
    {
        return mDriveDao.findByCapacity(min, max);
    }

    LiveData<List<HardDriveData>> getInterface(String inter)
    {
        return mDriveDao.findByInterface(inter);
    }

    LiveData<List<HardDriveData>> getFormFactor(String ff)
    {
        return mDriveDao.findByFormFactor(ff);
    }

    public void insert(HardDriveData driver)
    {
        mDriveDao.insert(driver);
    }

    public void update(HardDriveData driver)
    {
        mDriveDao.update(driver);
    }

    public void delete(HardDriveData driver)
    {
        mDriveDao.delete(driver);
    }
}
