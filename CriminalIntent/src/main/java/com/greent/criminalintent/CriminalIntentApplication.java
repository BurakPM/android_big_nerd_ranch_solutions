package com.greent.criminalintent;

import android.app.Application;

public class CriminalIntentApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrimeRepository.init(this);
    }
}
