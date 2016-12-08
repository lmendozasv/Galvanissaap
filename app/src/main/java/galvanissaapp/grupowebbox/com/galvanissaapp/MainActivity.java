package galvanissaapp.grupowebbox.com.galvanissaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    String user_firstname="";
    String user_lastname="";
    String position_name="";
    final int PERMISSION_REQUEST_CODE=10;
    final int PERMISSION_REQUEST_CODEI=100;
    JSONParserAlt jParser = new JSONParserAlt();

    String myArticleUrl = "http://galvanissa.lmendoza.info/backend/webroot/Mobile/service.php";
    SharedPreferences sp;
    SharedPreferences.Editor spe;

    private getUserDetailsTask mAuthTask = null;
    private SQLiteAdapter mySQLiteAdapter;
    NavigationView navigationView = null;

    TextView name =null;
    TextView tvposition_name =null;

    boolean tr=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               //nueva tarea
                startActivityForResult(new Intent(MainActivity.this,NewTaskActivity.class), 0);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        this.setTitle("Inicio");


        RelativeLayout relativemap =(RelativeLayout)findViewById(R.id.btnlist);
        relativemap.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivityForResult(new Intent(MainActivity.this,ListActivity.class), 0);
            }
        });


        RelativeLayout relativelist =(RelativeLayout)findViewById(R.id.btntask);
        relativelist.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivityForResult(new Intent(MainActivity.this,TaskActivity.class), 0);
            }
        });


        RelativeLayout relativetasks =(RelativeLayout)findViewById(R.id.fragment_left_menu_login);
        relativetasks.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivityForResult(new Intent(MainActivity.this,MapsActivity.class), 0);
            }
        });


        RelativeLayout relativeprofile =(RelativeLayout)findViewById(R.id.btnprofile);
        relativeprofile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivityForResult(new Intent(MainActivity.this,MyProfileActivity.class), 0);
            }
        });

        sp = getSharedPreferences("sp",MODE_PRIVATE);
        String email =sp.getString("user_email", "");






        Log.d("cuenta",email);
        mAuthTask = new getUserDetailsTask(email,"");

        mAuthTask.execute((Void) null);



        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkPermission())
            {
                tr=true;
            } else {
                tr=false;
                requestPermission();
            }

            if (checkPermissionI())
            {
                tr=true;
            } else {
                tr=false;
                requestPermissionI();
            }

        }
        else
        {


        }


