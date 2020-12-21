package com.greent.criminalintent;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class CrimeListFragment extends Fragment {
    private RecyclerView mCrimeRecyclerView;
    private TextView tv;
    private static final String TAG = "CrimeListFragment";
    private CrimeListViewModel clvm;
    private CrimeAdapter mAdapter;
    private int itemCount;
    private Callbacks mCallbacks;
    private Snackbar infoSnackbar;
    private TextView mTextViewPlaceholder;


    public interface Callbacks {
        void onCrimeSelected(UUID crimeId);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context; //the hosting activity must implement Callbacks interface
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mCrimeRecyclerView = v.findViewById(R.id.crime_recycler_view);
        mTextViewPlaceholder = v.findViewById(R.id.text_view_placeholder);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mCrimeRecyclerView.setAdapter(new CrimeAdapter());

        return v;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        clvm = new ViewModelProvider(this).get(CrimeListViewModel.class);

        infoSnackbar =
                Snackbar.make(view,
                        R.string.info_message,
                        Snackbar.LENGTH_INDEFINITE);
        infoSnackbar.setAction(R.string.add_string, new addCrimeListener());

        clvm.getCrimeCountLiveData().subscribeOn(Schedulers.computation()).
                observeOn(AndroidSchedulers.mainThread()).subscribe(integer -> {
            itemCount = integer.intValue();

            if (itemCount == 0) {
                infoSnackbar.show();
            } else {
                if (infoSnackbar.isShown()) {
                    infoSnackbar.dismiss();
                }
            }


        }, e -> System.out.println("RoomWithRx: " + e.getMessage()));


        clvm.getCrimeListLiveData().subscribeOn(Schedulers.computation()).
                observeOn(AndroidSchedulers.mainThread()).subscribe(crimeList -> {
            updateUI(crimeList);


        }, e -> System.out.println("RoomWithRx: " + e.getMessage()));

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }


    public static Fragment newInstance() {
        return new CrimeListFragment();
    }

    private class CrimeHolder extends RecyclerView.ViewHolder {
        private Crime mCrime;
        private TextView titleTextView;
        private TextView dateTextView;
        private ImageView solvedImageView;


        public CrimeHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.crime_title);
            dateTextView = itemView.findViewById(R.id.crime_date);
            solvedImageView = itemView.findViewById(R.id.crime_solved);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallbacks.onCrimeSelected(mCrime.getId());
                }
            });
        }

        public void bind(Crime crime) {
            mCrime = crime;
            titleTextView.setText(mCrime.getTitle());
            dateTextView.setText(new SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault()).format(mCrime.getDate()));

            if (mCrime.isSolved()) {
                solvedImageView.setVisibility(View.VISIBLE);
            } else {
                solvedImageView.setVisibility(View.GONE);
            }

        }
    }

    private class CrimeAdapter extends androidx.recyclerview.widget.ListAdapter<Crime, CrimeHolder> {


        public CrimeAdapter() {
            super(Crime.DIFF_CALLBACK);
        }


        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.list_item_crime, parent, false);


            return new CrimeHolder(v);
        }


        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            holder.bind(getItem(position));
        }


    }

    private void updateUI(List<Crime> crimes) {
        if (crimes.isEmpty()) {
            mCrimeRecyclerView.setVisibility(View.GONE);
            mTextViewPlaceholder.setVisibility(View.VISIBLE);

        } else {
            mCrimeRecyclerView.setVisibility(View.VISIBLE);
            mTextViewPlaceholder.setVisibility(View.GONE);

            mAdapter = new CrimeAdapter();
            mAdapter.submitList(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case (R.id.new_crime):
                Crime crime = new Crime();
                clvm.addCrime(crime);
                mCallbacks.onCrimeSelected(crime.getId());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public class addCrimeListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Crime crime = new Crime();
            clvm.addCrime(crime);
            mCallbacks.onCrimeSelected(crime.getId());
        }
    }

}

