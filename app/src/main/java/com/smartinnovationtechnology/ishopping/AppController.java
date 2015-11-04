package com.smartinnovationtechnology.ishopping;

import android.app.Application;
import android.content.Context;

/**
 * Created by Shamyyoun on 3/15/2015.
 */
public class AppController extends Application {
    public static final String END_POINT = "http://www.egypt-media.com/iteam/ishopping/public";

    public AppController() {
        super();
    }

    /**
     * overridden method
     */
    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * method used to return current application instance
     */
    public static AppController getInstance(Context context) {
        return (AppController) context.getApplicationContext();
    }
}
