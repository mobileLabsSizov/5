package com.example.lab5;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        findViewById(R.id.download_button).setOnClickListener((l) -> {
            EditText t = findViewById(R.id.file_id_input);
            new DownloadTask(this, t.getText().toString());
        });

        findViewById(R.id.view_button).setOnClickListener((l) -> {
            EditText t = findViewById(R.id.file_id_input);
            File dir = this.getExternalFilesDir("/Downloads");
            try {
                File file = new File(dir, t.getText().toString());
                if (!file.exists() || file.isDirectory()) {
                    Toast.makeText(this, "Файл не найден", Toast.LENGTH_SHORT).show();
                    return;
                }

                Uri path = FileProvider.getUriForFile(this,
                        this.getApplicationContext().getPackageName() + ".provider",
                        new File(dir, t.getText().toString()));

                Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
                pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pdfOpenintent.setDataAndType(path, "application/pdf");
                startActivity(pdfOpenintent);
            } catch (Exception e) {
                Toast.makeText(this, "Файл не найден", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.delete_button).setOnClickListener((l) -> {
            EditText t = findViewById(R.id.file_id_input);
            File file = new File(this.getExternalFilesDir("/Downloads"), t.getText().toString());

            if (file.isFile() && file.delete()) {
                Toast.makeText(this, "Файл успешно удалён", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Файл не найден", Toast.LENGTH_SHORT).show();
            }
        });

        showPopupWindow();
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
        SharedPreferences prefs = getSharedPreferences("key", Context.MODE_PRIVATE);
        if (prefs.getBoolean("popup", true)) {
            try {
                // Создаем layout для всплывающего окна
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.popup, null);

                // Создаем всплывающее окно
                PopupWindow popupWindow = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);

                // Обработка нажатия кнопки "OK"
                layout.findViewById(R.id.okButton).setOnClickListener(v -> {
                    try {
                        CheckBox dontShowAgain = layout.findViewById(R.id.dontShowAgain);

                        // Сохраняем значение чекбокса в SharedPreferences
                        SharedPreferences.Editor editor = getSharedPreferences("key", Context.MODE_PRIVATE).edit();
                        editor.putBoolean("popup", !dontShowAgain.isChecked());
                        editor.apply();

                        // Закрываем всплывающее окно
                        popupWindow.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                // Отображаем всплывающее окно
                popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
         */
    }

    private void showPopupWindow() {
        SharedPreferences prefs = getSharedPreferences("key", Context.MODE_PRIVATE);
//        prefs.edit().putBoolean("popup", true).apply(); // можно использовать для того, чтобы обратно в префы установить true (для отладки и дебага)

        if (prefs.getBoolean("popup", true)) {
            View layout = getLayoutInflater().inflate(R.layout.popup, findViewById(R.id.main));
            layout.findViewById(R.id.okButton).setOnClickListener(v -> {
                        // обрабатываем кнопку "ОК" на поп'апе
                        CheckBox dontShowAgain = layout.findViewById(R.id.dontShowAgain);

                        // Сохраняем значение чекбокса в SharedPreferences
                        SharedPreferences.Editor editor = getSharedPreferences("key", Context.MODE_PRIVATE).edit();
                        editor.putBoolean("popup", !dontShowAgain.isChecked());
                        editor.apply();

                        // Убераем popup с экрана
                        findViewById(R.id.popup).setVisibility(View.GONE);
                    }
            );
        }
    }
}