if(tr){
    startService(new Intent(this, TrackLocationService.class));
}

    }


    private void requestPermissionI() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Toast.makeText(MainActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODEI);
        }



    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(MainActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }


    }
    private boolean checkPermissionI() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent i =null;

        if (id == R.id.ic_home) {
            //i = new Intent(MainActivity.this, MainActivity.class);
        } else if (id == R.id.nav_gallery) {
            i = new Intent(MainActivity.this, ListActivity.class);
        } else if (id == R.id.nav_slideshow) {
            i = new Intent(MainActivity.this, TaskActivity.class);
        } else if (id == R.id.nav_manage) {
            i = new Intent(MainActivity.this, MapsActivity.class);
        } else if (id == R.id.nav_share) {
            i = new Intent(MainActivity.this, MyProfileActivity.class);
        } else if (id == R.id.exit) {

        }

        if(i!=null){
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    public class getUserDetailsTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        getUserDetailsTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            Log.d("cuentaback",mEmail);

            String url_select = myArticleUrl;

            ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
            ArrayList<NameValuePair> paramx = new ArrayList<NameValuePair>();
            ArrayList<NameValuePair> paramxx = new ArrayList<NameValuePair>();

            param.add(new BasicNameValuePair("formid", "2"));
            param.add(new BasicNameValuePair("email", mEmail));


            try {
                String resulting_json = null;
                ServiceHandler jsonParser = new ServiceHandler();
                resulting_json = jsonParser.makeServiceCall(url_select,
                        ServiceHandler.GET, param);

                Log.d("CUENTA",resulting_json.toString());
                JSONArray ja;
                ja = new JSONArray(resulting_json);

                if (ja != null) {
                    for (int i = 0; i < ja.length(); i++) {
                        user_firstname = ja.getJSONObject(i).getString("user_firstname");
                        user_lastname = ja.getJSONObject(i).getString("user_lastname");
                        position_name=ja.getJSONObject(i).getString("position_name");








                    }
                }



                paramx.add(new BasicNameValuePair("formid", "12"));
                paramx.add(new BasicNameValuePair("email", mEmail));
                Log.d("RET",mEmail);

                try {
                    String resulting_jsons = null;
                    ServiceHandler jsonParsers = new ServiceHandler();
                    resulting_jsons = jsonParsers.makeServiceCall(url_select,
                            ServiceHandler.GET, paramx);


                    JSONArray jas;
                    jas = new JSONArray(resulting_jsons);
                    Log.d("DATA",resulting_jsons);


                    if (jas != null) {
                        for (int i = 0; i < jas.length(); i++) {
                            //insert employees to list
                            mySQLiteAdapter = new SQLiteAdapter(MainActivity.this);
                            mySQLiteAdapter.openToWrite();
                            mySQLiteAdapter.insertSubordinate(jas.getJSONObject(i).getString("user_firstname")+ " "  +jas.getJSONObject(i).getString("user_lastname"),jas.getJSONObject(i).getString("user_id"));
                        }
                    }
                } catch (Exception jds) {
                }








                paramxx.add(new BasicNameValuePair("formid", "14"));
                paramxx.add(new BasicNameValuePair("email", mEmail));
                Log.d("RET",mEmail);

                try {
                    String resulting_jsons = null;
                    ServiceHandler jsonParsers = new ServiceHandler();
                    resulting_jsons = jsonParsers.makeServiceCall(url_select,
                            ServiceHandler.GET, paramxx);


                    JSONArray jas;
                    jas = new JSONArray(resulting_jsons);
                    Log.d("DATA",resulting_jsons);


                    if (jas != null) {
                        mySQLiteAdapter = new SQLiteAdapter(MainActivity.this);
                        mySQLiteAdapter.openToWrite();
                        mySQLiteAdapter.deleteAllGeopoints();
                        for (int i = 0; i < jas.length(); i++) {
                            //insert employees to list
                            mySQLiteAdapter.insertGeopoint(
                                    jas.getJSONObject(i).getString("geo_id"),
                                    jas.getJSONObject(i).getString("geo_name"),
                                    jas.getJSONObject(i).getString("geo_lat"),
                                    jas.getJSONObject(i).getString("geo_lng"),
                                    jas.getJSONObject(i).getString("geo_mts"),
                                    jas.getJSONObject(i).getString("geo_status"),
                                    jas.getJSONObject(i).getString("cat_id"),
                                    jas.getJSONObject(i).getString("geo_positions"),
                                    jas.getJSONObject(i).getString("geo_zoomlevel"),
                                    jas.getJSONObject(i).getString("cat_name"),
                                    jas.getJSONObject(i).getString("cat_description"),
                                    jas.getJSONObject(i).getString("cat_icon")
                            );
                        }
                    }
                } catch (Exception jds) {
                }





            } catch (Exception jds) {
            }


            if (user_firstname.length()>0) {
                Log.d("data",user_firstname);
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                navigationView = (NavigationView) findViewById(R.id.nav_view);

                                name =(TextView)navigationView.findViewById(R.id.nombreperfil);
                                tvposition_name =(TextView)navigationView.findViewById(R.id.positionname);

                                name.setText(user_firstname+" "+user_lastname);
                                tvposition_name.setText(position_name);

                                spe = sp.edit();
                                spe.putString("user_name_drawer",user_firstname+" "+user_lastname );
                                spe.apply();
                                Log.d("si",user_firstname+" "+user_lastname);
                                spe = sp.edit();
                                spe.putString("user_position",position_name );
                                spe.apply();


                            }
                        });
                    }
                },  10); // End of your timer code.

                return true;
            }
            else{

                return false;
            }
///load employees





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
