package com.example.mrchatbot.model;

import com.google.firebase.Timestamp;
import java.util.List;

public class ChatroomModel {
    String chatroomId;
    String lastMessage;
    List<String> userIDs;
    Timestamp lastMessageTimestamp;
    String lastMessageSenderID;
    public ChatroomModel(){
    }

    public ChatroomModel(String chatroomId, List<String> userIDs, Timestamp lastMessageTimestamp, String lastMessageSenderID) {
        this.chatroomId = chatroomId;
        this.userIDs = userIDs;
        this.lastMessageSenderID = lastMessageSenderID;
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(String chatroomID) {
        this.chatroomId = chatroomID;
    }

    public List<String> getUserIDs() {
        return userIDs;
    }

    public void setUserIDs(List<String> userIDs) {
        this.userIDs = userIDs;
    }

    public Timestamp getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(Timestamp lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public String getLastMessageSenderID() {
        return lastMessageSenderID;
    }

    public void setLastMessageSenderID(String lastMessageSenderID) {
        this.lastMessageSenderID = lastMessageSenderID;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
