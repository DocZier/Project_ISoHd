package com.example.kr.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.kr.web.WebPageParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Database(entities = {HardDriveData.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase
{
    public abstract HardDriveDao hardDriveDao();
    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "HDDDataBase.db").addCallback(sRoomDatabaseCallback).build();
                }
            }
        }
        return INSTANCE;
    }

    private static Callback sRoomDatabaseCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                HardDriveDao dao = INSTANCE.hardDriveDao();
                dao.deleteAll();

                WebPageParser webPageParser = new WebPageParser();

                try {
                    webPageParser.getLinks();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                for(int i=0;i<5;i++)
                {
                    ArrayList<HardDriveData> driveData;
                    try
                    {
                        driveData = webPageParser.getData(i);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        Thread.sleep(new Random().nextInt(12500)+2500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    for(HardDriveData j: driveData)
                        dao.insertAll(j);
                }
            });
        }
    };
}
