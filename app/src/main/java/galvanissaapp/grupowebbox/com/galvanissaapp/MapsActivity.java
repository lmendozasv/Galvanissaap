package galvanissaapp.grupowebbox.com.galvanissaapp;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LoaderCallbacks<Cursor>,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    final int PERMISSION_REQUEST_CODE=10;
    final int PERMISSION_REQUEST_CODEI=100;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private SQLiteAdapter mySQLiteAdapter;
    final Context context = this;
    String myArticleUrl = "http://galvanissa.lmendoza.info/backend/webroot/Mobile/service.php";
    private sendDataToServer mAuthTask = null;
    LatLng currentlt=null;
    String android_id=null;
    String user_id=null;
    String checkLat,checkLng=null;
    Boolean CanCheckin=true;
    String TAG ="Location ";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //start

        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkPermission())
            {
                // Code for above or equal 23 API Oriented Device
                // Create a common Method for both
            } else {
                requestPermission();
            }

            if (checkPermissionI())
            {
                // Code for above or equal 23 API Oriented Device
                // Create a common Method for both
            } else {
                requestPermissionI();
            }
        }
        else
        {

            // Code for Below 23 API Oriented Device
            // Create a common Method for both
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds



        //manual checkin
        Button button = (Button) findViewById(R.id.btncheckin);

        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {

if(CanCheckin){

                //checkin manual
                String lt,lng,man,datet;
                lt=String.valueOf(currentlt.latitude);
                lng=String.valueOf(currentlt.longitude);
                man="MANUAL";

                Calendar c = Calendar.getInstance();
                SimpleDateFormat df1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss a");
                datet = df1.format(c.getTime());


                mySQLiteAdapter = new SQLiteAdapter(MapsActivity.this);
                mySQLiteAdapter.openToWrite();
                mySQLiteAdapter.insertMyGeopoint(lt,lng,man,datet);
                checkLat=String.valueOf(lt);
                checkLng=String.valueOf(lng);

                mAuthTask = new sendDataToServer("","");

                mAuthTask.execute((Void) null);


}
else{
    Toast.makeText(MapsActivity.this, "Espera un momento...no hacer tantos check-in!", Toast.LENGTH_LONG).show();
}
                }


        });


        Button btnruta = (Button) findViewById(R.id.btnmiruta);

        btnruta.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View v) {

                //inicio
                PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);

                String strLat = "";
                String strLng = "";
                Float l=null;
                Float ln=null;
                SQLiteAdapter db = new SQLiteAdapter(getApplicationContext());
                Cursor cursor = null;
                cursor = db.getMyGeopoints();

                Log.d("CRASH REPORT",String.valueOf(cursor.getCount()));

                cursor.moveToFirst();
                for(int i=0;i<cursor.getCount();i++){


                    strLat = cursor.getString(cursor.getColumnIndex("geo_lat"));
                    strLng = cursor.getString(cursor.getColumnIndex("geo_lng"));

                    l= Float.parseFloat(strLat);
                    ln= Float.parseFloat(strLng);

                    LatLng location = new LatLng(l, ln);


                    options.add(location);

                    cursor.moveToNext();
                }

                cursor.close();
                mMap.addPolyline(options);

                Toast.makeText(MapsActivity.this, "Mostrando su ruta de hoy", Toast.LENGTH_LONG).show();
                //fin


            }});


    }
/*start map*/

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    private boolean checkPermissionI() {
        int result = ContextCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermissionI() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Toast.makeText(MapsActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODEI);
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(MapsActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }
    private void drawMarker(LatLng point,String NameMarker,String Snippet){
        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting latitude and longitude for the marker
        markerOptions.position(point).title(NameMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)).snippet(Snippet);

        // Adding marker on the Google Map
        mMap.addMarker(markerOptions);
    }
    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        currentlt=latLng;

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("Ubicación actual");
        mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));


        //Float zoomLevel = 16; //This goes up to 21
        Float zoomLevel=Float.parseFloat("16");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));

