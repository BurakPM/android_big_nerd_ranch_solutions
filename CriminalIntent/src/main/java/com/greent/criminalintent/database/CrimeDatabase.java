package com.greent.criminalintent.database;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.greent.criminalintent.Crime;
import com.greent.criminalintent.database.CrimeDao;
import com.greent.criminalintent.database.CrimeTypeConverters;

@Database(entities = {Crime.class}, version = 4)
@TypeConverters(CrimeTypeConverters.class)
public abstract class CrimeDatabase extends RoomDatabase {
    public abstract CrimeDao crimeDao();


   public static final Migration MIGRATION_2_3 = new Migration(2,3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
                database.execSQL("ALTER TABLE Crime ADD COLUMN mSuspect TEXT NOT NULL DEFAULT ' '");
        }
    };

   public static final Migration MIGRATION_3_4 = new Migration(3,4) {
       @Override
       public void migrate(@NonNull SupportSQLiteDatabase database) {
           database.execSQL("ALTER TABLE Crime ADD COLUMN mSuspectPhoneNumber TEXT");
       }
   };
}


