package com.example.kr;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "hard_drives")
public class HardDriveData
{
    @PrimaryKey(autoGenerate = true)
    public int uid;
    @ColumnInfo(name = "Manufactor")
    private String manufactor;
    @ColumnInfo(name = "Model")
    private String model;
    @ColumnInfo(name = "Capacity")
    private double capacity;
    @ColumnInfo(name = "Interface")
    private String interfc;
    @ColumnInfo(name = "FormFactor")
    private String formFactor;
    @ColumnInfo(name = "CacheMemory")
    private String cache;
    @ColumnInfo(name = "RotatingSpeed")
    private int speed;

    public HardDriveData()
    {}

    public HardDriveData(String manufactor, String model, double capacity, String interfc, String formFactor, String cache, int speed) {
        this.manufactor = manufactor;
        this.model = model;
        this.capacity = capacity;
        this.interfc = interfc;
        this.formFactor = formFactor;
        this.cache = cache;
        this.speed = speed;
    }

    public String getManufactor() {
        return manufactor;
    }

    public void setManufactor(String manufactor) {
        this.manufactor = manufactor;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public String getInterfc() {
        return interfc;
    }

    public void setInterfc(String interfc) {
        this.interfc = interfc;
    }

    public String getFormFactor() {
        return formFactor;
    }

    public void setFormFactor(String formFactor) {
        this.formFactor = formFactor;
    }

    public String getCache() {
        return cache;
    }

    public void setCache(String cache) {
        this.cache = cache;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
