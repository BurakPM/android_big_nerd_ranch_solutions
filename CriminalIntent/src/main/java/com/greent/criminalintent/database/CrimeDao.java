package com.greent.criminalintent.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.greent.criminalintent.Crime;

import java.util.List;
import java.util.UUID;

import io.reactivex.Flowable;

@Dao
public interface CrimeDao {
    @Query("SELECT * FROM crime")
    Flowable<List<Crime>> getCrimes();

    @Query("SELECT * FROM crime WHERE mId = (:id)")
    LiveData<Crime> getCrime(UUID id);

    @Query("SELECT COUNT(*) FROM crime")
    Flowable<Integer> getRowCount();

    @Update
    void updateCrime(Crime crime);

    @Insert
    void addCrime(Crime crime);

    @Delete
    void deleteCrime(Crime crime);
}
