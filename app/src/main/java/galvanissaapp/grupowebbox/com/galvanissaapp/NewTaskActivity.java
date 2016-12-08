package galvanissaapp.grupowebbox.com.galvanissaapp;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class NewTaskActivity extends AppCompatActivity {

    EditText duedate=null;
    Spinner spinner =null;
    private SQLiteAdapter mySQLiteAdapter;
    String upLoadServerUri = "http://galvanissa.lmendoza.info/backend/webroot/Mobile/upload.php";
    String myArticleUrl = "http://galvanissa.lmendoza.info/backend/webroot/Mobile/service.php";

    private ImageView imageView;

    String asignedTo="";
    //TextView messageText;

    int serverResponseCode = 0;
    ProgressDialog dialog = null;

     String uploadFilePath = "";
     String uploadFileName = "";
    final int PERMISSION_REQUEST_CODE=10;
    final int PERMISSION_REQUEST_CODEI=100;

    SharedPreferences sp;
    SharedPreferences.Editor spe;
    String filenameFinal="no";

    private saveTasktoDB mAuthTask = null;
    String useremail ="";
    String str_descr="";
    String str_title="";
    String str_duedate="";
    String formatedDateMYSQL="";
    EditText et_name=null;
    EditText et_descr=null;
    EditText et_duedate=null;
    Boolean isImageAttached=false;

    private static final String TEMP_IMAGE_NAME = "tempImage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImagePickerCustom.setMinQuality(600, 600);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewTaskActivity.this.onPickImage(NewTaskActivity.this.findViewById(R.id.imageView3));
            }
        });


    this.setTitle("Nueva tarea");

        imageView = (ImageView) findViewById(R.id.imageView3);


        mySQLiteAdapter = new SQLiteAdapter(this);
        mySQLiteAdapter.openToWrite();

        mySQLiteAdapter.insertSubordinate("Tarea propia","0");



        duedate=(EditText) this.findViewById(R.id.TaskDueDate);


        duedate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR);
                    int mMonth = c.get(Calendar.MONTH);
                    int mDay = c.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog dialog = new DatePickerDialog(NewTaskActivity.this,new mDateSetListener(), mYear, mMonth, mDay);
                    dialog.show();
                }else {
                    //Toast.makeText(getApplicationContext(), "lost the focus", Toast.LENGTH_LONG).show();
                }
            }
        });

        duedate.setInputType(InputType.TYPE_NULL);
        duedate.setTextIsSelectable(true);

    spinner=(Spinner)this.findViewById(R.id.TaskAssignedTo);


    this.loadSpinnerData();


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


        et_name=(EditText)this.findViewById(R.id.TaskName);
        et_descr=(EditText)this.findViewById(R.id.TaskComment);
        et_duedate=(EditText)this.findViewById(R.id.TaskDueDate);


        BootstrapButton button = (BootstrapButton) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {

                str_descr=et_descr.getText().toString();
                str_title=et_name.getText().toString();
                str_duedate=et_duedate.getTag().toString();
                String text = spinner.getSelectedItem().toString();


                SQLiteAdapter db = new SQLiteAdapter(getApplicationContext());
                String Empid=db.EmployeeID(text);
                Log.d("ASIGNADO A",Empid);
                if(Empid.equals("0")){
                    String cid=sp.getString("user_id","");
                    asignedTo=cid;
                }
                else{
                    asignedTo=Empid;
                }

try {
    str_duedate = converteStringEmData(str_duedate);
}catch(Exception s){

}               if(isImageAttached){
                    saveData();
                }
                else{
                mAuthTask = new saveTasktoDB("","");

                mAuthTask.execute((Void) null);
                }







            }


        });
        sp = getSharedPreferences("sp",MODE_PRIVATE);
        useremail =sp.getString("user_email", "");
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(NewTaskActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    private boolean checkPermissionI() {
        int result = ContextCompat.checkSelfPermission(NewTaskActivity.this, Manifest.permission.INTERNET);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermissionI() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(NewTaskActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(NewTaskActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(NewTaskActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODEI);
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(NewTaskActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(NewTaskActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(NewTaskActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
            case PERMISSION_REQUEST_CODEI:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        String path="";

        Bitmap bitmap = ImagePickerCustom.getImageFromResult(this, requestCode, resultCode, data);

        String selectedImagePath = MediaStore.Images.Media.insertImage(
                getContentResolver(),
                bitmap,
                "title",
                "description"
        );

//        Log.d("path",selectedImagePath);

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            imageView.setAlpha(200);



            /*test upload start*/
            try{

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                Log.d("paths",picturePath);
                path=picturePath;
                isImageAttached=true;
            }
            catch(Exception l){
                Log.d("paths",l.toString());
            // picture taken


                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                Uri tempUri = getImageUri(getApplicationContext(), bitmap);

                // CALL THIS METHOD TO GET THE ACTUAL PATH
                File finalFile = new File(getRealPathFromURI(tempUri));

                path=finalFile.getPath().toString();
                Log.d("TOMADA",finalFile.getPath().toString());
                isImageAttached=true;
            }


            /*test upload end*/
        }
        // TODO do something with the bitmap

        uploadFilePath=path;
        String filename=path.substring(path.lastIndexOf("/")+1);
        uploadFileName=filename;

    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public void onPickImage(View view) {
        // Click on image button
        ImagePickerCustom.pickImage(this, "Agregar imagen/foto");


    }

    private void loadSpinnerData() {
        // database handler
        SQLiteAdapter db = new SQLiteAdapter(getApplicationContext());

        // Spinner Drop down elements
        List<String> lables = db.getSubordinates();

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, lables);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
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

            duedate.setText(""+formatedDate+"");
            duedate.setTag(formatedDateMYSQL);

        }
    }



    /* upload file start*/
    public void saveData(){
        upLoadServerUri = "http://galvanissa.lmendoza.info/backend/webroot/Mobile/upload.php";


        dialog = ProgressDialog.show(NewTaskActivity.this, "", "Subiendo adjunto...", true);

        new Thread(new Runnable() {
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        //messageText.setText("uploading started.....");
                    }
                });
                Log.d("Intentando subir:",uploadFilePath);
                uploadFile(uploadFilePath);

            }
        }).start();

    }




    public int uploadFile(String sourceFileUri) {


        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {

            dialog.dismiss();

            Log.e("uploadFile", "Source File not exist :"
                    +uploadFilePath);

            runOnUiThread(new Runnable() {
                public void run() {
//                    messageText.setText("Source File not exist :"
//                            +uploadFilePath);
                }
            });

            return 0;

        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL("http://galvanissa.lmendoza.info/backend/webroot/Mobile/upload.php");
                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);



                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);



                Calendar c = Calendar.getInstance();
                int seconds = c.get(Calendar.SECOND);
                int ml =c.get(Calendar.MILLISECOND);
                String idcuenta=sp.getString("user_id", "");


                String fl=idcuenta+"_"+String.valueOf(seconds)+String.valueOf(ml)+".jpg";
                filenameFinal=fl;
                Log.d("Pruebas",fl.toString());


                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename="+ ""+  fl.toString()   + "" + lineEnd);
                dos.writeBytes(lineEnd);


                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){

                    runOnUiThread(new Runnable() {
                        public void run() {

                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                    +" http://www.androidexample.com/media/uploads/"
                                    +uploadFileName;

                         //   messageText.setText(msg);
                            Toast.makeText(NewTaskActivity.this, "Archivo enviado correctamente",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });


                    mAuthTask = new saveTasktoDB("","");

                    mAuthTask.execute((Void) null);

                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                       // messageText.setText("MalformedURLException Exception : check script url.");
                        Toast.makeText(NewTaskActivity.this, "MalformedURLException",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                      //  messageText.setText("Got Exception : see logcat ");
                        Toast.makeText(NewTaskActivity.this, "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload  server Exn", "Exception : "
                        + e.getMessage(), e);
            }
            dialog.dismiss();
            return serverResponseCode;

        } // End else block
    }
    /*upload file end*/






    /*create task */
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
            param.add(new BasicNameValuePair("formid", "4"));
            param.add(new BasicNameValuePair("email", useremail));
            param.add(new BasicNameValuePair("to", asignedTo));
            param.add(new BasicNameValuePair("descr", str_descr));
            param.add(new BasicNameValuePair("title", str_title));
            param.add(new BasicNameValuePair("duedate", str_duedate));

                param.add(new BasicNameValuePair("filename", filenameFinal));


            //filename
            Log.d("Parametros",param.toString());
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
            Toast.makeText(NewTaskActivity.this, "Tarea guardada exitosamente!", Toast.LENGTH_LONG).show();
            finish();
            mAuthTask = null;

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;

        }
    }

    public static String converteStringEmData(String stringData)
            throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/aaaa");//yyyy-MM-dd'T'HH:mm:ss
        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date data = sdf.parse(stringData);
        String formattedTime = output.format(data);
        return formattedTime;
    }

    /*create task end*/
}
