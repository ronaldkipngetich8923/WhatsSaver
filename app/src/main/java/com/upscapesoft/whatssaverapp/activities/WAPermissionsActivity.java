package com.upscapesoft.whatssaverapp.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.UriPermission;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.upscapesoft.whatssaverapp.R;
import com.upscapesoft.whatssaverapp.data.Constants;

import java.util.List;
import java.util.Objects;

public class WAPermissionsActivity extends AppCompatActivity {

    Uri uri;
    ActivityResultLauncher<Intent> resultLauncher;
    String stringWA = "primary:Android/media/com.whatsapp/WhatsApp/Media/.Statuses/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);
        showAccessDiaalog();

        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();

                        uri = null;
                        uri = Objects.requireNonNull(data).getData();

                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        Log.i("wss","uri loaded: " + uri.toString());

                        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(),0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("pathW", uri.toString());
                        editor.apply();

                        startApp();

                    }

                });
    }

    private void showAccessDiaalog(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(R.string.storage_access_required);
        builder.setMessage(R.string.alert_long);
        builder.setCancelable(true);

        builder.setPositiveButton(
                R.string.allow,
                (dialog, id) -> {

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        getAccess();
                    }else{
                        if(ActivityCompat.checkSelfPermission(WAPermissionsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                            startApp();
                        } else {
                            ActivityCompat.requestPermissions(WAPermissionsActivity.this, new  String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.REQ_CODE_EXTERNAL_STORAGE_PERMISSION);
                        }
                    }

                });

        builder.setNegativeButton(
                R.string.cancel,
                (dialog, id) -> {
                    Toast.makeText(getApplicationContext(), "Permissions denied! Please enable them in the App Permissions page", Toast.LENGTH_LONG).show();
                    Intent mIntent = new Intent();
                    Uri mUri = Uri.fromParts("package", getPackageName(), null);
                    mIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    mIntent.setData(mUri);
                    startActivity(mIntent);

                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private Boolean checkIfGotAccess() {
        List<UriPermission> permissionList = getContentResolver().getPersistedUriPermissions();
        for (int i = 0; i < permissionList.size(); i++) {
            UriPermission it = permissionList.get(i);

            if (it.getUri().equals((Parcelable)DocumentsContract.buildDocumentUri((String)"com.android.externalstorage.documents", (String)stringWA)) && it.isReadPermission()) {
                return true;
            }

        }
        return false;
    }

    private void getAccess(){
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(),0);
        String path = sharedPreferences.getString("pathW","");
        if (!checkIfGotAccess() && path.length() != 0) {

            uri = Uri.parse(sharedPreferences.getString("pathW",""));
            Log.i("wss","uri loaded: " + uri.toString());

            startApp();

        } else {
            uri = DocumentsContract.buildDocumentUri("com.android.externalstorage.documents", stringWA);
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            resultLauncher.launch(intent.putExtra("android.provider.extra.INITIAL_URI", (Parcelable)DocumentsContract.buildDocumentUri((String)"com.android.externalstorage.documents", (String)stringWA)));

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == Constants.REQ_CODE_EXTERNAL_STORAGE_PERMISSION && grantResults.length >0 &&grantResults[0] == PackageManager.PERMISSION_GRANTED){
            startApp();
        }
    }

    private void startApp(){
        startActivity(new Intent(WAPermissionsActivity.this, WAStatusActivity.class));
        finish();
    }

}