//aqui guardar

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class LocationInsertTask extends AsyncTask<ContentValues, Void, Void> {
        @Override
        protected Void doInBackground(ContentValues... contentValues) {

            /** Setting up values to insert the clicked location into SQLite database */
            getContentResolver().insert(LocationsContentProvider.CONTENT_URI, contentValues[0]);
            return null;
        }
    }

    private class LocationDeleteTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {

            /** Deleting all the locations stored in SQLite database */
            getContentResolver().delete(LocationsContentProvider.CONTENT_URI, null, null);
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0,
                                         Bundle arg1) {

        // Uri to the content provider LocationsContentProvider
        Uri uri = LocationsContentProvider.CONTENT_URI;

        // Fetches all the rows from locations table
        return new CursorLoader(this, uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0,
                               Cursor arg1) {

//        int locationCount = 0;
//        double lat=0;
//        double lng=0;
//        float zoom=0;
//
//        // Number of locations available in the SQLite database table
//        locationCount = arg1.getCount();
//
//        // Move the current record pointer to the first row of the table
//        arg1.moveToFirst();
//
//        for(int i=0;i<locationCount;i++){
//
//            // Get the latitude
//            lat = arg1.getDouble(arg1.getColumnIndex(LocationsDB.FIELD_LAT));
//
//            // Get the longitude
//            lng = arg1.getDouble(arg1.getColumnIndex(LocationsDB.FIELD_LNG));
//
//            // Get the zoom level
//            zoom = arg1.getFloat(arg1.getColumnIndex(LocationsDB.FIELD_ZOOM));
//
//            // Creating an instance of LatLng to plot the location in Google Maps
//            LatLng location = new LatLng(lat, lng);
//
//            // Drawing the marker in the Google Maps
//            drawMarker(location,"Nombre");
//
//            // Traverse the pointer to the next row
//            arg1.moveToNext();
//        }
//
//        if(locationCount>0){
//            // Moving CameraPosition to last clicked position
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat,lng)));
//
//            // Setting the zoom level in the map on last position  is clicked
//            mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));
//        }
        //custom


        SQLiteAdapter db = new SQLiteAdapter(getApplicationContext());

        Cursor cursor = null;
        String strLat = "";
        String strLng = "";
        String strZoom = "";
        String strName = "";
        String strCatName="";
        Float l=null;
        Float ln=null;
        Float zm=Float.parseFloat("5");


        cursor = db.getAllGeopoints();
        Log.d("CRASH REPORT",String.valueOf(cursor.getCount()));

        cursor.moveToFirst();
                for(int i=0;i<cursor.getCount();i++){

try{
                    strLat = cursor.getString(cursor.getColumnIndex("geo_lat"));
                    strLng = cursor.getString(cursor.getColumnIndex("geo_lng"));
//                    strZoom = cursor.getString(cursor.getColumnIndex("geo_zoomlevel"));
                   strName = cursor.getString(cursor.getColumnIndex("geo_name"));
                    strCatName = cursor.getString(cursor.getColumnIndex("cat_description"));

                    l= Float.parseFloat(strLat);
                    ln= Float.parseFloat(strLng);
                   // zm= Float.parseFloat(strZoom);

                    LatLng location = new LatLng(l, ln);

                    // Drawing the marker in the Google Maps
                    drawMarker(location,strName,strCatName);
                    cursor.moveToNext();
}
catch(Exception p){

}
                }




            cursor.close();

        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(l,ln)));

        // Setting the zoom level in the map on last position  is clicked
        mMap.animateCamera(CameraUpdateFactory.zoomTo(zm));
       //end custom
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // TODO Auto-generated method stub
    }
    /*end map*/

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));




        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        // Showing status
        if(status!= ConnectionResult.SUCCESS){ // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        }else { // Google Play Services are available


            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                    .setFastestInterval(1 * 1000); // 1 second, in milliseconds


            mMap.setMyLocationEnabled(true);

            // Invoke LoaderCallbacks to retrieve and draw already saved locations in map
            getSupportLoaderManager().initLoader(0, null, this);

        }


//agregar marker
//        drawMarker(pt,"Nombre");
//
//        // Creating an instance of ContentValues
//        ContentValues contentValues = new ContentValues();
//
//        // Setting latitude in ContentValues
//        contentValues.put(LocationsDB.FIELD_LAT, pt.latitude );
//
//        // Setting longitude in ContentValues
//        contentValues.put(LocationsDB.FIELD_LNG, pt.longitude);
//
//        // Setting zoom in ContentValues
//        contentValues.put(LocationsDB.FIELD_ZOOM, mMap.getCameraPosition().zoom);
// agregar marker





