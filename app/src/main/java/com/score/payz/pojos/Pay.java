package com.score.payz.pojos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * POJO to keep pay attributes
 *
 * @author eranga bandara (erangaeb@gmail.com)
 */
public class Pay implements Parcelable {
    String account;
    String amount;
    String time;

    public Pay(String account, String amount, String time) {
        this.account = account;
        this.amount = amount;
        this.time = time;
    }

    protected Pay(Parcel in) {
        account = in.readString();
        amount = in.readString();
        time = in.readString();
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public static final Creator<Pay> CREATOR = new Creator<Pay>() {
        @Override
        public Pay createFromParcel(Parcel in) {
            return new Pay(in);
        }

        @Override
        public Pay[] newArray(int size) {
            return new Pay[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(account);
        dest.writeString(amount);
        dest.writeString(time);
    }
}
