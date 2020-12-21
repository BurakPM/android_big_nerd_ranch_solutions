package com.greent.criminalintent;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import com.greent.criminalintent.database.CrimeDao;
import com.greent.criminalintent.database.CrimeDatabase;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.reactivex.Flowable;


public class CrimeRepository {
    private static CrimeRepository sCrimeRepository = null;
    private static String DATABASE_NAME = "crime-database";
    private CrimeDatabase database;
    private CrimeDao crimeDao;
    private Executor mExecutor = Executors.newSingleThreadExecutor();
    private  File filesDir;



    private CrimeRepository(Context context) {


        database = Room.
                databaseBuilder(context.getApplicationContext(), CrimeDatabase.class, DATABASE_NAME).
                addMigrations(CrimeDatabase.MIGRATION_2_3, CrimeDatabase.MIGRATION_3_4).
                build();


        crimeDao = database.crimeDao();
        filesDir = context.getApplicationContext().getFilesDir();

    }


    static synchronized  void init(Context context) {
        if (sCrimeRepository == null) {
            sCrimeRepository = new CrimeRepository(context);
        }
    }

    public static synchronized CrimeRepository get() {
        if (sCrimeRepository == null) {
            throw new IllegalStateException("CrimeRepository must be initialized");
        } else {
            return sCrimeRepository;
        }

    }


    public Flowable<List<Crime>> getCrimes() {
        return crimeDao.getCrimes();
    }

    public LiveData<Crime> getCrime(UUID id) {
        return crimeDao.getCrime(id);
    }

    public Flowable<Integer> getCount() {
        return crimeDao.getRowCount();
    }

    void updateCrime(Crime crime) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    crimeDao.updateCrime(crime);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public File getPhotoFile(Crime crime) {
        return new File(filesDir, crime.getPhotoFileName());
    }


    void addCrime(Crime crime) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    crimeDao.addCrime(crime);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void deleteCrime(Crime crime) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try{
                    crimeDao.deleteCrime(crime);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

