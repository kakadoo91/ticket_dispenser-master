package com.example.mramir.ticketDispenser;

import android.arch.persistence.room.*;

import java.util.*;

@Dao
public interface TurnDao {

    @Insert
    void insertTurn(Turn turn);

    @Query("SELECT * FROM turn")
    List<Turn> getAll();


    @Update
    void updateTurn(Turn turn);


    @Delete
    void delete(Turn turn);
}
