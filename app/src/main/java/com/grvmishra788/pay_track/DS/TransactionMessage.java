package com.grvmishra788.pay_track.DS;

import java.util.Date;
import java.util.UUID;

import androidx.annotation.NonNull;

public class TransactionMessage {
    private UUID id;
    private String src;
    private String body;
    private Date date;

    public TransactionMessage(UUID id, String src, String body, Date date) {
        this.id = id;
        this.src = src;
        this.body = body;
        this.date = date;
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @NonNull
    @Override
    public String toString() {
        return "Transaction Message -" +
                " date : " + date.toString() +
                " src : " + src +
                " body : " + body;
    }
}
