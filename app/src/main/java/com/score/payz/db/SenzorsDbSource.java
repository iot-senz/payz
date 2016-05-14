package com.score.payz.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.score.payz.pojos.Pay;

import java.util.ArrayList;
import java.util.List;

/**
 * Do all database insertions, updated, deletions from here
 *
 * @author erangaeb@gmail.com (eranga herath)
 */
public class SenzorsDbSource {
    private static final String TAG = SenzorsDbSource.class.getName();
    private static Context context;

    public SenzorsDbSource(Context context) {
        Log.i(TAG, "Init: db source");
        this.context = context;
    }

    public void createPayz(Pay pay) {
        Log.i(TAG, "Create payz with account: " + pay.getAccount() + " amount: " + pay.getAmount());

        SQLiteDatabase db = SenzorsDbHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SenzorsDbContract.Pay.COLUMN_NAME_ACCOUNT, pay.getAccount());
        values.put(SenzorsDbContract.Pay.COLUMN_NAME_AMOUNT, pay.getAmount());
        values.put(SenzorsDbContract.Pay.COLUMN_NAME_TIME, pay.getTime());

        db.insert(SenzorsDbContract.Pay.TABLE_NAME, null, values);
        db.close();
    }

    public List<Pay> readAllPayz() {
        Log.i(TAG, "Read payz");

        List<Pay> payzList = new ArrayList<Pay>();

        SQLiteDatabase db = SenzorsDbHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query(SenzorsDbContract.Pay.TABLE_NAME, null, null, null, null, null, null);

        // user attributes
        String _account;
        String _amount;
        String _time;

        // extract attributes
        while (cursor.moveToNext()) {
            _account = cursor.getString(cursor.getColumnIndex(SenzorsDbContract.Pay.COLUMN_NAME_ACCOUNT));
            _amount = cursor.getString(cursor.getColumnIndex(SenzorsDbContract.Pay.COLUMN_NAME_AMOUNT));
            _time = cursor.getString(cursor.getColumnIndex(SenzorsDbContract.Pay.COLUMN_NAME_TIME));

            payzList.add(new Pay(_account, _amount, _time));
        }

        // clean
        cursor.close();
        db.close();

        Log.d(TAG, "payz count " + payzList.size());

        return payzList;
    }

}
