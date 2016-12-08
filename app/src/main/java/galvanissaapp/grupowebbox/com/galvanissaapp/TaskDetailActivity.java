package galvanissaapp.grupowebbox.com.galvanissaapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

public class TaskDetailActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    SharedPreferences sp;
    SharedPreferences.Editor spe;
    final Context context = this;
    private EditText result;
    String TaskID="";
    String EMail="";
    String NComment="";
    String AsigneeName="";
    String OComment="";
    String myArticleUrl = "http://galvanissa.lmendoza.info/backend/webroot/Mobile/service.php";
    String ind="";

    private saveTasktoDB mAuthTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        this.setTitle("Detalle");
        EditText ddate = (EditText)this.findViewById(R.id.txtduedatedetail);

        EditText ddateassign = (EditText)this.findViewById(R.id.txtassigndate);
        EditText dfromtask = (EditText)this.findViewById(R.id.txtfromtask);
        EditText dtaskdetail = (EditText)this.findViewById(R.id.txttaskdetail);
        TextView etname = (TextView)this.findViewById(R.id.txttaskname);
        TextView et_assignedto = (TextView)this.findViewById(R.id.tvassignedto);

        BootstrapButton button = (BootstrapButton) findViewById(R.id.btnadjunto);
        BootstrapButton buttonObs = (BootstrapButton) findViewById(R.id.buttonobs);
        BootstrapButton buttonCompleted = (BootstrapButton) findViewById(R.id.button);

        String duedate ="";
        String fassign="";
        String assigner="";
        String detailtask="";
        String ttask="";
        String attached="";



        sp = getSharedPreferences("sp",MODE_PRIVATE);
        duedate =sp.getString("ddates", "");
        fassign=sp.getString("dassign","");
        assigner=sp.getString("dassignname","");
        detailtask=sp.getString("ddetail","");
        ttask=sp.getString("titletask","");
        attached=sp.getString("attached","");
        TaskID=sp.getString("id","");
        EMail =sp.getString("user_email", "");
        OComment=sp.getString("ddetail","");
        AsigneeName=sp.getString("assignedtoid","");
        //Search name by id
        SQLiteAdapter db = new SQLiteAdapter(getApplicationContext());
        String EmpName=db.EmployeeName(AsigneeName);
        if (EmpName.equals("")|| EmpName.length()==0){
            et_assignedto.setText("Tarea propia");
        }
        else{
            et_assignedto.setText("Asignado a: "+EmpName);
        }


        ddate.setText(duedate.substring(0,10));
        ddateassign.setText(fassign);
        dfromtask.setText(assigner);
        dtaskdetail.setText(detailtask);
        etname.setText(ttask);

        String  statustask="";
        statustask=sp.getString("statustask","");

        if(statustask.equals("0")){

            etname.setBackgroundColor(Color.parseColor("#80045FB4"));
        }


        if(statustask.equals("1")){

            etname.setBackgroundColor(Color.parseColor("#80DF7401"));
        }


        if(statustask.equals("2")){

            etname.setBackgroundColor(Color.parseColor("#805FB404"));
            buttonCompleted.setEnabled(false);
            buttonObs.setEnabled(false);
        }


        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {

                Intent main = new Intent(TaskDetailActivity.this, ShowImageActivity.class);
                //imagename
                startActivity(main);

            }
        });

        buttonCompleted.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                //comentario a tarea finalizada
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.promt_commenttask, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);


                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);


                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Finalizar",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // get user input and set it to result
                                        // edit text
                                        //result.setText(userInput.getText());
                                        ind="1";
                                        NComment=userInput.getText().toString();
                                        mAuthTask = new saveTasktoDB("","");
                                        mAuthTask.execute((Void) null);
                                    }
                                })
                        .setNegativeButton("Cancelar",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }
        });


        if(attached.equals("1")){
            button.setText("Ver adjunto");
        }
        else{
            button.setText("Sin adjuntos");
            button.setEnabled(false);
        }

        result = (EditText) findViewById(R.id.txttaskdetail);
        buttonObs.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                //comentario a tarea
                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.promt_commenttask, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Aceptar",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // get user input and set it to result
                                        // edit text
                                        //result.setText(userInput.getText());
                                        ind="0";
                                        NComment=userInput.getText().toString();
                                        mAuthTask = new saveTasktoDB("","");
                                        mAuthTask.execute((Void) null);
                                    }
                                })
                        .setNegativeButton("Cancelar",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }
        });

        String namestr =sp.getString("user_name_drawer", "");
        String position =sp.getString("user_position", "");

        View header = ((NavigationView)findViewById(R.id.nav_view)).getHeaderView(0);
        ((TextView) header.findViewById(R.id.nombreperfil)).setText(namestr);
        ((TextView) header.findViewById(R.id.positionname)).setText(position);

        TextView name = (TextView)this.findViewById(R.id.tvnombre);
        TextView pos = (TextView)this.findViewById(R.id.tvfrase);
//        name.setText(namestr);
 //       pos.setText(position);

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
        //getMenuInflater().inflate(R.menu.task_detail, menu);
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
            i = new Intent(TaskDetailActivity.this, MainActivity.class);
        } else if (id == R.id.nav_gallery) {
            i = new Intent(TaskDetailActivity.this, ListActivity.class);
        } else if (id == R.id.nav_slideshow) {
            i = new Intent(TaskDetailActivity.this, TaskActivity.class);
        } else if (id == R.id.nav_manage) {
            i = new Intent(TaskDetailActivity.this, MapsActivity.class);
        } else if (id == R.id.nav_share) {
            i = new Intent(TaskDetailActivity.this, MyProfileActivity.class);
        } else if (id == R.id.exit) {

        }

        if(i!=null){
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public class saveTasktoDB extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        saveTasktoDB(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            Log.d("cuentaback",mEmail);

            String url_select = myArticleUrl;


            ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
            if(ind.equals("1")){
                param.add(new BasicNameValuePair("formid", "10"));
            }
            else{
                param.add(new BasicNameValuePair("formid", "9"));
            }

            param.add(new BasicNameValuePair("email", EMail));
            param.add(new BasicNameValuePair("idtask", TaskID));

            param.add(new BasicNameValuePair("ncomment", NComment));
            param.add(new BasicNameValuePair("ocomment", OComment));

            try {
                String resulting_json = null;
                ServiceHandler jsonParser = new ServiceHandler();
                resulting_json = jsonParser.makeServiceCall(url_select,
                        ServiceHandler.GET, param);
                Log.d("CUENTA",resulting_json.toString());
            } catch (Exception jds) {
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Toast.makeText(TaskDetailActivity.this, "Tarea actualizada exitosamente!", Toast.LENGTH_LONG).show();
            finish();
            mAuthTask = null;

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;

        }
    }



}
