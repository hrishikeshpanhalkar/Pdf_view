package com.example.pdf_viewer;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private PDFView pdfView;
    private static final String TAG = "MainActivity";
    private static final String[] PERMISSIONS = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(TAG, "onCreate() Method invoked ");
        ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, 112);
        pdfView = findViewById(R.id.pdfView);
        Toast.makeText(MainActivity.this, Environment.getExternalStorageDirectory().getPath(), Toast.LENGTH_SHORT).show();
    }

    public void request(View view) {
        ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, 112);
        Search_Dir(getFilesDir().getAbsoluteFile());
        System.out.println(String.valueOf(getFilesDir().getAbsoluteFile()));
    }

    public void view(View view) {
        Log.v(TAG, "view() Method invoked ");
        if (!hasPermissions(MainActivity.this, PERMISSIONS)) {
            Log.v(TAG, "download() Method DON'T HAVE PERMISSIONS ");
            Toast t = Toast.makeText(getApplicationContext(), "You don't have read access !", Toast.LENGTH_LONG);
            t.show();
        } else {
            File d = new File(getFilesDir().getAbsolutePath());
            System.out.println(d);// -> filename = maven.pdf
            File pdfFile = new File(d, "maven.pdf");

            Log.v(TAG, "view() Method pdfFile " + pdfFile.getAbsolutePath());

            //Uri path = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", pdfFile);


            Log.v(TAG, "view() Method path " + pdfFile);

//            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
//            pdfIntent.setDataAndType(path, "application/pdf");
//            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//            try {
//                startActivity(pdfIntent);
//            } catch (ActivityNotFoundException e) {
//                Toast.makeText(MainActivity.this, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
//            }

            pdfView.fromFile(pdfFile).load();
        }
        Log.v(TAG, "view() Method completed ");

    }

    public void download(View view) {
        Log.v(TAG, "download() Method invoked ");

        if (!hasPermissions(MainActivity.this, PERMISSIONS)) {

            Log.v(TAG, "download() Method DON'T HAVE PERMISSIONS ");

            Toast t = Toast.makeText(getApplicationContext(), "You don't have write access !", Toast.LENGTH_LONG);
            t.show();

        } else {
            Log.v(TAG, "download() Method HAVE PERMISSIONS ");
            new DownloadFile().execute("http://www.axmag.com/download/pdfurl-guide.pdf", "maven.pdf");

        }

        Toast.makeText(MainActivity.this, "download() Method completed ", Toast.LENGTH_SHORT).show();

    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            Log.v(TAG, "doInBackground() Method invoked ");

            String fileUrl = strings[0];   // -> http://maven.apache.org/maven-1.x/maven.pdf
            String fileName = strings[1];  // -> maven.pdf
            //File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File folder = getFilesDir();

            File pdfFile = new File(folder, fileName);
            Log.v(TAG, "doInBackground() pdfFile invoked " + pdfFile.getAbsolutePath());
            Log.v(TAG, "doInBackground() pdfFile invoked " + pdfFile.getAbsoluteFile());

            try {
                pdfFile.createNewFile();
                Log.v(TAG, "doInBackground() file created" + pdfFile);

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "doInBackground() error" + e.getMessage());
                Log.e(TAG, "doInBackground() error" + e.getStackTrace());


            }
            FileDownloader.downloadFile(fileUrl, pdfFile);
            Log.v(TAG, "doInBackground() file download completed");

            return null;
        }
    }

    public void Search_Dir(File dir) {
        String pdfPattern = ".pdf";

        File FileList[] = dir.listFiles();

        if (FileList != null) {
            for (int i = 0; i < FileList.length; i++) {

                if (FileList[i].isDirectory()) {
                    Search_Dir(FileList[i]);
                } else {
                    if (FileList[i].getName().endsWith(pdfPattern)){
                        Toast.makeText(MainActivity.this, FileList[i].getName(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }else {
            Toast.makeText(MainActivity.this, "not done", Toast.LENGTH_SHORT).show();
        }
    }
}