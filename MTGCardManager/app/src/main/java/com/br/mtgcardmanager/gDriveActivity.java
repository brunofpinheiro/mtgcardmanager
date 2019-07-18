//package com.br.mtgcardmanager;
//
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.IntentSender;
//import android.os.Bundle;
//import android.support.v4.app.DialogFragment;
//import android.util.Log;
//import android.webkit.MimeTypeMap;
//
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GooglePlayServicesUtil;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.ResultCallback;
//import com.google.android.gms.common.api.Status;
//import com.google.android.gms.drive.Drive;
//import com.google.android.gms.drive.DriveApi;
//import com.google.android.gms.drive.DriveContents;
//import com.google.android.gms.drive.DriveFile;
//import com.google.android.gms.drive.DriveFolder;
//import com.google.android.gms.drive.MetadataChangeSet;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//
///**
// * Created by BRUNO PINHEIRO on 01/11/2016.
// */
//
//public class gDriveActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
//
//    private static final String TAG = "MainActivity";
//    private GoogleApiClient api;
//    private boolean mResolvingError = false;
//    private DriveFile mfile;
//    private static final int DIALOG_ERROR_CODE =100;
//    private static final String DATABASE_NAME = "demodb";
//    private static final String GOOGLE_DRIVE_FILE_NAME = "sqlite_db_backup";
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        // Create the Drive API instance
//        api = new GoogleApiClient.Builder(this).addApi(Drive.API).addScope(Drive.SCOPE_FILE).
//                addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        if(!mResolvingError) {
//            api.connect(); // Connect the client to Google Drive
//        }
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        api.disconnect(); // Disconnect the client from Google Drive
//    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult result) {
//        Log.v(TAG, "Connection failed");
//        if(mResolvingError) { // If already in resolution state, just return.
//            return;
//        } else if(result.hasResolution()) { // Error can be resolved by starting an intent with user interaction
//            mResolvingError = true;
//            try {
//                result.startResolutionForResult(this, DIALOG_ERROR_CODE);
//            } catch (IntentSender.SendIntentException e) {
//                e.printStackTrace();
//            }
//        } else { // Error cannot be resolved. Display Error Dialog stating the reason if possible.
//            ErrorDialogFragment fragment = new ErrorDialogFragment();
//            Bundle args = new Bundle();
//            args.putInt("error", result.getErrorCode());
//            fragment.setArguments(args);
//            fragment.show(getFragmentManager(), "errordialog");
//        }
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(requestCode == DIALOG_ERROR_CODE) {
//            mResolvingError = false;
//            if(resultCode == RESULT_OK) { // Error was resolved, now connect to the client if not done so.
//                if(!api.isConnecting() && !api.isConnected()) {
//                    api.connect();
//                }
//            }
//        }
//    }
//
//    @Override
//    public void onConnected(Bundle connectionHint) {
//        Log.v(TAG, "Connected successfully");
//
//    /* Connection to Google Drive established. Now request for Contents instance, which can be used to provide file contents.
//       The callback is registered for the same. */
//        Drive.DriveApi.newDriveContents(api).setResultCallback(contentsCallback);
//    }
//
//    final private ResultCallback<DriveApi.DriveContentsResult> contentsCallback = new ResultCallback<DriveApi.DriveContentsResult>() {
//
//        @Override
//        public void onResult(DriveApi.DriveContentsResult result) {
//            if (!result.getStatus().isSuccess()) {
//                Log.v(TAG, "Error while trying to create new file contents");
//                return;
//            }
//
//            String mimeType = MimeTypeMap.getSingleton().getExtensionFromMimeType("db");
//            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
//                    .setTitle(GOOGLE_DRIVE_FILE_NAME) // Google Drive File name
//                    .setMimeType(mimeType)
//                    .setStarred(true).build();
//            // create a file on root folder
//            Drive.DriveApi.getRootFolder(api)
//                    .createFile(api, changeSet, result.getDriveContents())
//                    .setResultCallback(fileCallback);
//        }
//
//    };
//
//    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new ResultCallback<DriveFolder.DriveFileResult>() {
//
//        @Override
//        public void onResult(DriveFolder.DriveFileResult result) {
//            if (!result.getStatus().isSuccess()) {
//                Log.v(TAG, "Error while trying to create the file");
//                return;
//            }
//            mfile = result.getDriveFile();
//            mfile.open(api, DriveFile.MODE_WRITE_ONLY, null).setResultCallback(contentsOpenedCallback);
//        }
//    };
//
//    final private ResultCallback<DriveApi.DriveContentsResult> contentsOpenedCallback = new ResultCallback<DriveApi.DriveContentsResult>() {
//
//        @Override
//        public void onResult(DriveApi.DriveContentsResult result) {
//
//            if (!result.getStatus().isSuccess()) {
//                Log.v(TAG, "Error opening file");
//                return;
//            }
//
//            try {
//                FileInputStream is = new FileInputStream(getDbPath());
//                BufferedInputStream in = new BufferedInputStream(is);
//                byte[] buffer = new byte[8 * 1024];
//                DriveContents content = result.getDriveContents();
//                BufferedOutputStream out = new BufferedOutputStream(content.getOutputStream());
//                int n = 0;
//                while( ( n = in.read(buffer) ) > 0 ) {
//                    out.write(buffer, 0, n);
//                }
//
//                in.close();
////                mfile.commitAndCloseContents(api, content).setResultCallback(new ResultCallback<Status>() {
////                 @Override
////                 public void onResult(Status result) {
////                  Handle the response status
////                 }
////                 });
//            } catch (FileNotFoundException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//
//    };
//
//    private File getDbPath() {
//        return this.getDatabasePath(DATABASE_NAME);
//    }
//
//    @Override
//    public void onConnectionSuspended(int cause) {
//        // TODO Auto-generated method stub
//        Log.v(TAG, "Connection suspended");
//
//    }
//
//    public void onDialogDismissed() {
//        mResolvingError = false;
//    }
//
//    public static class ErrorDialogFragment extends DialogFragment {
//        public ErrorDialogFragment() {}
//
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            int errorCode = this.getArguments().getInt("error");
//            return GooglePlayServicesUtil.getErrorDialog(errorCode, this.getActivity(), DIALOG_ERROR_CODE);
//        }
//
//        public void onDismiss(DialogInterface dialog) {
//            this.getActivity().onDialogDismissed();
//        }
//    }
//}