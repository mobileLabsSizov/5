package com.example.lab5;

import android.os.Environment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by SONU on 29/10/15.
 */
public class DownloadTask {

    private static final String TAG = "Download Task";
    private final Context context;
    private final Button mainButton, viewButton, deleteButton;
    private final URL baseURL;

    public DownloadTask(MainActivity context, String fileID) {
        this.context = context;
        this.mainButton = context.findViewById(R.id.download_button);
        this.viewButton = context.findViewById(R.id.view_button);
        this.deleteButton = context.findViewById(R.id.delete_button);
        try {
            this.baseURL = new URL("https://ntv.ifmo.ru/file/journal/");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        //Start Downloading Task
        new DownloadingTask().execute(fileID);
    }

    private class DownloadingTask extends AsyncTask<String, Void, Void> {
        File outputFile = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mainButton.setEnabled(false);
            viewButton.setEnabled(false);
            deleteButton.setEnabled(false);
            mainButton.setText("Скачивается ...");//Set Button Text when download started
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                if (outputFile != null) {
                    Toast.makeText(context, "Файл скачан", Toast.LENGTH_SHORT).show();
                    mainButton.setEnabled(true);
                    viewButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                    mainButton.setText("Скачать");//If Download completed then change button text

                } else {
                    Toast.makeText(context, "Скачать файл не получилось(", Toast.LENGTH_SHORT).show();
                    mainButton.setEnabled(true);
                    viewButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                    mainButton.setText("Скачать");//Change button text again after 3sec


                }
            } catch (Exception e) {
                e.printStackTrace();

                Toast.makeText(context, "Скачать файл не получилось(", Toast.LENGTH_SHORT).show();
                mainButton.setEnabled(true);
                viewButton.setEnabled(true);
                deleteButton.setEnabled(true);
                mainButton.setText("Скачать");
            }


            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(String... arg0) {
            if (arg0[0].isEmpty()) {
                return null;
            }

            try {
                URL url = new URL(baseURL + arg0[0]);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.connect();

                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Server returned HTTP " + c.getResponseCode()
                            + " " + c.getResponseMessage());
                }

                if (!c.getContentType().equals("application/pdf")) {
                    Toast.makeText(context, "Файл не найден", Toast.LENGTH_SHORT).show();
                    return null;
                }


                outputFile = new File(context.getExternalFilesDir("/Downloads"), arg0[0]);

                //Create New File if not present
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                    Log.e(TAG, "File Created");
                }

                FileOutputStream fos = new FileOutputStream(outputFile);

                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                byte[] buffer = new byte[1024];//Set buffer type
                int count;
                while ((count = input.read(buffer)) != -1) {
                    fos.write(buffer, 0, count);
                }

                fos.flush();
                fos.close();
                input.close();

            } catch (Exception e) {

                //Read exception if something went wrong
                e.printStackTrace();
                outputFile = null;
                Log.e(TAG, "Download Error Exception " + e.getMessage());
            }

            return null;
        }
    }
}