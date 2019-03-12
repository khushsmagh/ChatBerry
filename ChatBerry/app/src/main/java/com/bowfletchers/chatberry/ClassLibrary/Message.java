package com.bowfletchers.chatberry.ClassLibrary;

import java.util.Date;

public class Message {

    private Member member;
    private String message;
    private Date messageDate;


    public Message (Member member, String message, Date messageDate) {
        this.member = member;
        this.message = message;
        this.messageDate = messageDate;
    }


    public String getMessage() {
        return message;
    }

    public Date getMessageDate() {return messageDate;}



    public Member getMember() {
        return member;
    }


}
