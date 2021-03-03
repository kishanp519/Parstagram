package com.example.parstagram;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Post.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("eMqZoGOKixRPB5JnekRr1xDGF29rgETnJ5oclq88")
                .clientKey("JPdKzksteaKEprN0SeoiRy3UyhHfDe4wBF75pX4S")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
