package com.greent.criminalintent.database;

import androidx.room.TypeConverter;

import java.util.Date;
import java.util.UUID;

public class CrimeTypeConverters {

    @TypeConverter
    public static Long fromDate(Date date) {
        return date == null ? null : date.getTime();

    }

    @TypeConverter
    public static Date toDate(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static String fromUUID(UUID uuid) {
        return uuid == null ? null : uuid.toString();
    }

    @TypeConverter
    public static UUID toUUID(String value) {
        return value == null ? null : UUID.fromString(value);
    }
}
