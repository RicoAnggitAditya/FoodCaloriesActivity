package com.example.casptoneworkout;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

public class Addschedulee extends AppCompatActivity {

//    Spinner edJudullat ;
//    TextView edtgl, edwaktu;
//    MaterialButton btn_save;
//    FirebaseFirestor db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addschedulee);

        // Mendapatkan referensi ke Spinner dari XML
        Spinner spinner = findViewById(R.id.edJudullat);

        // Mendapatkan array dari sumber daya string
        String[] judulLatArray = getResources().getStringArray(R.array.judul_lat_array);

        // Membuat adapter menggunakan array string
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                judulLatArray
        );

        // Menetapkan layout untuk daftar pilihan dropdown
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Menetapkan adapter ke Spinner
        spinner.setAdapter(adapter);

    }
}