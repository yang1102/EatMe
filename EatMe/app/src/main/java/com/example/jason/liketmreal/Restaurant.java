package com.example.jason.liketmreal;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.net.URL;

/**
 * Created by mattunion on 11/7/16.
 */

public class Restaurant implements Serializable{
    private String name = "";
    private String address = "";
    private URL url = null;
    private String phoneNumber = "";
    private int rating = -1;
    private int price = -1;

    private String pictureURL="";

    public Restaurant(String name, String address, URL url, String phoneNumber, int rating, String pictureURL){
        this.name = name;
        this.address = address;
        this.url = url;
        this.phoneNumber = phoneNumber;
        this.rating = rating;
        this.pictureURL = pictureURL;
    }

    public Restaurant(String name){
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture(){return pictureURL; }

    public void setPicture(String picture) {this.pictureURL = picture; }

}

