package galvanissaapp.grupowebbox.com.galvanissaapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LocationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent myIntent = new Intent(context, TrackLocationService.class);
        context.startService(myIntent);

    }
}