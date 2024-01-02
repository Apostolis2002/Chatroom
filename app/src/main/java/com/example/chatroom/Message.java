package com.example.chatroom;

public class Message {
    private String SenderNickname;
    private String messageText;
    private String messageTime;

    public Message(String senderNickname, String messageText, String messageTime) {
        SenderNickname = senderNickname;
        this.messageText = messageText;
        this.messageTime = messageTime;
    }

    public String getSenderNickname() {
        return SenderNickname;
    }

    public void setSenderNickname(String senderNickname) {
        SenderNickname = senderNickname;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }
}
