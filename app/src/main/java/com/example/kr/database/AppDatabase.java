package com.example.kr.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.kr.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Database(entities = {HardDriveData.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase
{
    public abstract HardDriveDao hardDriveDao();
    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
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

            });
        }
    };

    public static void loadHardDrivesFromXml(final Context context) {
        databaseWriteExecutor.execute(() -> {

            HardDriveDao dao = INSTANCE.hardDriveDao();

            try {
                ArrayList<HardDriveData> hardDrives = new ArrayList<>();

                String[] hardDriveStrings = context.getResources().getStringArray(R.array.hard_drives_data);

                for (String hardDriveString : hardDriveStrings) {
                    String[] parts = hardDriveString.split(",");
                    if (parts.length == 6)
                    {
                        HardDriveData hardDrive = new HardDriveData(
                                parts[0],
                                parts[1],
                                Double.parseDouble(parts[2]),
                                parts[3],
                                parts[4],
                                Integer.parseInt(parts[5])
                        );
                        hardDrives.add(hardDrive);
                    }
                }

                for (int i=0;i<hardDrives.size();i++) {
                    hardDrives.get(i).uid = i+1;
                    dao.update(hardDrives.get(i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void start(final Context context) {
        databaseWriteExecutor.execute(() -> {

            HardDriveDao dao = INSTANCE.hardDriveDao();

            try {
                ArrayList<HardDriveData> hardDrives = new ArrayList<>();

                String[] hardDriveStrings = context.getResources().getStringArray(R.array.hard_drives_data);

                for (String hardDriveString : hardDriveStrings) {
                    String[] parts = hardDriveString.split(",");
                    if (parts.length == 6)
                    {
                        HardDriveData hardDrive = new HardDriveData(
                                parts[0],
                                parts[1],
                                Double.parseDouble(parts[2]),
                                parts[3],
                                parts[4],
                                Integer.parseInt(parts[5])
                        );
                        hardDrives.add(hardDrive);
                    }
                }

                dao.deleteAll();
                dao.insertAll(hardDrives.toArray(new HardDriveData[hardDrives.size()]));

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void updateItem(final Context context, HardDriveData hardDriveData) {
        databaseWriteExecutor.execute(() -> {

            HardDriveDao dao = INSTANCE.hardDriveDao();

            try {
                dao.update(hardDriveData);
                Log.d("DriveData", hardDriveData.toString());

            } catch (Exception e) {

                Log.e("Database", "Error", e);
                e.printStackTrace();
            }
        });
    }
}
