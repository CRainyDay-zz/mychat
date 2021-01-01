package com.crainyday.mychat.entity;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Message  implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean isMine = false;

    private String sender;
    private String senderId;
    private String recordGuid;
    private String contents;
    private String contentsType;
    private String sendTime;

    private Bitmap bitmap;

    public Message(boolean isMine, String sender, String senderId, String recordGuid, String contents, String contentsType, String sendTime, Bitmap bitmap) {
        this.isMine = isMine;
        this.sender = sender;
        this.senderId = senderId;
        this.recordGuid = recordGuid;
        this.contents = contents;
        this.contentsType = contentsType;
        this.sendTime = sendTime;
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getRecordGuid() {
        return recordGuid;
    }

    public void setRecordGuid(String recordGuid) {
        this.recordGuid = recordGuid;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getContentsType() {
        return contentsType;
    }

    public void setContentsType(String contentsType) {
        this.contentsType = contentsType;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender='" + sender + '\'' +
                ", senderId='" + senderId + '\'' +
                ", recordGuid='" + recordGuid + '\'' +
                ", contents='" + contents + '\'' +
                ", contentsType='" + contentsType + '\'' +
                ", sendTime='" + sendTime + '\'' +
                '}';
    }
}
