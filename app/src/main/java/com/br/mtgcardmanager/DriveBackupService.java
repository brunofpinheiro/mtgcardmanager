package com.br.mtgcardmanager;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.br.mtgcardmanager.Helper.DatabaseHelper;
import com.br.mtgcardmanager.View.MainActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;


/**
 * Created by Bruno on 21/12/2016.
 */

public class DriveBackupService extends IntentService {
    private JSONArray           json_have_cards;
    private JSONArray           json_want_cards;
    private DatabaseHelper      db_helper;
    private int                 notification_number         = 0;
    private static final String DRIVE_TAG                   = "Google Drive Activity";
    private boolean             is_table_want_ready_for_bkp = true;
    private boolean             file_operation              = false;
    private String              table_to_backup             = "";
    public static GoogleApiClient mGoogleApiClient;

    public DriveBackupService() {
        super("DriveBackupService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("DriveBackupService", "Executando backup");
        Boolean running = intent.getBooleanExtra("is_running", false);
        mGoogleApiClient = MainActivity.mGoogleApiClient;

        if (running.equals("true")) {
            createHaveDriveFile();
            createNotification();
        } else {
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.getApplicationContext());

                mBuilder.setSmallIcon(R.mipmap.ic_notification_white);
                mBuilder.setContentTitle(getString(R.string.notification_title));
                mBuilder.setContentText("running: " + running.toString());

                NotificationManager mNotificationManager = (NotificationManager)
                        this.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notification_number = notification_number + 1;
                mNotificationManager.notify(notification_number, mBuilder.build());
        }
    }

    public void createHaveDriveFile() {
        file_operation = true;
        table_to_backup = "have";
        // create new contents resource
        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(driveContentsCallback);
    }

    public void createWantDriveFile() {
        file_operation = true;
        table_to_backup = "want";
        // create new contents resource
        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(driveContentsCallback);
    }

    /**
     * This is Result result handler of Drive contents.
     * this callback method call CreateFileOnGoogleDrive() method.
     */
    final ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (result.getStatus().isSuccess()) {
                        if (file_operation == true) {
                            CreateFileOnGoogleDrive(result, table_to_backup);
                        }
                    }
                }
            };

    /**
     * Create a file in root folder using MetadataChangeSet object.
     *
     * @param result
     */
    public void CreateFileOnGoogleDrive(DriveApi.DriveContentsResult result, final String table_to_backup) {
        // Exports the database to a JSONArray
        exportDbToJSON();

        final DriveContents driveContents = result.getDriveContents();

        // Perform I/O off the UI thread.
        new Thread() {
            @Override
            public void run() {
                writeContentToDriveContents(driveContents, table_to_backup);
            }
        }.start();
    }

    private void writeContentToDriveContents(DriveContents driveContents, String table_to_backup) {
        OutputStream outputStream = driveContents.getOutputStream();
        Writer writer = new OutputStreamWriter(outputStream);
        try {
            if (table_to_backup.equals("have")) {
                writer.write(json_have_cards.toString());
            } else if (table_to_backup.equals("want")) {
                writer.write(json_want_cards.toString());
            }
            writer.close();
        } catch (IOException e) {
            Log.e(DRIVE_TAG, e.getMessage());
        }

        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle("mtgcardmanager_" + table_to_backup + ".json")
                .setMimeType("application/json")
                .setStarred(true).build();

        // create a file in root folder
        Drive.DriveApi.getRootFolder(MainActivity.mGoogleApiClient)
                .createFile(MainActivity.mGoogleApiClient, changeSet, driveContents).
                setResultCallback(fileCallback);
    }


    /**
     * Handle result of Created file
     */
    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (result.getStatus().isSuccess()) {
//                        Toast.makeText(getApplicationContext(), "file created: " + "" +
//                                result.getDriveFile().getDriveId(), Toast.LENGTH_LONG).show();
                        if (is_table_want_ready_for_bkp) {
                            createWantDriveFile();
                            is_table_want_ready_for_bkp = false;
                        }
                    }
                    return;
                }
            };


    public void exportDbToJSON () {
        json_have_cards = convertDbToJSON("have");
        json_want_cards = convertDbToJSON("want");
    }

    private JSONArray convertDbToJSON(String table_name) {
        String myTable      = table_name;//Set name of your table
        JSONArray resultSet = new JSONArray();

        db_helper = DatabaseHelper.getInstance(this);
        SQLiteDatabase myDataBase = db_helper.getReadableDatabase();
        String searchQuery = "SELECT  * FROM " + myTable;
        Cursor cursor = myDataBase.rawQuery(searchQuery, null );

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for( int i=0 ;  i< totalColumn ; i++ ) {
                if( cursor.getColumnName(i) != null ) {
                    try {
                        if( cursor.getString(i) != null ) {
//                            Log.d("TAG_NAME", cursor.getString(i) );
                            rowObject.put(cursor.getColumnName(i) ,  cursor.getString(i) );
                        } else {
                            rowObject.put( cursor.getColumnName(i) ,  "" );
                        }
                    } catch( Exception e ) {
                        Log.d("TAG_NAME", e.getMessage()  );
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        Log.d("TAG_NAME", resultSet.toString() );

        return resultSet;
    }

    private void createNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.getApplicationContext());

        mBuilder.setSmallIcon(R.mipmap.ic_notification_white);
        mBuilder.setContentTitle(getString(R.string.notification_title));
        mBuilder.setContentText(getString(R.string.backup_completed));

        NotificationManager mNotificationManager = (NotificationManager)
                this.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notification_number = notification_number + 1;
        mNotificationManager.notify(notification_number, mBuilder.build());
    }
}