package com.br.mtgcardmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.br.mtgcardmanager.View.MainActivity;

/**
 * Created by Bruno on 21/12/2016.
 */

public class AlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "com.br.mtgcardmanager";

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, DriveBackupService.class);
        i.putExtra("is_running", MainActivity.running);
        context.startService(i);
    }
}
