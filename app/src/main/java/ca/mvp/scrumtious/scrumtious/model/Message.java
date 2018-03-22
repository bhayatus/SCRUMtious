package ca.mvp.scrumtious.scrumtious.model;

public class Message {
    private String messageText, senderEmail;
    private long timeStamp;


    public Message(){

    }

    public String getMessageText() {return messageText;}
    public String getSenderEmail() {return senderEmail;}
    public long getTimeStamp() {return timeStamp;}

}
