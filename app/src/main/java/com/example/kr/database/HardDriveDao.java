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
    @Query("SELECT * FROM hard_drives WHERE Favorite == 1")
    LiveData<List<HardDriveData>> getFavorite();

    @Query("SELECT * FROM hard_drives WHERE uid IN (:userIds)")
    LiveData<List<HardDriveData>> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM hard_drives WHERE " +
            "Manufactor IN (:manufactors) AND " +
            "Capacity BETWEEN :minCapacity AND :maxCapacity AND " +
            "FormFactor IN (:formFactors) AND " +
            "RotatingSpeed BETWEEN :minRotatingSpeed AND :maxRotatingSpeed AND " +
            "( Favorite == :isFavorite OR Favorite == :constant )")
    LiveData<List<HardDriveData>> getFiltered(String[] manufactors, double minCapacity,
                                              double maxCapacity,
                                              Double[] formFactors, int minRotatingSpeed,
                                              int maxRotatingSpeed, boolean isFavorite,
                                              boolean constant);
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
