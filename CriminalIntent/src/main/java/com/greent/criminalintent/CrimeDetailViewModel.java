package com.greent.criminalintent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.util.UUID;

public class CrimeDetailViewModel extends ViewModel {
    private CrimeRepository cr = CrimeRepository.get();
    private MutableLiveData<UUID> crimeIdLiveData = new MutableLiveData<>();


    LiveData<Crime> crimeLiveData = Transformations.switchMap(crimeIdLiveData,
            crimeId -> cr.getCrime(crimeId));

    void loadCrime(UUID crimeId) {
        crimeIdLiveData.setValue(crimeId); // on main thread
    }

    void saveCrime(Crime crime) {
        cr.updateCrime(crime);
    }

    File getPhotoFile(Crime crime) {
        return cr.getPhotoFile(crime);
    }

    void deleteCrime(Crime crime) {
        cr.deleteCrime(crime);
    }
}


