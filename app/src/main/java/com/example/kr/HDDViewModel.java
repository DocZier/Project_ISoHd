package com.example.kr;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;


public class HDDViewModel extends AndroidViewModel
{
    private HardDriveDao mDriveDao;
    private LiveData<List<HardDriveData>> Drivers;

    public HDDViewModel(Application application)
    {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        mDriveDao = db.hardDriveDao();
        Drivers = mDriveDao.getAll();
    }

    LiveData<List<HardDriveData>> getDrivers()
    {
        return Drivers;
    }

    public void reset()
    {
        Drivers = mDriveDao.getAll();
    }

    public void filter(ArrayList<String> manufactors, double min, double max)
    {
        //TODO закончить фильтрацию, желательно не через огромное количество if и запросов, а получать и фильтровать их.
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
