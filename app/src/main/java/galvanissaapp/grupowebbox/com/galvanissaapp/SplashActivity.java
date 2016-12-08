package galvanissaapp.grupowebbox.com.galvanissaapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

public class SplashActivity extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1500;
    Intent main=null;
    final int PERMISSION_REQUEST_CODE=10;
    final int PERMISSION_REQUEST_CODEI=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);




        this.startAct();


    }



    public void startAct(){

            new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    // Start your app main activity




                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
                    String logged = preferences.getString("Logged", "0");

                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    main = new Intent(SplashActivity.this, MainActivity.class);

                    if(logged.equals("1")){
                        startActivity(main);


                    }
                    else{
                        startActivity(i);
                    }

                    // close this activity
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }


}