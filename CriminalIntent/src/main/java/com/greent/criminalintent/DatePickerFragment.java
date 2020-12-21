package com.greent.criminalintent;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment {
    private static final String ARG_DATE = "date";


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Date resultDate = new GregorianCalendar(year, month, dayOfMonth).getTime();

                 @NonNull Callbacks frag = (Callbacks) getTargetFragment();
                 frag.onDateSelected(resultDate);




            }
        };



        Date date = (Date) getArguments().getSerializable(ARG_DATE);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int initialYear = cal.get(Calendar.YEAR);
        int initialMonth = cal.get(Calendar.MONTH); // starts with 0 (Jan)
        int initialDay = cal.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(requireContext(),
                listener,
                initialYear,
                initialMonth,
                initialDay);

    }

    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        DatePickerFragment instance = new DatePickerFragment();
        instance.setArguments(args);
        return instance;
    }


    public interface Callbacks {
        void onDateSelected(Date date);
    }
}
