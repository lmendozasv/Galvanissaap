package galvanissaapp.grupowebbox.com.galvanissaapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

//import android.widget.SimpleCursorAdapter;

public class ListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , OnItemClickListener{

    private SQLiteAdapter mySQLiteAdapter;
    AdaptadorLista ma ;
    Cursor cursor =null;
    ArrayList<String> titulo = new ArrayList<String>();
    ArrayList<String> contenido = new ArrayList<String>();
    ArrayList<String> fecha = new ArrayList<String>();
    ArrayList<String> hora = new ArrayList<String>();
    SharedPreferences sp;
    SharedPreferences.Editor spe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Agregar una lista aun no está disponible", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //start
        ListView listContent = (ListView)findViewById(R.id.contentlist);

      /*
       *  Create/Open a SQLite database
       *  and fill with dummy content
       *  and close it
       */
        mySQLiteAdapter = new SQLiteAdapter(this);
        mySQLiteAdapter.openToWrite();
        mySQLiteAdapter.deleteAll();

        mySQLiteAdapter.insert("Arqueo de caja","CONTENIDO","01-01-2015","20:43");
        mySQLiteAdapter.insert("Inventario selectivo","CONTENIDO","01-01-2015","20:43");
        mySQLiteAdapter.insert("Reporte de supervisor","CONTENIDO","01-01-2015","20:43");


        mySQLiteAdapter.close();


        mySQLiteAdapter = new SQLiteAdapter(this);
        mySQLiteAdapter.openToRead();

        cursor= mySQLiteAdapter.queueAll();
        startManagingCursor(cursor);


        cursor.moveToFirst();

        while(!cursor.isAfterLast()) {
            titulo.add(cursor.getString(cursor.getColumnIndex("titulo")));
            contenido.add(cursor.getString(cursor.getColumnIndex("contenido")));
            fecha.add(cursor.getString(cursor.getColumnIndex("fecha")));
            hora.add(cursor.getString(cursor.getColumnIndex("hora")));
            cursor.moveToNext();
        }
        cursor.close();
        contenido.toArray(new String[contenido.size()]);

        ma = new AdaptadorLista();

        listContent.setAdapter(ma);

        mySQLiteAdapter.close();
        //end
        this.setTitle("Listas");

        /*Drawer header values start*/



        TextView tvposition_name =null;
        sp = getSharedPreferences("sp",MODE_PRIVATE);
        String namestr =sp.getString("user_name_drawer", "");
        String position =sp.getString("user_position", "");

        View header = ((NavigationView)findViewById(R.id.nav_view)).getHeaderView(0);
        ((TextView) header.findViewById(R.id.nombreperfil)).setText(namestr);
        ((TextView) header.findViewById(R.id.positionname)).setText(position);


        /*Drawer heade valeus end*/
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
            i = new Intent(ListActivity.this, MainActivity.class);
        } else if (id == R.id.nav_gallery) {
            i = new Intent(ListActivity.this, ListActivity.class);
        } else if (id == R.id.nav_slideshow) {
            i = new Intent(ListActivity.this, TaskActivity.class);
        } else if (id == R.id.nav_manage) {
            i = new Intent(ListActivity.this, MapsActivity.class);
        } else if (id == R.id.nav_share) {
            i = new Intent(ListActivity.this, MyProfileActivity.class);
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
            mCheckStates = new SparseBooleanArray(cursor.getCount());
            mInflater = (LayoutInflater)ListActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return cursor.getCount();
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
                vi = mInflater.inflate(R.layout.row_list, null);
	            lblprecio= (TextView) vi.findViewById(R.id.title_row);
                lblfecha= (TextView) vi.findViewById(R.id.fecha);
            txt_hora= (TextView) vi.findViewById(R.id.txt_hora);

            ImageView image = (ImageView) vi.findViewById(R.id.image_view);

            //Log.d("array ",names.toString());
                String title=titulo.get(position);
                String fecha_row=fecha.get(position);

                lblprecio.setText(String.valueOf(title));
                lblfecha.setText(String.valueOf(fecha_row));


            String myDate = "2-5-2015";
            String myTime = "20:43";
            String toParse = myDate + " " + myTime; // Results in "2-5-2012 20:43"

            Calendar calendar = Calendar.getInstance();

            long startTime = calendar.getTimeInMillis();
            CharSequence lahora=null;
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("d-M-yyyy hh:mm"); // I assume d-M, you may refer to M-d for month-day instead.
                Date date = formatter.parse(toParse); // You will need try/catch around this
                long millis = date.getTime();
                lahora=DateUtils.getRelativeTimeSpanString(millis, startTime,DateUtils.MINUTE_IN_MILLIS);
                System.out.println(millis);
            } catch (ParseException pe) {
                pe.printStackTrace();
            }


            txt_hora.setText(String.valueOf(String.valueOf(lahora)));

            //Random rand = new Random();
            //int r = rand.nextInt(150);
            //int g = rand.nextInt(150);
            //int b = rand.nextInt(150);

            //int randomColor = Color.rgb(r,g,b);


            char first = title.charAt(0);
                drawable = TextDrawable.builder().buildRound(String.valueOf(first), Color.BLACK
                );
            image.setImageDrawable(drawable);


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
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        Toast.makeText(ListActivity.this, "C",Toast.LENGTH_SHORT).show();
    }
}
