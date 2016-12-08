package galvanissaapp.grupowebbox.com.galvanissaapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.util.ArrayList;

public class TrackLocationService extends Service
{
    public static final String BROADCAST_ACTION = "Hello World";
    private static final int TWO_MINUTES = 1000 * 60 * 4;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null;

    Intent intent;
    int counter = 0;
    String android_id="";
    String myArticleUrl = "http://galvanissa.lmendoza.info/backend/webroot/Mobile/service.php";
    private sendDataToServer mAuthTask = null;
    String geohist_lat;
    String geohist_lng;
    String did;
    String geohist_provider;
    String geohist_user_id;
    String geohist_accuracy;
    String geohist_comment;
    String user_id=null;



    @Override
    public void onCreate()
    {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
        Log.d("SERVICIO","iniciado!");

         android_id = Secure.getString(this.getApplicationContext().getContentResolver(),
                Secure.ANDROID_ID);


    }

    @Override
    public void onStart(Intent intent, int startId)
    {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            listener = new MyLocationListener();
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, listener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);
            Log.d("SERVICIO", "iniciado start!");
        }
        catch(Exception s){
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            listener = new MyLocationListener();
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, listener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }



    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }



    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");
        locationManager.removeUpdates(listener);
    }

    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }




    public class MyLocationListener implements LocationListener
    {

        public void onLocationChanged(final Location loc)
        {
            SharedPreferences sp = null;
            sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            user_id=sp.getString("user_id","");

            Log.i("******", "Location changed"+String.valueOf(loc.getLatitude())+" | "+String.valueOf(loc.getLongitude())+" - did:"+android_id+"-provider"+loc.getProvider().toString() +String.valueOf(loc.getAccuracy()));
            if(isBetterLocation(loc, previousBestLocation)) {
                loc.getLatitude();
                loc.getLongitude();
                intent.putExtra("Latitude", loc.getLatitude());
                intent.putExtra("Longitude", loc.getLongitude());
                intent.putExtra("Provider", loc.getProvider());
                sendBroadcast(intent);

                 geohist_lat=String.valueOf(loc.getLatitude());
                 geohist_lng=String.valueOf(loc.getLongitude());
                 did=android_id;
                 geohist_provider=String.valueOf(loc.getProvider());
                 geohist_user_id=user_id;
                 geohist_accuracy=String.valueOf(loc.getAccuracy());
                 geohist_comment="AUTO";


                mAuthTask = new sendDataToServer("","");

                    mAuthTask.execute((Void) null);

            }
        }

        public void onProviderDisabled(String provider)
        {
            Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
        }


        public void onProviderEnabled(String provider)
        {
            Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }


        public void onStatusChanged(String provider, int status, Bundle extras)
        {

        }

    }


    public class sendDataToServer extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        sendDataToServer(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            Log.d("cuentaback",mEmail);

            String url_select = myArticleUrl;

            ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();


            param.add(new BasicNameValuePair("formid", "15"));
            param.add(new BasicNameValuePair("geohist_lat", geohist_lat));
            param.add(new BasicNameValuePair("geohist_lng", geohist_lng));
            param.add(new BasicNameValuePair("did", did));
            param.add(new BasicNameValuePair("geohist_provider", geohist_provider));
            param.add(new BasicNameValuePair("geohist_user_id", geohist_user_id));
            param.add(new BasicNameValuePair("geohist_accuracy", geohist_accuracy));
            param.add(new BasicNameValuePair("geohist_comment", geohist_comment));


            try {
                String resulting_json = null;
                ServiceHandler jsonParser = new ServiceHandler();
                resulting_json = jsonParser.makeServiceCall(url_select,
                        ServiceHandler.GET, param);

                Log.d("CUENTA",resulting_json.toString());
                JSONArray ja;
                ja = new JSONArray(resulting_json);


            } catch (Exception jds) {
            }

          return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;

        }
    }
}