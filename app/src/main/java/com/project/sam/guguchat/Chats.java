package com.project.sam.guguchat;

/**
 * Created by A.Richard on 09/10/2017.
 */

public class Chats
{
    private String status ;//check firebase later

    public Chats(String user_status) {
        this.status = user_status;
    }

    public  Chats(){


    }

    public String getUser_status() {
        return status;
    }

    public void setUser_status(String user_status) {
        this.status = user_status;
    }
}
