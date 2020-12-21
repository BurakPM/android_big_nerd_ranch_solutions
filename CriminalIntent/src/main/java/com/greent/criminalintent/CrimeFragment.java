package com.greent.criminalintent;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class CrimeFragment extends Fragment implements DatePickerFragment.Callbacks {
    private Crime mCrime;
    private EditText mTitleField;
    private String titleWatcher;
    private Button mDateButton;
    private CheckBox mCheckBox;
    private Button mSendCrimeButton;
    private Button mSuspectButton;
    private Button mCallSuspectButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private String mContactId;
    private File mPhotoFile;
    private Uri mPhotoUri;
    private CrimeDetailViewModel cdvm;
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static final String DATE_FORMAT = "EEE, MMM, dd";
    private static final int REQUEST_CONTACT = 1;
    private static final String[] CONTACTS_PERMISSIONS = new String[]{Manifest.permission.READ_CONTACTS};
    private static final int REQUEST_CONTACT_PERMISSIONS = 666;
    private static final int REQUEST_PHOTO = 2;
    private static final String DIALOG_PHOTO = "DialogPhoto";

    private int mPhotoWidth;
    private int mPhotoHeight;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        cdvm = new ViewModelProvider(this).get(CrimeDetailViewModel.class);
        mCrime = new Crime();
        UUID crimeID = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        cdvm.loadCrime(crimeID);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = v.findViewById(R.id.crime_title);
        mDateButton = v.findViewById(R.id.crime_date);
        mCheckBox = v.findViewById(R.id.crime_solved);
        mSendCrimeButton = v.findViewById(R.id.crime_report);
        mSuspectButton = v.findViewById(R.id.crime_suspect);
        mCallSuspectButton = v.findViewById(R.id.call_suspect);
        mPhotoButton = v.findViewById(R.id.crime_camera);
        mPhotoView = v.findViewById(R.id.crime_photo);


        return v;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cdvm.crimeLiveData.observe(getViewLifecycleOwner(), new Observer<Crime>() {


            @Override
            public void onChanged(Crime crime) {
                if (crime != null) {
                    mCrime = crime;
                    mPhotoFile = cdvm.getPhotoFile(crime);
                    mPhotoUri = FileProvider.getUriForFile(getActivity(),
                            "com.greent.criminalintent.fileprovider",
                            mPhotoFile);
                    updateUI();
                } else {
                    System.out.println("mCrime is null");
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                titleWatcher = s.toString();
                mCrime.setTitle(titleWatcher);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }


        });
        mTitleField.setText(titleWatcher);

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment dpf = DatePickerFragment.newInstance(mCrime.getDate());
                dpf.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dpf.show(CrimeFragment.this.requireActivity().getSupportFragmentManager(), DIALOG_DATE);
            }
        });


        mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> mCrime.setSolved(isChecked));

        mSendCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                sendIntent.setType("text/plain");
                Intent chooserIntent = Intent.createChooser(sendIntent, getString(R.string.send_report));

                if (chooserIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(chooserIntent);
                } else {
                    mSendCrimeButton.setEnabled(false);
                }
            }
        });

        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickContactIntent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.Contacts.CONTENT_URI);


                PackageManager pm = getActivity().getPackageManager();
                ResolveInfo resolvedActivity = pm.resolveActivity(pickContactIntent,
                        PackageManager.MATCH_DEFAULT_ONLY);

                if (resolvedActivity == null) {
                    mSuspectButton.setEnabled(false);
                } else {
                    startActivityForResult(pickContactIntent, REQUEST_CONTACT);
                }

            }


        });

        mCallSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasContactPermission()) {
                    Intent callContact = new Intent(Intent.ACTION_DIAL);
                    Uri number = Uri.parse("tel:" + mCrime.getSuspectPhoneNumber());
                    callContact.setData(number);
                    startActivity(callContact);
                } else {
                    Snackbar sb = Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.permission_info
                            , Snackbar.LENGTH_LONG);
                    sb.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
                    sb.show();
                }
            }
        });

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = requireActivity().getPackageManager();
                Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                ResolveInfo resolvedActivity = pm.resolveActivity(captureImage, PackageManager.MATCH_DEFAULT_ONLY);
                if(resolvedActivity == null) {
                    mPhotoButton.setEnabled(false);
                }

                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
                List<ResolveInfo> cameraActivities = pm.queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);

                for(ResolveInfo cameraActivity: cameraActivities) {
                    requireActivity().grantUriPermission(cameraActivity.activityInfo.packageName,
                            mPhotoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(captureImage, REQUEST_PHOTO);
            }

        });

        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZoomedPhotoFragment zpf = ZoomedPhotoFragment.newInstance(mPhotoFile);
                zpf.show(getFragmentManager(), DIALOG_PHOTO);


            }
        });

        ViewTreeObserver vto = mPhotoView.getViewTreeObserver();
        if(vto.isAlive()) {
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mPhotoView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                     mPhotoWidth = mPhotoView.getWidth();
                     mPhotoHeight = mPhotoView.getHeight();

                    updatePhotoView();
                }
            });
        }
    }

    private void updateUI() {
        mTitleField.setText(mCrime.getTitle());
        mDateButton.setText(mCrime.getDate().toString());
        skipCheckBoxAnimation();

        if (mCrime.getSuspect().trim().length() > 0) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        updatePhotoView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CONTACT && (data != null)) {
            String suspect = getSuspectName(data);
            mCrime.setSuspect(suspect);
            cdvm.saveCrime(mCrime);
            mSuspectButton.setText(suspect);


            //Get suspect's mobile number.
            if (hasContactPermission()) {
                updateSuspectPhone();
            } else {
                // This will call onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
                requestPermissions(CONTACTS_PERMISSIONS, REQUEST_CONTACT_PERMISSIONS);
            }

        }

        if(requestCode == REQUEST_PHOTO) {
            requireActivity().revokeUriPermission(mPhotoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            updatePhotoView();
        }



    }

    private String getCrimeReport() {
        String solvedString = "";
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        android.text.format.DateFormat df = new android.text.format.DateFormat();
        String dateString = df.format(DATE_FORMAT, mCrime.getDate()).toString();

        String suspectString = "";
        String suspectName = mCrime.getSuspect();
        if (suspectName.trim().length() == 0) {
            suspectString = getString(R.string.crime_report_no_suspect);
        } else {
            suspectString = getString(R.string.crime_report_suspect, mCrime.getSuspect());
        }
        return getString(R.string.crime_report, mCrime.getTitle(),
                dateString, solvedString, suspectString);
    }

    private void updatePhotoView() {
        if(mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        }else{
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), mPhotoWidth, mPhotoHeight);
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        CrimeFragment newCf = new CrimeFragment();
        newCf.setArguments(args);
        return newCf;
    }

    public void skipCheckBoxAnimation() {
        mCheckBox.setChecked(mCrime.isSolved());
        mCheckBox.jumpDrawablesToCurrentState();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        requireActivity().revokeUriPermission(mPhotoUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    }

    @Override
    public void onStop() {
        super.onStop();
        cdvm.saveCrime(mCrime);
    }

    @Override
    public void onDateSelected(Date date) {
        mCrime.setDate(date);
        updateUI();
    }

    private String getSuspectName(Intent data) {
        Uri contactUri = data.getData();

        String[] queryFields = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
        };

        Cursor c = getActivity().getContentResolver().query(
                contactUri, queryFields, null, null, null
        );

        try {
            if (c.getCount() == 0) {
                return null;
            }
            // move cursor to the first row
            c.moveToFirst();

            mContactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
            String suspectName = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            return suspectName;

        } finally {
            c.close();
        }


    }

    private String getSuspectNumber(String contactId) {
        String suspectPhoneNumber = null;
        // The content URI of the CommonDataKinds.Phone
        Uri phoneContactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        //The columns to return for each row
        String[] queryFields = new String[]{
                ContactsContract.Contacts.Data._ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER, // default phone number
                ContactsContract.CommonDataKinds.Phone.TYPE,
        };

        // Selection criteria
        String mSelectionClause = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";

        String[] mSelectionArgs = {""};
        mSelectionArgs[0] = contactId;

        Cursor c = getActivity().getContentResolver().query(phoneContactUri,
                queryFields, mSelectionClause,
                mSelectionArgs, null);

        try {
            if (c.getCount() == 0) {
                return null;
            }

            while (c.moveToNext()) {
                int phoneType = c.getInt(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                if (phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
                    suspectPhoneNumber = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    break;
                }

            }


        } finally {
            c.close();
        }
        return suspectPhoneNumber;
    }

    private void updateSuspectPhone() {
        String suspectPhoneNo = getSuspectNumber(mContactId);
        mCrime.setSuspectPhoneNumber(suspectPhoneNo);
        cdvm.saveCrime(mCrime);
    }

    private boolean hasContactPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), CONTACTS_PERMISSIONS[0]);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CONTACT_PERMISSIONS:
                if (hasContactPermission()) {
                    updateSuspectPhone();
                }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_crime_delete, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_crime:
                cdvm.deleteCrime(mCrime);
                getActivity().getSupportFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}


