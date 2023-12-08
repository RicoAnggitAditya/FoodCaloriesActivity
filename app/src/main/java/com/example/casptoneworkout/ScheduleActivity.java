package com.example.casptoneworkout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.casptoneworkout.databinding.ActivityScheduleActivityBinding;

public class ScheduleActivity extends AppCompatActivity {

    private ActivityScheduleActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScheduleActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // ... kode lainnya ...

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Jalankan kode
                // Membuat Intent untuk memulai Addschedulee
                Intent intent = new Intent(ScheduleActivity.this, Addschedulee.class);

                // Memulai Activity baru
                startActivity(intent);

            }
        });
    }
}

