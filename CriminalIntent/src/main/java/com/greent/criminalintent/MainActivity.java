package com.greent.criminalintent;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.UUID;

public class MainActivity extends AppCompatActivity implements CrimeListFragment.Callbacks {

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentById(R.id.fragment_container);


        if(frag == null) {
            frag = CrimeListFragment.newInstance();
            fm.beginTransaction()
                    .add(R.id.fragment_container, frag)
                    .commit();
        }
    }

    public static Activity getInstance() {
        return new MainActivity();
    }

    @Override
    public void onCrimeSelected(UUID crimeId) {

        Fragment fragment = CrimeFragment.newInstance(crimeId);


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
  }


}
