package galvanissaapp.grupowebbox.com.galvanissaapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    AdaptadorLista ma ;
    List<String> optionsArray = new ArrayList<String>();
    ArrayList<String> NameTasks = new ArrayList<String>();
    ArrayList<String> CountTasks = new ArrayList<String>();
    String myArticleUrl = "http://galvanissa.lmendoza.info/backend/webroot/Mobile/service.php";
    private getTodayTask mAuthTask = null;
    String todayTasks="";
    String mytasks="";
    String delegatedTasks="";
    String useremail ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taska);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //onclick
                Intent i = new Intent(TaskActivity.this, NewTaskActivity.class);

                    startActivity(i);

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        SharedPreferences sp;
        SharedPreferences.Editor spe;

        TextView tvposition_name =null;
        sp = getSharedPreferences("sp",MODE_PRIVATE);
        String namestr =sp.getString("user_name_drawer", "");
        String position =sp.getString("user_position", "");

        View header = ((NavigationView)findViewById(R.id.nav_view)).getHeaderView(0);
        ((TextView) header.findViewById(R.id.nombreperfil)).setText(namestr);
        ((TextView) header.findViewById(R.id.positionname)).setText(position);
        this.setTitle("Tareas");





        sp = getSharedPreferences("sp",MODE_PRIVATE);
        useremail =sp.getString("user_email", "");


        mAuthTask = new getTodayTask("","");

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
        //getMenuInflater().inflate(R.menu.taska, menu);
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
            i = new Intent(TaskActivity.this, MainActivity.class);
        } else if (id == R.id.nav_gallery) {
            i = new Intent(TaskActivity.this, ListActivity.class);
        } else if (id == R.id.nav_slideshow) {
            i = new Intent(TaskActivity.this, TaskActivity.class);
        } else if (id == R.id.nav_manage) {
            i = new Intent(TaskActivity.this, MapsActivity.class);
        } else if (id == R.id.nav_share) {
            i = new Intent(TaskActivity.this, MyProfileActivity.class);
        } else if (id == R.id.exit) {

        }

        if(i!=null){
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class AdaptadorLista extends BaseAdapter implements CompoundButton.OnCheckedChangeListener
    {  private SparseBooleanArray mCheckStates;
        LayoutInflater mInflater;
        TextView lblnombreprofesor,lblprecio,lblfecha,txt_hora;
        TextDrawable drawable =null;
        ImageView image=null;


        AdaptadorLista()
        {
            mCheckStates = new SparseBooleanArray(optionsArray.size());
            mInflater = (LayoutInflater)TaskActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return optionsArray.size();
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
                vi = mInflater.inflate(R.layout.row_task, null);
            lblprecio= (TextView) vi.findViewById(R.id.title_row);
            lblfecha= (TextView) vi.findViewById(R.id.fecha);


            ImageView image = (ImageView) vi.findViewById(R.id.image_view);

            //Log.d("array ",names.toString());
            String title=optionsArray.get(position);
            String fecha_row=CountTasks.get(position);

            lblprecio.setText(String.valueOf(title));
            lblfecha.setText(String.valueOf(fecha_row));




            String uri1 = "@android:drawable/ic_menu_today";
            String uri2 = "@drawable/ic_listas";
            String uri3 = "@drawable/ic_check";

            int imageResource1 = getResources().getIdentifier(uri1, null, getPackageName());
            int imageResource2 = getResources().getIdentifier(uri2, null, getPackageName());
            int imageResource3 = getResources().getIdentifier(uri3, null, getPackageName());

            Drawable res1 = getResources().getDrawable(imageResource1);
            Drawable res2 = getResources().getDrawable(imageResource2);
            Drawable res3 = getResources().getDrawable(imageResource3);

            if(title.equals("Hoy")){
                image.setImageDrawable(res1);
            }
            if(title.equals("Mis tareas")){
                image.setImageDrawable(res2);
            }
            if(title.equals("Tareas supervisadas")){
                image.setImageDrawable(res3);
            }




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
            param.add(new BasicNameValuePair("formid", "5"));
            param.add(new BasicNameValuePair("email", useremail));


            try {
                String resulting_json = null;
                ServiceHandler jsonParser = new ServiceHandler();
                resulting_json = jsonParser.makeServiceCall(url_select,
                        ServiceHandler.GET, param);


                JSONArray ja;
                ja = new JSONArray(resulting_json);

                if (ja != null) {
                    for (int i = 0; i < ja.length(); i++) {
                        todayTasks = ja.getJSONObject(i).getString("today_tasks");
                        mytasks = ja.getJSONObject(i).getString("my_tasks");
                        delegatedTasks= ja.getJSONObject(i).getString("delegated_tasks");

                    }
                }
            } catch (Exception jds) {
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            optionsArray.clear();
            CountTasks.clear();

            optionsArray.add("Hoy");
            optionsArray.add("Mis tareas");
            optionsArray.add("Tareas supervisadas");

            CountTasks.add(todayTasks);
            CountTasks.add(mytasks);
            CountTasks.add(delegatedTasks);
            ListView listContent = (ListView)findViewById(R.id.contenttask);
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
                    if (value==0){
                        //Hoy
                        Intent main = new Intent(TaskActivity.this, ListTasksbyType.class);
                        String strName = null;
                        main.putExtra("Tipo", "Tareas para hoy");
                        main.putExtra("id_tipo", "1");
                        startActivity(main);
                    }
                    if(value==1){
                        //Todas mias
                        Intent main = new Intent(TaskActivity.this, ListTasksbyType.class);
                        main.putExtra("Tipo", "Mis Tareas");
                        main.putExtra("id_tipo", "2");
                        startActivity(main);
                    }
                    if(value==2){
                        //Asignadas
                        Intent main = new Intent(TaskActivity.this, ListTasksbyType.class);
                        main.putExtra("Tipo", "Tareas asignadas");
                        main.putExtra("id_tipo", "3");
                        startActivity(main);
                    }
                }
            });
            mAuthTask = null;

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;

        }
    }

    public static String converteStringEmData(String stringData)
            throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/aaaa");
        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date data = sdf.parse(stringData);
        String formattedTime = output.format(data);
        return formattedTime;
    }
    @Override
    public void onResume(){
        super.onResume();

        mAuthTask = new getTodayTask("","");

        mAuthTask.execute((Void) null);
    }
}
