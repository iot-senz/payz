package com.score.payz.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.score.payz.pojos.Summary;
import com.score.payz.pojos.Pay;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by root on 11/19/15.
 */
public class SenzorsDbSource {
    private static final String TAG = SenzorsDbSource.class.getName();
    private static Context context;

    public SenzorsDbSource(Context context) {
        Log.d(TAG, "Init: db source");
        this.context = context;
    }

    public void createPay(Pay pay) {
        SQLiteDatabase db = SenzorsDbHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SenzorsDbContract.Pay.COLUMN_NAME_shopNo, pay.getShopNo());
        values.put(SenzorsDbContract.Pay.COLUMN_NAME_shopName, pay.getShopName());
        values.put(SenzorsDbContract.Pay.COLUMN_NAME_invoiceNumber, pay.getInvoiceNumber());
        values.put(SenzorsDbContract.Pay.COLUMN_NAME_payAmount, pay.getPayAmount());
        values.put(SenzorsDbContract.Pay.COLUMN_NAME_payTime, pay.getPayTime());

        long id = db.insert(SenzorsDbContract.Pay.TABLE_NAME, null, values);
        db.close();

    }

    public ArrayList<Pay> getAllPays() {
        ArrayList<Pay> sensorList = new ArrayList();

        SQLiteDatabase db = SenzorsDbHelper.getInstance(context).getReadableDatabase();

        // join query to retrieve data
        String query = "SELECT * " +
                "FROM " + SenzorsDbContract.Pay.TABLE_NAME;

        Cursor cursor = db.rawQuery(query, null);
        // sensor/user attributes
        int id;
        String shopName;
        String shopNo;
        String invoiceNumber;
        double payAmount;
        String payTime;
        Log.e(TAG, cursor.getCount() + "f");
        // extract attributes
        while (cursor.moveToNext()) {
            HashMap<String, String> senzAttributes = new HashMap<>();

            // get senz attributes

            id = cursor.getInt(cursor.getColumnIndex(SenzorsDbContract.Pay._ID));

            shopName = cursor.getString(cursor.getColumnIndex(SenzorsDbContract.Pay.COLUMN_NAME_shopName));

            shopNo = cursor.getString(cursor.getColumnIndex(SenzorsDbContract.Pay.COLUMN_NAME_shopNo));

            invoiceNumber = cursor.getString(cursor.getColumnIndex(SenzorsDbContract.Pay.COLUMN_NAME_invoiceNumber));

            payAmount = cursor.getInt(cursor.getColumnIndex(SenzorsDbContract.Pay.COLUMN_NAME_payAmount));

            payTime = cursor.getString(cursor.getColumnIndex(SenzorsDbContract.Pay.COLUMN_NAME_payTime));

            System.out.println(id + " " + shopName + " " + shopNo + " " + invoiceNumber + " " + payAmount + " " + payTime);
            Pay pay = new Pay(id, shopName, shopNo, invoiceNumber, payAmount, payTime);
            //senzAttributes.put(_senzName, _senzValue);

            Log.d(TAG,cursor.getColumnIndex(SenzorsDbContract.Pay._ID)+"");
            Log.d(TAG,cursor.getColumnIndex(SenzorsDbContract.Pay.COLUMN_NAME_shopName)+"");
            Log.d(TAG,cursor.getColumnIndex(SenzorsDbContract.Pay.COLUMN_NAME_shopNo)+"");
            Log.d(TAG,cursor.getColumnIndex(SenzorsDbContract.Pay.COLUMN_NAME_invoiceNumber)+"");
            Log.d(TAG,cursor.getColumnIndex(SenzorsDbContract.Pay.COLUMN_NAME_payAmount)+"");
            Log.d(TAG,cursor.getColumnIndex(SenzorsDbContract.Pay.COLUMN_NAME_payTime)+"");

            // fill senz list
            System.out.println("Done in Create object");
            sensorList.add(pay);
        }

        // clean
        cursor.close();
        db.close();


        return sensorList;

    }

    public void clearTable() {
        SQLiteDatabase db = SenzorsDbHelper.getInstance(context).getWritableDatabase();

        // delete senz of given user

        db.close();
    }


    public Summary getSummeryAmmount(){
        SQLiteDatabase db = SenzorsDbHelper.getInstance(context).getWritableDatabase();
        String query = "SELECT COUNT("+SenzorsDbContract.Pay._ID+") AS trcount, SUM("+SenzorsDbContract.Pay.COLUMN_NAME_payAmount+") AS total" +
                " FROM " +SenzorsDbContract.Pay.TABLE_NAME;
        Log.e(TAG,query);
        Cursor cursor = db.rawQuery(query, null);
        int tcount;
        int tamount;
        if(cursor.moveToFirst()){
            tcount = cursor.getInt(cursor.getColumnIndex("trcount"));

            tamount = cursor.getInt(cursor.getColumnIndex("total"));
        }
        else{
            tcount = 0;

            tamount = 0;
        }

        Calendar cp = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(cp.getTime());

        Summary tempsum=new Summary("10255",""+tcount,""+tamount,formattedDate);



        return tempsum;
    }
}
