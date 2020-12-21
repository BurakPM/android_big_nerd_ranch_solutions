package com.greent.criminalintent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity
public class Crime {



    @NonNull
    @PrimaryKey
    private UUID mId;

    private String mTitle = "";
    private Date mDate;
    private boolean isSolved = false;
    @NonNull private String mSuspect = "";
    private String mSuspectPhoneNumber;



    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public Crime() {
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return isSolved;
    }

    public void setSolved(boolean solved) {
        isSolved = solved;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public String getSuspectPhoneNumber() { return mSuspectPhoneNumber; }

    public void setSuspectPhoneNumber(String suspectPhoneNumber) {
        mSuspectPhoneNumber = suspectPhoneNumber;
    }

    public String getPhotoFileName() {
        return "IMG_" + getId().toString() + ".jpg";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Crime crime = (Crime) o;
        return isSolved == crime.isSolved &&
                mId.equals(crime.mId) &&
                mTitle.equals(crime.mTitle) &&
                mDate.equals(crime.mDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mId, mTitle, mDate, isSolved);
    }



    public static final DiffUtil.ItemCallback<Crime> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Crime>() {
                @Override
                public boolean areItemsTheSame(@NonNull Crime oldItem, @NonNull Crime newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Crime oldItem, @NonNull Crime newItem) {
                    return oldItem.equals(newItem);
                }
            };
}
