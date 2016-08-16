package com.example.sujit.myapplication;

/**
 * Created by sujit on 8/12/16.
 */
public class Config {
    public static final String LOGIN_URL = "http://192.168.94.1/Android/LoginLogout/login.php";

    //Keys for email and password as defined in our $_POST['key'] in login.php
    public static final String KEY_PHONENO = "phone";
    public static final String KEY_PASSWORD = "password";

    //If server response is equal to this that means login is successful
    public static final String LOGIN_SUCCESS = "success";

    //Keys for Sharedpreferences
    //This would be the name of our shared preferences
    public static final String SHARED_PREF_NAME = "myloginapp";

    //This would be used to store the email of current logged in user
    public static final String PHONE_SHARED_PREF = "phone";

    //We will use this to store the boolean in sharedpreference to track user is loggedin or not
    public static final String LOGGEDIN_SHARED_PREF = "loggedin";

    public final String code = "";

    public final String getmeacode="http://"+code+".ngrok.io/getmeacode";
    public final String registeracustomer="http://"+code+".ngrok.io/registeracustomer";

}