//        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//
//            @Override
//            public void onMapClick(LatLng point) {
//
//                final LatLng pt=point;
//
//
//
//                //agregar
//                //comentario a tarea finalizada
//                LayoutInflater li = LayoutInflater.from(context);
//                View promptsView = li.inflate(R.layout.promt_commenttask, null);
//
//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//                        context);
//
//
//                alertDialogBuilder.setView(promptsView);
//
//                final EditText userInput = (EditText) promptsView
//                        .findViewById(R.id.editTextDialogUserInput);
//                TextView tv = (TextView) promptsView.findViewById(R.id.textView1);
//                tv.setText("Punto de interés");
//
//                alertDialogBuilder
//                        .setCancelable(false)
//                        .setPositiveButton("Finalizar",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog,int id) {
//                                        //save latlng start
//
//                                        // Drawing marker on the map
//                                        drawMarker(pt,"Nombre");
//
//                                        // Creating an instance of ContentValues
//                                        ContentValues contentValues = new ContentValues();
//
//                                        // Setting latitude in ContentValues
//                                        contentValues.put(LocationsDB.FIELD_LAT, pt.latitude );
//
//                                        // Setting longitude in ContentValues
//                                        contentValues.put(LocationsDB.FIELD_LNG, pt.longitude);
//
//                                        // Setting zoom in ContentValues
//                                        contentValues.put(LocationsDB.FIELD_ZOOM, mMap.getCameraPosition().zoom);
//
//                                        // Creating an instance of LocationInsertTask
//                                        LocationInsertTask insertTask = new LocationInsertTask();
//
//                                        // Storing the latitude, longitude and zoom level to SQLite database
//                                        insertTask.execute(contentValues);
//
//                                        Toast.makeText(getBaseContext(), "Punto de interés guardado con éxito!", Toast.LENGTH_SHORT).show();
//                                        //save latlng end
//                                    }
//                                })
//                        .setNegativeButton("Cancelar",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog,int id) {
//                                        dialog.cancel();
//                                    }
//                                });
//
//                // create alert dialog
//                AlertDialog alertDialog = alertDialogBuilder.create();
//
//                // show it
//                alertDialog.show();
//                //agregar fin
//
//            }
//        });
//
//        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
//            @Override
//            public void onMapLongClick(LatLng point) {
//
//                // Removing all markers from the Google Map
//                mMap.clear();
//
//                // Creating an instance of LocationDeleteTask
//                LocationDeleteTask deleteTask = new LocationDeleteTask();
//
//                // Deleting all the rows from SQLite database table
//                deleteTask.execute();
//
//                Toast.makeText(getBaseContext(), "Todos los puntos de interés fueron eliminados", Toast.LENGTH_LONG).show();
//            }
//        });
        //end




    }

    @Override
    protected void onResume() {
        super.onResume();
        //setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }
//send to server


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
            Log.d("CHECK IN MANUAL",mEmail);

            String url_select = myArticleUrl;

            ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();


            param.add(new BasicNameValuePair("formid", "15"));
            param.add(new BasicNameValuePair("geohist_lat", checkLat));
            param.add(new BasicNameValuePair("geohist_lng", checkLng));
            param.add(new BasicNameValuePair("did", android_id));
            param.add(new BasicNameValuePair("geohist_provider", "content_provider"));
            param.add(new BasicNameValuePair("geohist_user_id", user_id));
            param.add(new BasicNameValuePair("geohist_accuracy", "1.0"));
            param.add(new BasicNameValuePair("geohist_comment", "MANUAL"));


            try {
                String resulting_json = null;
                ServiceHandler jsonParser = new ServiceHandler();
                resulting_json = jsonParser.makeServiceCall(url_select,
                        ServiceHandler.GET, param);

                Log.d("CHECK IN MANUAL",resulting_json.toString());
                JSONArray ja;
                ja = new JSONArray(resulting_json);
                CanCheckin=false;

            } catch (Exception jds) {
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Toast.makeText(MapsActivity.this, "Check-in realizado con éxito", Toast.LENGTH_LONG).show();



            mAuthTask = null;


            new CountDownTimer(30000, 1000) {

                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {
                    CanCheckin=true;
                }

            }.start();

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;

        }
    }
}
