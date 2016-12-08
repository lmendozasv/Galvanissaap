package galvanissaapp.grupowebbox.com.galvanissaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.util.ArrayList;

public class MyProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    String myArticleUrl = "http://galvanissa.lmendoza.info/backend/webroot/Mobile/service.php";
    private getTodayTask mAuthTask = null;
    String todayTasks="";
    String totalTasks="";
    String useremail="";
    TextView tv_tasks=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Solicitar ayuda aún no está disponible", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        tv_tasks=(TextView)this.findViewById(R.id.textView4);

        SharedPreferences sp;
        SharedPreferences.Editor spe;

        TextView tvposition_name =null;
        sp = getSharedPreferences("sp",MODE_PRIVATE);
        String namestr =sp.getString("user_name_drawer", "");
        String position =sp.getString("user_position", "");

        View header = ((NavigationView)findViewById(R.id.nav_view)).getHeaderView(0);
        ((TextView) header.findViewById(R.id.nombreperfil)).setText(namestr);
        ((TextView) header.findViewById(R.id.positionname)).setText(position);

        TextView name = (TextView)this.findViewById(R.id.tvnombre);
        TextView pos = (TextView)this.findViewById(R.id.tvfrase);
        name.setText(namestr);
        pos.setText(position);
        this.setTitle("Mi perfil");
        useremail =sp.getString("user_email", "");




        BootstrapButton button = (BootstrapButton) findViewById(R.id.btnchangepassword);

        button.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                Intent main = new Intent(MyProfileActivity.this, ChangePasswordActivity.class);


                startActivity(main);
            }
        });



        mAuthTask = new getTodayTask(useremail,"");

        mAuthTask.execute((Void) null);


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
        //getMenuInflater().inflate(R.menu.my_profile, menu);
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
            i = new Intent(MyProfileActivity.this, MainActivity.class);
        } else if (id == R.id.nav_gallery) {
            i = new Intent(MyProfileActivity.this, ListActivity.class);
        } else if (id == R.id.nav_slideshow) {
            i = new Intent(MyProfileActivity.this, TaskActivity.class);
        } else if (id == R.id.nav_manage) {
            i = new Intent(MyProfileActivity.this, MapsActivity.class);
        } else if (id == R.id.nav_share) {
            i = new Intent(MyProfileActivity.this, MyProfileActivity.class);
        } else if (id == R.id.exit) {

        }

        if(i!=null){
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public class getTodayTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        getTodayTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            Log.d("cuentaback",mEmail);

            String url_select = myArticleUrl;


            ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("formid", "11"));
            param.add(new BasicNameValuePair("email", useremail));


            try {
                String resulting_json = null;
                ServiceHandler jsonParser = new ServiceHandler();
                resulting_json = jsonParser.makeServiceCall(url_select,
                        ServiceHandler.GET, param);
                Log.d("Result",resulting_json);

                JSONArray ja;
                ja = new JSONArray(resulting_json);

                if (ja != null) {
                    for (int i = 0; i < ja.length(); i++) {
                        todayTasks = ja.getJSONObject(i).getString("today_tasks");
                        totalTasks = ja.getJSONObject(i).getString("all_tasks");


                    }
                }
            } catch (Exception jds) {
                Log.d("Error",jds.toString());
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            //poner aqui las tareas

                    tv_tasks.setText(todayTasks+" / "+totalTasks);
            mAuthTask = null;

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;

        }
    }
    @Override
    public void onResume(){
        super.onResume();

        mAuthTask = new getTodayTask(useremail,"");

        mAuthTask.execute((Void) null);
    }
}
