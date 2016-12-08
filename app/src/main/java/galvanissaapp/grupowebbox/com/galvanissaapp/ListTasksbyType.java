package galvanissaapp.grupowebbox.com.galvanissaapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.beardedhen.androidbootstrap.BootstrapButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ListTasksbyType extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    String myArticleUrl = "http://galvanissa.lmendoza.info/backend/webroot/Mobile/service.php";
    private getTaskDetail mAuthTask = null;
    List<String> NameTask = new ArrayList<String>();
    List<String> StatusTask = new ArrayList<String>();

    List<String> IDTask = new ArrayList<String>();
    List<String> Duedates = new ArrayList<String>();
    List<String> AssignDate = new ArrayList<String>();
    List<String> AssignName = new ArrayList<String>();
    List<String> TaskDetail = new ArrayList<String>();
    List<String> TaskAttached = new ArrayList<String>();
    List<String> TaskImagename = new ArrayList<String>();

    List<String> TaskAssignetTo = new ArrayList<String>();


    String formatedDateMYSQL="";
    final Context context = this;
    BootstrapButton buttonDate =null;
            EditText userInput =null;

            AdaptadorLista ma ;


    SharedPreferences sp;
    SharedPreferences.Editor spe;
    String useremail="";
    String ttareas ="";
    String StatusList ="0,1,2";
    String StatusListTmp ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tasksby_type);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        String TipoVista="Tareas";
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                TipoVista= null;
            } else {
                TipoVista= extras.getString("Tipo");
                ttareas=extras.getString("id_tipo");
            }
        } else {
            TipoVista= (String) savedInstanceState.getSerializable("Tipo");
            ttareas= (String) savedInstanceState.getSerializable("id_tipo");

        }

        this.setTitle(TipoVista);

        sp = getSharedPreferences("sp",MODE_PRIVATE);
        useremail =sp.getString("user_email", "");

        mAuthTask = new getTaskDetail(useremail,"");

        mAuthTask.execute((Void) null);



        String namestr =sp.getString("user_name_drawer", "");
        String position =sp.getString("user_position", "");

        View header = ((NavigationView)findViewById(R.id.nav_view)).getHeaderView(0);
        ((TextView) header.findViewById(R.id.nombreperfil)).setText(namestr);
        ((TextView) header.findViewById(R.id.positionname)).setText(position);

        TextView name = (TextView)this.findViewById(R.id.tvnombre);
        TextView pos = (TextView)this.findViewById(R.id.tvfrase);
        //name.setText(namestr);
        //pos.setText(position);



        //filtros

         buttonDate = (BootstrapButton) findViewById(R.id.btnFecha);



        BootstrapButton buttonStatus = (BootstrapButton) findViewById(R.id.btnEstado);

        buttonDate.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(ListTasksbyType.this,new mDateSetListener(), mYear, mMonth, mDay);
                dialog.show();

            }
        });

        if(ttareas.equals("1")){
            buttonDate.setEnabled(false);
        }

        buttonStatus.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {

                /*inicio*/
                //comentario a tarea finalizada
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.prompt_statusfilter, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);


                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);

                final CheckBox chkFinalizadas = (CheckBox) promptsView
                        .findViewById(R.id.chkFinalizadas);

                final CheckBox chkReporte = (CheckBox) promptsView
                        .findViewById(R.id.chkConReporte);

                final CheckBox chkAsignadas = (CheckBox) promptsView
                        .findViewById(R.id.chkAsignadas);


                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // get user input and set it to result
                                        // edit text
                                        //result.setText(userInput.getText());

                                        StatusListTmp="";

                                        if (chkFinalizadas.isChecked()){
                                            StatusListTmp="2";

                                        }
                                        if(chkAsignadas.isChecked()){
                                            StatusListTmp=StatusListTmp+",0";

                                        }
                                        if(chkReporte.isChecked()){
                                            StatusListTmp=StatusListTmp+",1";

                                        }

                                        StatusListTmp = StatusListTmp.startsWith(",") ? StatusListTmp.substring(1) : StatusListTmp;
                                        StatusListTmp = StatusListTmp.endsWith(",") ? StatusListTmp.substring(1) : StatusListTmp;

                                        Log.d("FILTRO",StatusListTmp);
                                        StatusList=StatusListTmp;

                                        mAuthTask = new getTaskDetail(useremail,"");

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
        /*fin*/

            }
        });

    }


    class mDateSetListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            int mYear = year;
            int mMonth = monthOfYear;
            int mDay = dayOfMonth;

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String formatedDate = sdf.format(new Date(year-1900, mMonth, mDay));

            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
            formatedDateMYSQL = sdf2.format(new Date(year-1900, mMonth, mDay));

            buttonDate.setText(""+formatedDate+"");
            buttonDate.setTag(formatedDateMYSQL);

            mAuthTask = new getTaskDetail(useremail,"");

            mAuthTask.execute((Void) null);

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
        //getMenuInflater().inflate(R.menu.list_tasksby_type, menu);
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
            i = new Intent(ListTasksbyType.this, MainActivity.class);
        } else if (id == R.id.nav_gallery) {
            i = new Intent(ListTasksbyType.this, ListActivity.class);
        } else if (id == R.id.nav_slideshow) {
            i = new Intent(ListTasksbyType.this, TaskActivity.class);
        } else if (id == R.id.nav_manage) {
            i = new Intent(ListTasksbyType.this, MapsActivity.class);
        } else if (id == R.id.nav_share) {
            i = new Intent(ListTasksbyType.this, MyProfileActivity.class);
        } else if (id == R.id.exit) {

        }

        if(i!=null){
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class getTaskDetail extends AsyncTask<Void, Void, Boolean> {

        ProgressDialog pDialog;
        private final String mEmail;
        private final String mPassword;

        getTaskDetail(String email, String password) {
            mEmail = email;
            mPassword = password;

        }
        @Override
        protected void onPreExecute(){
            pDialog = new ProgressDialog(ListTasksbyType.this);
            pDialog.setMessage("Obteniendo datos");
            pDialog.show();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            Log.d("cuentaback",mEmail);
            Log.d("tareas:s:",ttareas);
            Log.d("DA",formatedDateMYSQL);
            String url_select = myArticleUrl;

            ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
            String DateFilter ="";
            DateFilter=formatedDateMYSQL;

            if(DateFilter.length()>1){
                param.add(new BasicNameValuePair("datefilter", formatedDateMYSQL));
            }
            else
            {

            }





            param.add(new BasicNameValuePair("status", StatusList));

            if(ttareas.equals("1")){
                param.add(new BasicNameValuePair("formid", "6"));
            }

            if(ttareas.equals("2")){
                param.add(new BasicNameValuePair("formid", "7"));
                Log.d("Dale","parametro added");
            }


            if(ttareas.equals("3")){
                param.add(new BasicNameValuePair("formid", "8"));
            }


            param.add(new BasicNameValuePair("email", mEmail));


            try {
                String resulting_json = null;
                ServiceHandler jsonParser = new ServiceHandler();
                resulting_json = jsonParser.makeServiceCall(url_select,
                        ServiceHandler.GET, param);
                Log.d("Param",param.toString());
                Log.d("Result",resulting_json);
                Log.d("CUENTA",resulting_json.toString());
                JSONArray ja;
                ja = new JSONArray(resulting_json);
                NameTask.clear();
                StatusTask.clear();
                IDTask.clear();
                 Duedates.clear();
                 AssignDate.clear();
                AssignName.clear();
                TaskDetail.clear();
                TaskAttached.clear();
                TaskImagename.clear();
                TaskAssignetTo.clear();

                String attached="0";

                if (ja != null) {
                    for (int i = 0; i < ja.length(); i++) {
                        IDTask.add(ja.getJSONObject(i).getString("task_id"));
                        NameTask.add(ja.getJSONObject(i).getString("task_title"));
                        StatusTask.add(ja.getJSONObject(i).getString("task_status"));

                          Duedates.add(ja.getJSONObject(i).getString("task_duedate"));
                        AssignDate.add(ja.getJSONObject(i).getString("task_timestamp"));
                        AssignName.add(ja.getJSONObject(i).getString("user_firstname")+" "+ja.getJSONObject(i).getString("user_lastname"));
                        TaskDetail.add(ja.getJSONObject(i).getString("task_description"));
                        TaskAssignetTo.add(ja.getJSONObject(i).getString("task_user_id_to"));


                        Log.d("Asignada a:",ja.getJSONObject(i).getString("task_user_id_to"));
                        Log.d("ADJUNTO:============",ja.getJSONObject(i).getString("task_imgid")+"/"+ja.getJSONObject(i).getString("task_imgname"));

                        String compath="";
                        compath=ja.getJSONObject(i).getString("task_imgid");

                        if(ja.getJSONObject(i).getString("task_imgname").length()>4){//null = 4
                            TaskAttached.add("1");
                            if(compath.length()>4){
                                TaskImagename.add(ja.getJSONObject(i).getString("task_imgid")+"/"+ja.getJSONObject(i).getString("task_imgname"));
                            }
                            else

                                TaskImagename.add(ja.getJSONObject(i).getString("task_imgname"));
                        }
                        else{
                            TaskAttached.add("0");
                            TaskImagename.add(ja.getJSONObject(i).getString("task_imgid")+"/"+ja.getJSONObject(i).getString("task_imgname"));
                        }


                    }
                }


            } catch (Exception jds) {
                Log.d("Error HT",jds.toString());
            }


          return true;


        }

        @Override
        protected void onPostExecute(final Boolean success) {


            ListView listContent = (ListView)findViewById(R.id.listView);
            ma = new AdaptadorLista();

            listContent.setAdapter(ma);


            listContent.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapter, View v, int position,
                                        long arg3)
                {
                    Integer value = (Integer)adapter.getItemAtPosition(position);
                    Log.d("POSICION",value.toString());
                    // assuming string and if you want to get the value on click of list item
                    // do what you intend to do on click of listview row

                    String ddates =Duedates.get(position);
                    String dassign =AssignDate.get(position);
                    String dassignname =AssignName.get(position);
                    String ddetail =TaskDetail.get(position);
                    String title=NameTask.get(position);
                    String status=StatusTask.get(position);
                    String attached =TaskAttached.get(position);
                    String timagename=TaskImagename.get(position);
                    String AssignedtoID=TaskAssignetTo.get(position);
                    Log.d("Com",AssignedtoID);
                    String id = IDTask.get(position);

                        Intent main = new Intent(ListTasksbyType.this, TaskDetailActivity.class);
                        main.putExtra("Tipo", "Tareas asignadas");
                        startActivity(main);

                    spe = sp.edit();
                    spe.putString("ddates", ddates);
                    spe.putString("dassign", dassign);
                    spe.putString("dassignname", dassignname);
                    spe.putString("ddetail", ddetail);
                    spe.putString("titletask", title);
                    spe.putString("statustask", status);
                    spe.putString("attached", attached);
                    spe.putString("imagename", timagename);
                    spe.putString("assignedtoid", AssignedtoID);
                    spe.putString("id", id);
                    spe.apply();

                }
            });

            mAuthTask = null;



            pDialog.dismiss();

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;

        }
    }

    class AdaptadorLista extends BaseAdapter implements CompoundButton.OnCheckedChangeListener
    {  private SparseBooleanArray mCheckStates;
        LayoutInflater mInflater;
        TextView lblnombreprofesor,lblprecio,lblfecha,txt_hora;
        TextDrawable drawable =null;
        ImageView image=null;


        AdaptadorLista()
        {
            mCheckStates = new SparseBooleanArray(NameTask.size());
            mInflater = (LayoutInflater)ListTasksbyType.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return NameTask.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub

            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View vi=convertView;
            if(convertView==null)
                vi = mInflater.inflate(R.layout.row_task_bytype, null);
            lblprecio= (TextView) vi.findViewById(R.id.title_row);
            lblfecha= (TextView) vi.findViewById(R.id.fecha);



            //Log.d("array ",names.toString());
            String title=NameTask.get(position);
            String status=StatusTask.get(position);








            String str_stado = "";

            if(status.equals("0")){
                str_stado="Asignada";
                lblfecha.setTextColor(Color.parseColor("#045FB4"));
            }
            if(status.equals("1")){
                str_stado="Con reporte";
                lblfecha.setTextColor(Color.parseColor("#DF7401"));
            }
            if(status.equals("2")){
                str_stado="Finalizada";
                lblfecha.setTextColor(Color.parseColor("#5FB404"));
            }

            lblprecio.setText(String.valueOf(title));


            lblfecha.setText(String.valueOf(str_stado));



            return vi;
        }
        public boolean isChecked(int position) {
            //Toast.makeText(ContactsActivity.this, "checkeyadasasosasdasdasdasdsadasdsadsassss",1000).show();
            return mCheckStates.get(position, false);

        }

        public void setChecked(int position, boolean isChecked) {
            mCheckStates.put(position, isChecked);
            //Toast.makeText(ContactsActivity.this, "checkeyado",1000).show();
        }

        public void toggle(int position) {
            setChecked(position, !isChecked(position));
            //Toast.makeText(ContactsActivity.this, "checkeyadosasdhaks",1000).show();
        }
        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // TODO Auto-generated method stub
            //Toast.makeText(ContactsActivity.this, "checkeyadosssss",1000).show();



        }
    }
    @Override
    public void onResume(){
        super.onResume();

        mAuthTask = new getTaskDetail(useremail,"");

        mAuthTask.execute((Void) null);
    }
}
