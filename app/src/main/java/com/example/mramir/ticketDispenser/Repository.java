package com.example.mramir.ticketDispenser;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.*;

public class Repository {
    private AppDatabase db;

    public Repository(Context context) {
        db = Room.databaseBuilder(context, AppDatabase.class, "my_database").build();
    }

    private void insertTurnSync(final Turn t) {
        db.userDao().insertTurn(t);
    }
    private void insertTurnAsync(final Turn t) {
        new AsyncTask<Turn, Void, Void>(){
            @Override
            protected Void doInBackground(Turn... turns) {
                db.userDao().insertTurn(t);
                return null;
            }
        }.execute();
    }

    private List<Turn> getAllSync() {

         return db.userDao().getAll();
    }

    public List<Turn> getAllAsyc(){
        try {
            return new AsyncTask<Void, Void, List<Turn>>() {
                @Override
                protected List<Turn> doInBackground(Void... voids) {
                    return getAllSync();
                }
            }.execute().get();
        } catch (Exception e) {
            Log.e("DB error -> " , e.toString());
            return null;
        }
    }
    /***
     *  this method is used to find the turn for today or return a new turn if no turn is found for today and if an error happens it will return null
     *
     */
    public Turn findTodaysTurn() {
        Turn toReturn = null;
        try {
            toReturn =  new AsyncTask<Void, Void, Turn>() {
                @Override
                protected Turn doInBackground(Void... voids) {
                    List<Turn> list = getAllSync();
                    if(!list.isEmpty()) {
                        Calendar today = Calendar.getInstance();
                        today.setTime(new Date());
                        for (Turn t : list) {
                            Calendar storedDate = Calendar.getInstance();
                            storedDate.setTime(new Date(t.getDate()));
                            if (t.equals(new Turn()))
                                Log.i("Turn for today", t.toString());
                            return t; // the turn's date is the same as today's date
                        }
                    }
                    return null;
                }
            }.execute().get();
        } catch (Exception e) {
            Log.e("DB error -> " , e.toString());
            return null;
        }
        if(toReturn == null){
            Turn todaysTurn = new Turn();
            Log.i("Turn for today" , todaysTurn.toString());
            insertTurnAsync(todaysTurn);
            toReturn = todaysTurn;
        }
        return toReturn;
    }

    public void updateTurn(final Turn t) {
        new AsyncTask<Turn, Void, Void>(){
            @Override
            protected Void doInBackground(Turn... turns) {
                db.userDao().updateTurn(t);
                return null;
            }
        }.execute();
    }

    /**
     * this method is designed to be called before the app closes so that all the previous turns are deleted and only the turn for today is stored in the database
     */
    public void cleanup() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                List<Turn> oldList = getAllSync();
                for (Turn t : oldList) {
                    if (!t.equals(new Turn())) db.userDao().delete(t);
                }
                return null;
            }
        }.execute();
    }

    /**
     * @param type one of the four types of turn namely PASSPORT,VISA,DANESHJOO and PROXY
     * @return the turn for the specific type of turn
     */
    public int getNewTurn(MainPageFa.TurnType type) {
        Turn todaysTurn = findTodaysTurn();
        int result = 0;
        switch (type) {
            case PASSPORT:
                result = todaysTurn.getPassportCounter();
                todaysTurn.incrementPass();
                break;
            case VISA:
                result = todaysTurn.getVisaCounter();
                todaysTurn.incrementVisa();
                break;
            case PROXY:
                result = todaysTurn.getProxyCounter();
                todaysTurn.incrementProxy();
                break;
            case DANESHJOO:
                result = todaysTurn.getDaneshjooCounter();
                todaysTurn.incrementDaneshjoo();
                break;
                default:
                    break;
        }
        updateTurn(todaysTurn);
        return result;
    }
}
