package com.greent.criminalintent;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;


public class ZoomedPhotoFragment extends DialogFragment {
    private static final String ARG_PHOTO_FILE = "photoFile";

    private ImageView zoomedCrimeImageView;
    private File mPhotoFile;

    public static ZoomedPhotoFragment newInstance(File photoFile) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PHOTO_FILE, photoFile);

        ZoomedPhotoFragment zpf = new ZoomedPhotoFragment();
        zpf.setArguments(args);
        return zpf;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mPhotoFile = (File) getArguments().getSerializable(ARG_PHOTO_FILE);

        View v = inflater.inflate(R.layout.fragment_crime_photo, container, false);

        zoomedCrimeImageView = v.findViewById(R.id.crime_photo_zoomed);

        if(mPhotoFile == null || !mPhotoFile.exists()) {
            zoomedCrimeImageView.setImageDrawable(null);
        }else{
            Bitmap bmp = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            zoomedCrimeImageView.setImageBitmap(bmp);
        }


        return v;

    }
}
