package com.example.learing_platform.objects;

import android.app.Application;
import android.util.Log;

public class Userinfo extends Application{
    private User user;
    public User getUser(){
        return this.user;
    }
    public void setUser(User user){
        this.user= user;
        //Log.d("User",user.getId()+":"+user.getNickname()+":"+user.getIntroduction());
    }
    @Override
    public void onCreate(){
        user = new User();
        super.onCreate();

    }

}
