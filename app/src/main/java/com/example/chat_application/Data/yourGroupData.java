package com.example.chat_application.Data;

import java.io.Serializable;

public class yourGroupData implements Serializable {
    private String groupId, groupName, groupImage, msgCountUser,msgCount ,lastmsgUserName, lastMessage, lastmsgTime;

    public yourGroupData() {
    }


    public yourGroupData(String msgCountUser, String lastmsgUserName, String lastMessage, String lastmsgTime) {
        this.msgCountUser = msgCountUser;
        this.lastmsgUserName = lastmsgUserName;
        this.lastMessage = lastMessage;
        this.lastmsgTime = lastmsgTime;
    }

    public String getMsgCountUser() {
        return msgCountUser;
    }

    public void setMsgCountUser(String msgCountUser) {
        this.msgCountUser = msgCountUser;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(String groupImage) {
        this.groupImage = groupImage;
    }

    public String getLastmsgUserName() {
        return lastmsgUserName;
    }

    public void setLastmsgUserName(String lastmsgUserName) {
        this.lastmsgUserName = lastmsgUserName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastmsgTime() {
        return lastmsgTime;
    }

    public void setLastmsgTime(String lastmsgTime) {
        this.lastmsgTime = lastmsgTime;
    }

    public String getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(String msgCount) {
        this.msgCount = msgCount;
    }
}
