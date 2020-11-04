package com.greent.criminalintent;

import androidx.lifecycle.ViewModel;

import java.util.List;

import io.reactivex.Flowable;

public class CrimeListViewModel extends ViewModel {

    private CrimeRepository mCrimeRepository = CrimeRepository.get();



    Flowable<Integer> getCrimeCountLiveData() {
        return mCrimeRepository.getCount();
    }


    Flowable<List<Crime>> getCrimeListLiveData() {
        return mCrimeRepository.getCrimes();
    }

    void addCrime(Crime crime) {
        mCrimeRepository.addCrime(crime);
    }

}
