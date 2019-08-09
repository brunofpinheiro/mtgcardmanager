package com.br.mtgcardmanager;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.br.mtgcardmanager.Helper.DatabaseHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.android.volley.VolleyLog.TAG;


/**
 * Created by Bruno on 21/12/2016.
 */

//public class DriveBackupService extends IntentService implements GoogleApiClient.ConnectionCallbacks,
//        GoogleApiClient.OnConnectionFailedListener {
public class DriveBackupService {
    private JSONArray       json_have_cards;
    private JSONArray       json_want_cards;
    private DatabaseHelper  db_helper;
    private int             notification_number = 0;
    private final String    DRIVE_TAG = "Google Drive Activity";
    private boolean         is_table_want_ready_for_bkp = true;
    private boolean         file_operation = false;
    private String          table_to_backup = "";
    public  GoogleApiClient googleApiClient;
    private final int       REQUEST_CODE_RESOLUTION = 1;
    private final int       REQUEST_CODE_SIGN_IN = 1;
    private final int       REQUEST_CODE_OPEN_DOCUMENT = 2;
    private Activity        activity;

    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private final Drive mDriveService;

    public DriveBackupService(Drive driveService) {
        mDriveService = driveService;
    }

//    public DriveBackupService(Activity activity) {
////        super("DriveBackupService");
//        this.activity = activity;
//    }

//    /**
//     * Starts a sign-in activity using {@link #REQUEST_CODE_SIGN_IN}.
//     */
//    public void connect() {
//        if (googleApiClient == null) {
//            Log.d(TAG, "Requesting sign-in");
//
//            GoogleSignInOptions signInOptions =
//                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                            .requestEmail()
//                            .requestScopes(new Scope(DriveScopes.DRIVE_APPDATA))
//                            .build();
//            GoogleSignInClient client = GoogleSignIn.getClient(activity, signInOptions);
//
//            // The result of the sign-in Intent is handled in onActivityResult.
//            startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
////            googleApiClient = new GoogleApiClient.Builder(activity)
////                    .addApi(Drive.API)
////                    .addScope(Drive.SCOPE_APPFOLDER)
////                    .addConnectionCallbacks(this)
////                    .addOnConnectionFailedListener(this)
////                    .build();
//        }
//
//        googleApiClient.connect();
//    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
//        switch (requestCode) {
//            case REQUEST_CODE_SIGN_IN:
//                if (resultCode == Activity.RESULT_OK && resultData != null) {
//                    handleSignInResult(resultData);
//                }
//                break;
//
//            case REQUEST_CODE_OPEN_DOCUMENT:
//                if (resultCode == Activity.RESULT_OK && resultData != null) {
//                    Uri uri = resultData.getData();
//                    if (uri != null) {
//                        openFileFromFilePicker(uri);
//                    }
//                }
//                break;
//        }
//
//        super.onActivityResult(requestCode, resultCode, resultData);
//    }

//    /**
//     * Handles the {@code result} of a completed sign-in activity initiated from {@link
//     * #requestSignIn()}.
//     */
//    private void handleSignInResult(Intent result) {
//        GoogleSignIn.getSignedInAccountFromIntent(result)
//                .addOnSuccessListener(googleAccount -> {
//                    Log.d(TAG, "Signed in as " + googleAccount.getEmail());
//
//                    // Use the authenticated account to sign in to the Drive service.
//                    GoogleAccountCredential credential =
//                            GoogleAccountCredential.usingOAuth2(
//                                    this, Collections.singleton(DriveScopes.DRIVE_FILE));
//                    credential.setSelectedAccount(googleAccount.getAccount());
//                    Drive googleDriveService =
//                            new Drive.Builder(
//                                    AndroidHttp.newCompatibleTransport(),
//                                    new GsonFactory(),
//                                    credential)
//                                    .setApplicationName("Drive API Migration")
//                                    .build();
//
//                    // The DriveServiceHelper encapsulates all REST API and SAF functionality.
//                    // Its instantiation is required before handling any onClick actions.
//                    mDriveServiceHelper = new DriveServiceHelper(googleDriveService);
//                })
//                .addOnFailureListener(exception -> Log.e(TAG, "Unable to sign in.", exception));
//    }

//    @Override
//    public void onConnected(Bundle connectionHint) {
//        Log.i(DRIVE_TAG, "----- GoogleApiClient connected ----- ");
//        Toast.makeText(activity, "Connected", Toast.LENGTH_LONG).show();
////        scheduleAlarm();
//        createHaveDriveFile();
//        createWantDriveFile();
//    }

//    @Override
//    public void onConnectionFailed(ConnectionResult result) {
//        // Called whenever the API client fails to connect.
//        Log.i(DRIVE_TAG, "----- GoogleApiClient connection failed: " + result.toString() + " ----- ");
//
//        if (!result.hasResolution()) {
//            GoogleApiAvailability.getInstance().getErrorDialog(activity, result.getErrorCode(), 0).show();
//            return;
//        }
//
//        /**
//         *  The failure has a resolution. Resolve it.
//         *  Called typically when the app is not yet authorized, and an authorization
//         *  dialog is displayed to the user.
//         */
//        try {
//            result.startResolutionForResult(activity, REQUEST_CODE_RESOLUTION);
//        } catch (IntentSender.SendIntentException e) {
//            Log.e(TAG, "Exception while starting resolution activity", e);
//        }
//    }

//    /**
//     * It invoked when connection suspended
//     *
//     * @param cause
//     */
//    @Override
//    public void onConnectionSuspended(int cause) {
//        Log.i(DRIVE_TAG, "----- GoogleApiClient connection suspended -----");
////        cancelAlarm();
//    }

//    @Override
//    protected void onHandleIntent(Intent intent) {
//        Log.i("DriveBackupService", "Executando backup");
//        Boolean running = intent.getBooleanExtra("is_running", false);
//
//        if (running.equals("true")) {
//            createHaveDriveFile();
//            createNotification();
//        } else {
//            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity);
//
//            mBuilder.setSmallIcon(R.mipmap.ic_notification_white);
//            mBuilder.setContentTitle(getString(R.string.notification_title));
//            mBuilder.setContentText("running: " + running.toString());
//
//            NotificationManager mNotificationManager = (NotificationManager)
//                    this.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//            notification_number = notification_number + 1;
//            mNotificationManager.notify(notification_number, mBuilder.build());
//        }
//    }


//    public void createHaveDriveFile() {
//        connect();
//        file_operation = true;
//        table_to_backup = "have";
////        // create new contents resource
////        Drive.DriveApi.newDriveContents(googleApiClient).setResultCallback(driveContentsCallback);
//    }
//
//    public void createWantDriveFile() {
//        connect();
//        file_operation = true;
//        table_to_backup = "want";
//        // create new contents resource
////        Drive.DriveApi.newDriveContents(googleApiClient).setResultCallback(driveContentsCallback);
//    }

//    /**
//     * This is Result result handler of Drive contents.
//     * this callback method call CreateFileOnGoogleDrive() method.
//     */
//    final ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
//            new ResultCallback<DriveApi.DriveContentsResult>() {
//                @Override
//                public void onResult(DriveApi.DriveContentsResult result) {
//                    if (result.getStatus().isSuccess()) {
//                        if (file_operation == true) {
//                            CreateFileOnGoogleDrive(result, table_to_backup);
//                        }
//                    }
//                }
//            };

//    /**
//     * Create a file in root folder using MetadataChangeSet object.
//     *
//     * @param result
//     */
//    public void CreateFileOnGoogleDrive(DriveApi.DriveContentsResult result, final String table_to_backup) {
//        // Exports the database to a JSONArray
//        exportDbToJSON();
//
//        final DriveContents driveContents = result.getDriveContents();
//
//        // Perform I/O off the UI thread.
//        new Thread() {
//            @Override
//            public void run() {
//                writeContentToDriveContents(driveContents, table_to_backup);
//            }
//        }.start();
//    }
//
//    private void writeContentToDriveContents(DriveContents driveContents, String table_to_backup) {
//        OutputStream outputStream = driveContents.getOutputStream();
//        Writer       writer       = new OutputStreamWriter(outputStream);
//        try {
//            if (table_to_backup.equalsIgnoreCase("have")) {
//                writer.write(json_have_cards.toString());
//            } else if (table_to_backup.equalsIgnoreCase("want")) {
//                writer.write(json_want_cards.toString());
//            }
//            writer.close();
//        } catch (IOException e) {
//            Log.e(DRIVE_TAG, e.getMessage());
//        }
//
//        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
//                .setTitle("mtgcardmanager_" + table_to_backup + ".json")
//                .setMimeType("application/json")
//                .setStarred(true).build();
//
//        // create a file in root folder
//        Drive.DriveApi.getRootFolder(googleApiClient)
//                .createFile(googleApiClient, changeSet, driveContents).
//                setResultCallback(fileCallback);
//    }
//
//
//    /**
//     * Handle result of Created file
//     */
//    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
//            ResultCallback<DriveFolder.DriveFileResult>() {
//                @Override
//                public void onResult(DriveFolder.DriveFileResult result) {
//                    if (result.getStatus().isSuccess()) {
////                        Toast.makeText(getApplicationContext(), "file created: " + "" +
////                                result.getDriveFile().getDriveId(), Toast.LENGTH_LONG).show();
//                        if (is_table_want_ready_for_bkp) {
//                            createWantDriveFile();
//                            is_table_want_ready_for_bkp = false;
//                        }
//                    }
//                    return;
//                }
//            };


