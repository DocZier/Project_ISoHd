package com.example.kr.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface HardDriveDao
{
    @Query("SELECT * FROM hard_drives")
    LiveData<List<HardDriveData>> getAll();
    @Query("SELECT * FROM hard_drives WHERE uid IN (:userIds)")
    LiveData<List<HardDriveData>> loadAllByIds(int[] userIds);
    @Query("SELECT * FROM hard_drives WHERE Manufactor LIKE :manufactor")
    LiveData<List<HardDriveData>> findByManufactor(String manufactor);
    @Query("SELECT * FROM hard_drives WHERE Capacity > :minCapacity AND Capacity < :maxCapacity")
    LiveData<List<HardDriveData>> findByCapacity(double minCapacity, double maxCapacity);
    @Query("SELECT * FROM hard_drives WHERE Interface LIKE :interfce")
    LiveData<List<HardDriveData>> findByInterface(String interfce);
    @Query("SELECT * FROM hard_drives WHERE FormFactor LIKE :formfactor")
    LiveData<List<HardDriveData>> findByFormFactor(String formfactor);
    @Query("SELECT * FROM hard_drives WHERE " +
            "Manufactor IN (:manufactors) AND " +
            "Capacity BETWEEN :minCapacity AND :maxCapacity AND " +
            //"Interface IN (:interfaces) AND " +
            "FormFactor IN (:formFactors) AND " +
            "RotatingSpeed BETWEEN :minRotatingSpeed AND :maxRotatingSpeed")
    LiveData<List<HardDriveData>> getFiltered(String[] manufactors, double minCapacity,
                                              double maxCapacity, //String[] interfaces,
                                              Double[] formFactors, int minRotatingSpeed,
                                              int maxRotatingSpeed);
    @Query("SELECT * FROM hard_drives WHERE uid LIKE :id")
    LiveData<HardDriveData> findID(int id);

    @Insert
    void insertAll(HardDriveData... hard_drives);

    @Insert
    void insert(HardDriveData hard_drives);

    @Update
    void update(HardDriveData hardDriveData);

    @Delete
    void delete(HardDriveData hard_drives);

    @Query("DELETE FROM hard_drives")
    void deleteAll();
}
