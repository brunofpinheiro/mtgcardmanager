package com.br.mtgcardmanager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.br.mtgcardmanager.Helper.DatabaseHelper;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.content.ContentValues.TAG;


public class DriveBackupService {
    private JSONArray       jsonHaveCards;
    private JSONArray       jsonWantCards;
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
    private final Executor  mExecutor = Executors.newSingleThreadExecutor();
    private final Drive     mDriveService;

    public DriveBackupService(Drive driveService) {
        mDriveService = driveService;
    }


    /**
     * Create the backup files on Google Drive.
     * @param activity
     * @param progressDialog
     */
    public void createFiles(Activity activity, ProgressDialog progressDialog) {
        exportDbToJSON();
        //TODO verificar se os arquivos já existem antes de subir de novo
        createDriveFile("have").addOnFailureListener(exception ->
                Log.e(TAG, "Não foi possível fazer o backup da tabela Have", exception));
        createDriveFile("want").addOnFailureListener(exception ->
                Log.e(TAG, "Não foi possível fazer o backup da tabela Want", exception));

        if (progressDialog != null)
            progressDialog.dismiss();

        Toast.makeText(activity, activity.getString(R.string.backup_completed), Toast.LENGTH_SHORT).show();
    }

    /**
     * Creates a json file in the user's root folder and returns its file ID.
     * @param table_to_backup
     * @return
     */
    private Task<String> createDriveFile(String table_to_backup) {
        return Tasks.call(mExecutor, () -> {
            ByteArrayContent contentStream = null;
            File googleFile;
            File metadata;

            metadata = new File()
                    .setParents(Collections.singletonList("root"))
                    .setMimeType("application/json")
                    .setName("mtgcardmanager_ " + table_to_backup + ".json");

            if (table_to_backup.equalsIgnoreCase("have")) {
                contentStream = ByteArrayContent.fromString("text/plain", jsonHaveCards.toString());
            } else if (table_to_backup.equalsIgnoreCase("want")) {
                contentStream = ByteArrayContent.fromString("text/plain", jsonWantCards.toString());
            }

            googleFile = mDriveService.files().create(metadata, contentStream).execute();

            if (googleFile == null) {
                throw new IOException("Retorno nulo ao requisitar a criação do arquivo.");
            }

            return googleFile.getId();
        });
    }

    /**
     * This is Result result handler of Drive contents.
     * this callback method call CreateFileOnGoogleDrive() method.
     */
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

    /**
     * Create a file in root folder using MetadataChangeSet object.
     *
     * @param result
     */
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


    /**
     * Handle result of Created file
     */
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
        jsonHaveCards = convertDbToJSON("have");
        jsonWantCards = convertDbToJSON("want");
    }

    private JSONArray convertDbToJSON(String table_name) {
        JSONArray resultSet = new JSONArray();

        db_helper = DatabaseHelper.getInstance(activity);
        SQLiteDatabase myDataBase = db_helper.getReadableDatabase();
        String searchQuery = "SELECT  * FROM " + table_name;
        Cursor cursor = myDataBase.rawQuery(searchQuery, null);

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        if (cursor.getString(i) != null) {
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