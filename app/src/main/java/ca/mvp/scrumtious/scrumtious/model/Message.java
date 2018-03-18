package ca.mvp.scrumtious.scrumtious.model;

/**
 * Created by Nensi on 2018-03-18.
 */

public class Message {
    private String messageText, senderEmail;
    private long timeStamp;


    public Message(){

    }

    public String getMessageText() {return messageText;}
    public String getSenderEmail() {return senderEmail;}
    public long getTimeStamp() {return timeStamp;}

}
