package galvanissaapp.grupowebbox.com.galvanissaapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

//import android.widget.SimpleCursorAdapter;

public class ChangePasswordActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{


    Cursor cursor =null;
    ArrayList<String> titulo = new ArrayList<String>();
    ArrayList<String> contenido = new ArrayList<String>();
    ArrayList<String> fecha = new ArrayList<String>();
    ArrayList<String> hora = new ArrayList<String>();
    SharedPreferences sp;
    SharedPreferences.Editor spe;
    String myArticleUrl = "http://galvanissa.lmendoza.info/backend/webroot/Mobile/service.php";
    private SaveNewPasswordTask mAuthTask = null;


    String result ="";
    private ProgressDialog pdia;

    String email="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //start

        //end
        this.setTitle("Cambiar clave");

        /*Drawer header values start*/

        TextView tvposition_name =null;
        sp = getSharedPreferences("sp",MODE_PRIVATE);
        String namestr =sp.getString("user_name_drawer", "");
        String position =sp.getString("user_position", "");

        View header = ((NavigationView)findViewById(R.id.nav_view)).getHeaderView(0);
        ((TextView) header.findViewById(R.id.nombreperfil)).setText(namestr);
        ((TextView) header.findViewById(R.id.positionname)).setText(position);
        //ctx=this.getApplicationContext();
        /*Drawer heade valeus end*/
        BootstrapButton button = (BootstrapButton) findViewById(R.id.task2);

        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                //save changes

                EditText c_actual = (EditText)findViewById(R.id.txtclaveactual);

                EditText c_nueva1 = (EditText)findViewById(R.id.txtnueva1);
                EditText c_nueva2 = (EditText)findViewById(R.id.txtnueva2);
                String t_actual,t_nueva1,t_nueva2="";

                t_actual=c_actual.getText().toString();
                t_nueva1=c_nueva1.getText().toString();
                t_nueva2=c_nueva2.getText().toString();

                String pwd="";
                email=sp.getString("user_email","");
                pwd=sp.getString("user_pwd","");

                if(md5(t_actual).length()>0 && previousPwdIsValid(md5(t_actual),pwd)){
                    if(t_nueva1.length()>0)
                    {
                        Log.d("valido1",t_nueva1);
                        if(t_nueva2.length()>0)
                        {
                            Log.d("valido2",t_nueva2);


                            if(md5(t_nueva1).equals(md5(t_nueva2))){
                                //All is ok! then save
                                mAuthTask = new SaveNewPasswordTask(t_nueva1,t_nueva1);

                                mAuthTask.execute((Void) null);
                            }


                        }
                        else{
                            Toast.makeText(ChangePasswordActivity.this, "Rellenar todos los campos",
                                    Toast.LENGTH_LONG).show();
                            c_nueva2.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(c_nueva2, InputMethodManager.SHOW_IMPLICIT);
                        }
                    }
                    else{
                        Toast.makeText(ChangePasswordActivity.this, "Rellenar todos los campos",
                                Toast.LENGTH_LONG).show();
                        c_nueva1.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(c_nueva1, InputMethodManager.SHOW_IMPLICIT);
                    }
                }
                else{
                    Toast.makeText(ChangePasswordActivity.this, "Clave actual no válida",
                            Toast.LENGTH_LONG).show();
                }


            }
        });
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
        //getMenuInflater().inflate(R.menu.list, menu);
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
            i = new Intent(ChangePasswordActivity.this, MainActivity.class);
        } else if (id == R.id.nav_gallery) {
            i = new Intent(ChangePasswordActivity.this, ListActivity.class);
        } else if (id == R.id.nav_slideshow) {
            i = new Intent(ChangePasswordActivity.this, TaskActivity.class);
        } else if (id == R.id.nav_manage) {
            i = new Intent(ChangePasswordActivity.this, MapsActivity.class);
        } else if (id == R.id.nav_share) {
            i = new Intent(ChangePasswordActivity.this, MyProfileActivity.class);
        } else if (id == R.id.exit) {

        }

        if(i!=null){
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public class SaveNewPasswordTask extends AsyncTask<Void, Void, Boolean> {

       // private final String mEmail;
        private final String mPassword;

        SaveNewPasswordTask(String email, String password) {
            //mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            Log.d("cuentaback",mPassword);

            String url_select = myArticleUrl;

            ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("formid", "3"));
            param.add(new BasicNameValuePair("npassword", md5(mPassword)));
            param.add(new BasicNameValuePair("email", email));

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
                        result = ja.getJSONObject(i).getString("result");

                    }
                }




            } catch (Exception jds) {
            }


            if (result.length()>0) {

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Toast.makeText(ChangePasswordActivity.this, "Clave cambiada correctamente",
                                        Toast.LENGTH_LONG).show();

                                Intent i = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                                startActivity(i);
                                ChangePasswordActivity.this.finish();

                            }
                        });
                    }
                },  10); // End of your timer code.

                return true;
            }
            else{

                return false;
            }


        }

        @Override
        protected void onPostExecute(final Boolean success) {
            pdia.dismiss();
            mAuthTask = null;





        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pdia = ProgressDialog.show(
                    ChangePasswordActivity.this, "",
                    "Espere un momento", true);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;

        }
    }



    public static String md5(String s)
    {
        MessageDigest digest;
        try
        {
            digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes(Charset.forName("US-ASCII")),0,s.length());
            byte[] magnitude = digest.digest();
            BigInteger bi = new BigInteger(1, magnitude);
            String hash = String.format("%0" + (magnitude.length << 1) + "x", bi);
            return hash;
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public boolean previousPwdIsValid(String enteredpw,String previouspwd){
                boolean rpt=false;
        if(enteredpw.equals(previouspwd)){
            rpt=true;
        }
        else{
            rpt=false;
        }
            return rpt;
        }

}
