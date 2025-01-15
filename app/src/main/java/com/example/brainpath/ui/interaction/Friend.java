package com.example.brainpath.ui.interaction;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Friend implements Parcelable {
    private String username;
    private String userId;
    private String profile;
    private String lastMessage;
    private String lastSeen;
    private Date lastMessageTimestamp; // Raw timestamp for sorting

    public Friend(String username, String userId, String profile, String lastMessage, String lastSeen, Date lastMessageTimestamp) {
        this.username = username;
        this.userId = userId;
        this.profile = profile;
        this.lastMessage = lastMessage;
        this.lastSeen = lastSeen;
        this.lastMessageTimestamp = lastMessageTimestamp; // Store the raw timestamp
    }

    public Date getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(Date lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public String getUsername() {
        return username;
    }

    public String getUserId() {
        return userId;
    }

    public String getProfile() {
        return profile;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(userId);
        dest.writeString(profile);
        dest.writeString(lastMessage);
        dest.writeString(lastSeen);
        dest.writeLong(lastMessageTimestamp != null ? lastMessageTimestamp.getTime() : 0); // Write the timestamp to parcel
    }

    public static final Creator<Friend> CREATOR = new Creator<Friend>() {
        @Override
        public Friend createFromParcel(Parcel in) {
            Friend friend = new Friend(
                    in.readString(),  // username
                    in.readString(),  // userId
                    in.readString(),  // profile
                    in.readString(),  // lastMessage
                    in.readString(),  // lastSeen
                    new Date(in.readLong())  // lastMessageTimestamp (raw Date)
            );
            return friend;
        }

        @Override
        public Friend[] newArray(int size) {
            return new Friend[size];
        }
    };
}
