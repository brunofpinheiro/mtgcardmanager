package com.br.mtgcardmanager.DriveBackup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.br.mtgcardmanager.Helper.DatabaseHelper;
import com.br.mtgcardmanager.Model.Card;
import com.br.mtgcardmanager.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class DriveBackupService {
    private       JSONArray       jsonHaveCards;
    private       JSONArray       jsonWantCards;
    private       DatabaseHelper  dbHelper;
    public        GoogleApiClient googleApiClient;
    private       Activity        activity;
    private final Executor        mExecutor = Executors.newSingleThreadExecutor();
    private final Drive           mDriveService;
    private       ProgressDialog  progressDialog;

    public DriveBackupService(Drive driveService) {
        mDriveService = driveService;
    }


    /**
     * Create and/or update the backup files on Google Drive.
     * @param activity
     * @param progressDialog
     */
    public void backupFiles(Activity activity, ProgressDialog progressDialog) {
        exportDbToJSON();

        checkIfBackupExists().addOnSuccessListener(backupExists -> {
            if (backupExists) {
                deleteExistingBackup().addOnSuccessListener(res -> {
                    createDriveFile("have");
                    createDriveFile("want");

                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }

                    Toast.makeText(activity, activity.getString(R.string.backup_completed), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    /**
     * Searches for existing backup files on Google Drive.
     * Returns true if there is a backup, or false otherwise.
     * @return
     */
    private Task<Boolean> checkIfBackupExists() {
        return Tasks.call(mExecutor, () -> {
            FileList driveFiles;

            driveFiles = mDriveService.files().list().execute();

            if (driveFiles.getFiles().size() > 0) {
                return true;
            } else {
                return false;
            }
        });
    }

    /**
     * Creates a json file in the user's root folder and returns its file ID.
     * @param tableName
     * @return
     */
    private Task<String> createDriveFile(String tableName) {
        return Tasks.call(mExecutor, () -> {
            ByteArrayContent contentStream = null;
            File             googleFile    = new File();
            File             metadata;

            try {
                metadata = new File()
                        .setParents(Collections.singletonList("root"))
                        .setMimeType("application/json")
                        .setName("mtgcardmanager_" + tableName + ".json");

                if (tableName.equalsIgnoreCase("have")) {
                    contentStream = ByteArrayContent.fromString("text/plain", jsonHaveCards.toString());
                } else if (tableName.equalsIgnoreCase("want")) {
                    contentStream = ByteArrayContent.fromString("text/plain", jsonWantCards.toString());
                }

                googleFile = mDriveService.files().create(metadata, contentStream).execute();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return googleFile.getId();
        });
    }

    /**
     * Searches for existing backup files and deletes them.
     * @return
     */
    private Task<Void> deleteExistingBackup() {
        return Tasks.call(mExecutor, () -> {
            FileList driveFiles;

            try {
                driveFiles = mDriveService.files().list().execute();

                if (driveFiles.getFiles().size() > 0) {
                    for (int i = 0; i < driveFiles.getFiles().size(); i++) {
                        mDriveService.files().delete(driveFiles.getFiles().get(i).getId()).execute();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        });
    }


    public void restoreBackup(Activity activity) {
        checkIfBackupExists().addOnSuccessListener(res -> {
            if (res) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.exclusionConfirmationDialog);
                builder
                        .setMessage(activity.getString(R.string.backup_found))
                        .setTitle(activity.getString(R.string.atention))
                        .setNegativeButton(activity.getString(R.string.no), (dialog, id) -> {
                            dialog.cancel();
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                        })
                        .setPositiveButton(activity.getString(R.string.yes), (dialog, id) -> {
                            progressDialog = new ProgressDialog(activity, R.style.customProgressDialog);
                            progressDialog.setMessage(activity.getString(R.string.restoring_backup));
                            progressDialog.show();

                            restoreExistingBackup(activity)
                                    .addOnCompleteListener(complete -> {
                                        if (progressDialog != null) {
                                            progressDialog.dismiss();
                                        }
                                    })
                                    .addOnFailureListener(failure -> showRestoreFailureMessage(activity))
                                    .addOnSuccessListener(success -> showRestoreSuccessMessage(activity));
                        })
                        .show();
            }
        });
    }

    private void showRestoreSuccessMessage(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.exclusionConfirmationDialog);
        builder
                .setMessage(activity.getString(R.string.backup_completed))
                .setNegativeButton("OK", (dialog, id) -> dialog.cancel())
                .show();
    }

    private void showRestoreFailureMessage(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.exclusionConfirmationDialog);
        builder
                .setMessage(activity.getString(R.string.backup_failure))
                .setNegativeButton("OK", (dialog, id) -> dialog.cancel())
                .show();
    }


    /**
     * Downloads the backup files from Google Drive, converts them to a json array and inserts
     * each object into it's corresponding table.
     * @return
     */
    private Task<Void> restoreExistingBackup(Activity activity) {
        return Tasks.call(mExecutor, () -> {
            FileList       driveFiles;
            InputStream    is;
            BufferedReader reader;
            StringBuilder  stringBuilder;
            String         line;
            String         content;
            Gson           gson;

            dbHelper   = new DatabaseHelper(activity);
            driveFiles = mDriveService.files().list().execute();

            if (driveFiles.getFiles().size() > 0) {
                for (int i = 0; i < driveFiles.getFiles().size(); i++) {
                    is            = mDriveService.files().get(driveFiles.getFiles().get(i).getId()).executeMediaAsInputStream();
                    reader        = new BufferedReader(new InputStreamReader(is));
                    stringBuilder = new StringBuilder();

                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    content = stringBuilder.toString();
                    gson    = new Gson();

                    if (driveFiles.getFiles().get(i).getName().contains("mtgcardmanager_want")) {
                        Card[] wantCards = gson.fromJson(content, Card[].class);
                        for (Card card : wantCards) {
                            Card wantCard = new Card();

                            wantCard.setId(card.getId());
                            wantCard.setFoil(card.getFoil());
                            wantCard.setId_edition(card.getId_edition());
                            wantCard.setName_en(card.getName_en());
                            wantCard.setName_pt(card.getName_pt());
                            wantCard.setQuantity(card.getQuantity());

                            dbHelper.insertWantCard(wantCard);
                        }
                    } else {
                        Card[] haveCards = gson.fromJson(content, Card[].class);
                        for (Card card : haveCards) {
                            Card haveCard = new Card();

                            haveCard.setId(card.getId());
                            haveCard.setFoil(card.getFoil());
                            haveCard.setId_edition(card.getId_edition());
                            haveCard.setName_en(card.getName_en());
                            haveCard.setName_pt(card.getName_pt());
                            haveCard.setQuantity(card.getQuantity());

                            dbHelper.insertHaveCard(haveCard);
                        }
                    }
                }
            }
            return null;
        });
    }

    private void exportDbToJSON() {
        jsonHaveCards = convertDbToJSON("have");
        jsonWantCards = convertDbToJSON("want");
    }

    private JSONArray convertDbToJSON(String table_name) {
        JSONArray      jsonArray = new JSONArray();
        SQLiteDatabase myDataBase;
        String         searchQuery;
        Cursor         cursor;
        int            totalColumn;
        JSONObject     rowObject;

        dbHelper    = DatabaseHelper.getInstance(activity);
        myDataBase  = dbHelper.getReadableDatabase();
        searchQuery = "SELECT  * FROM " + table_name;
        cursor      = myDataBase.rawQuery(searchQuery, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            totalColumn = cursor.getColumnCount();
            rowObject   = new JSONObject();
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
            jsonArray.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();

        return jsonArray;
    }
}