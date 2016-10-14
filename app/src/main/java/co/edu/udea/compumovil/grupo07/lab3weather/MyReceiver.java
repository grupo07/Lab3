package co.edu.udea.compumovil.grupo07.lab3weather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Intent service = new Intent(context,  MyWidgetProvider.class);
        context.startService(service);

        Toast.makeText(context, "Se actualizó clima", Toast.LENGTH_LONG).show();
        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
