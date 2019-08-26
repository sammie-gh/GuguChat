package com.project.sam.guguchat;

/**
 * Created by Sammie on 6/24/2017.
 */

public class Users {

    public  String name;
    public  String image;
    public  String status;
    public  String thumb_image;

    public Users (){

    }



    public Users(String name, String image, String status) {
        this.name = name;
        this.image = image;
        this.status = status;
        this.thumb_image = thumb_image;


    }

    public String getThumb_image() {
        return thumb_image;
    }

    public Users(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
