package com.example.mramir.ticketDispenser;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Turn.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TurnDao userDao();
}