    public void exportDbToJSON() {
        json_have_cards = convertDbToJSON("have");
        json_want_cards = convertDbToJSON("want");
    }

    private JSONArray convertDbToJSON(String table_name) {
        String myTable = table_name;//Set name of your table
        JSONArray resultSet = new JSONArray();

        db_helper = DatabaseHelper.getInstance(activity);
        SQLiteDatabase myDataBase = db_helper.getReadableDatabase();
        String searchQuery = "SELECT  * FROM " + myTable;
        Cursor cursor = myDataBase.rawQuery(searchQuery, null);

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        if (cursor.getString(i) != null) {
//                            Log.d("TAG_NAME", cursor.getString(i) );
                            rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                        } else {
                            rowObject.put(cursor.getColumnName(i), "");
                        }
                    } catch (Exception e) {
                        Log.d("TAG_NAME", e.getMessage());
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        Log.d("TAG_NAME", resultSet.toString());

        return resultSet;
    }

//    private void createNotification() {
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity);
//
//        mBuilder.setSmallIcon(R.mipmap.ic_notification_white);
//        mBuilder.setContentTitle(getString(R.string.notification_title));
//        mBuilder.setContentText(getString(R.string.backup_completed));
//
//        NotificationManager mNotificationManager = (NotificationManager)
//                activity.getSystemService(Context.NOTIFICATION_SERVICE);
//        notification_number = notification_number + 1;
//        mNotificationManager.notify(notification_number, mBuilder.build());
//    }
}