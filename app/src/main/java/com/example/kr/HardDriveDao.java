package com.example.kr;

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
    @Query("SELECT * FROM hard_drives WHERE Capacity > :minCapacity")
    LiveData<List<HardDriveData>> findByMinCapacity(double minCapacity);
    @Query("SELECT * FROM hard_drives WHERE Capacity > :minCapacity AND Capacity < :maxCapacity")
    LiveData<List<HardDriveData>> findByCapacity(double minCapacity, double maxCapacity);
    @Query("SELECT * FROM hard_drives WHERE Capacity < :maxCapacity")
    LiveData<List<HardDriveData>> findByMaxCapacity(double maxCapacity);
    @Query("SELECT * FROM hard_drives WHERE Interface LIKE :interfce")
    LiveData<List<HardDriveData>> findByInterface(String interfce);
    @Query("SELECT * FROM hard_drives WHERE RotatingSpeed LIKE :speed")
    LiveData<List<HardDriveData>> findBySpeed(int speed);
    @Query("SELECT * FROM hard_drives WHERE CacheMemory LIKE :cache")
    LiveData<List<HardDriveData>> findByCache(String cache);
    @Query("SELECT * FROM hard_drives WHERE FormFactor LIKE :formfactor")
    LiveData<List<HardDriveData>> findByFormFactor(String formfactor);

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
