package utsav.childsecurity;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
