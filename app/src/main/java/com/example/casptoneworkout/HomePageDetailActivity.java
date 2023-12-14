package com.example.casptoneworkout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.casptoneworkout.databinding.ActivityFoodCaloriesDetailBinding;
import com.example.casptoneworkout.databinding.ActivityHomePageDetailBinding;

public class HomePageDetailActivity extends AppCompatActivity {

    private ActivityHomePageDetailBinding binding;

    private HomeData homeData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mengatur layout dari file XML menggunakan view binding
        binding = ActivityHomePageDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Mendapatkan Intent dari aktivitas sebelumnya
        Intent intent = getIntent();

        // Mengekstrak objek HomeData dari Intent
        if (intent != null && intent.hasExtra("HOME_DATA")) {
            HomeData receivedHomeData = (HomeData) intent.getSerializableExtra("HOME_DATA");

            // Menetapkan objek HomeData yang diterima ke variabel homeData
            homeData = receivedHomeData;
        }

        // Memeriksa apakah homeData tidak null sebelum menetapkan tampilan
        if (homeData != null) {
            setViewByData();
        }
    }

    private void setViewByData() {
        binding.workoutname.setText(homeData.getNamaWorkout());
        binding.descriptionworkout.setText(homeData.getDetail());

        // Membuat dan mengatur MediaController
//        MediaController mediaController = new MediaController(this);
//        mediaController.setAnchorView(binding.videoworkout);
//        binding.videoworkout.setMediaController(mediaController);
//        mediaController.hide();

        // Mengatur URI video dan memulai pemutaran
        binding.videoworkout.setVideoURI(Uri.parse(homeData.getVideoUrl()));
        binding.videoworkout.start();



        binding.videoworkout.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                // Restart the video from the beginning
                binding.videoworkout.seekTo(0);
                binding.videoworkout.start();
            }
        });
    }


}