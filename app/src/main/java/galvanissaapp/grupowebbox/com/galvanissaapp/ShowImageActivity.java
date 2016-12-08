package galvanissaapp.grupowebbox.com.galvanissaapp;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;

public class ShowImageActivity extends AppCompatActivity {
    Button load_img;
    ImageView img;
    Bitmap bitmap;
    ProgressDialog pDialog;
    SharedPreferences sp;
    SharedPreferences.Editor spe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        this.setTitle("Adjunto");
        sp = getSharedPreferences("sp",MODE_PRIVATE);
        String namestr =sp.getString("imagename", "");
        img = (ImageView)findViewById(R.id.imageView4);
        new LoadImage().execute("http://galvanissa.lmendoza.info/backend/webroot/Mobile/tbltasks/task_imgname/"+namestr);
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ShowImageActivity.this);
            pDialog.setMessage("Cargando adjunto ....");
            pDialog.show();

        }
        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {

            if(image != null){
                img.setImageBitmap(image);
                pDialog.dismiss();

            }else{

                pDialog.dismiss();
                Toast.makeText(ShowImageActivity.this, "El adjunto no se pudo cargar", Toast.LENGTH_SHORT).show();

            }
        }
    }
}
