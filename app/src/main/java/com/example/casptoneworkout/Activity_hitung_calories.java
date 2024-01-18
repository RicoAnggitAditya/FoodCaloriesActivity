package com.example.casptoneworkout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.casptoneworkout.databinding.ActivityFoodcaloriesBinding;
import com.example.casptoneworkout.databinding.ActivityHitungCaloriesBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Activity_hitung_calories extends AppCompatActivity {
    // Objek binding untuk layout aktivitas
    private ActivityHitungCaloriesBinding binding;

    // Instans Firestore untuk berinteraksi dengan Firebase Firestore
    private FirebaseFirestore firestore;

    // Adapter untuk RecyclerView
    private KalkulatorAdapter kalkulatorAdapter;

    // Daftar untuk menyimpan objek FoodData yang diambil dari Firestore
    private List<FoodData> foodDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHitungCaloriesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Menginisialisasi Firestore
        firestore = FirebaseFirestore.getInstance();

        // Nama koleksi Firestore
        String namaKoleksi = "foodcalories";

        // Membuat referensi ke koleksi Firestore
        CollectionReference collectionReference = firestore.collection(namaKoleksi);

        // Menyiapkan RecyclerView
        setUpRv(collectionReference);


    }



    // Fungsi untuk menyiapkan RecyclerView
    private void setUpRv(CollectionReference collectionReference) {
        // Menginisialisasi FoodAdapter
        kalkulatorAdapter = new KalkulatorAdapter();


        // Mengatur layout manager dan adapter untuk RecyclerView
        binding.recycleview.setHasFixedSize(true);
        binding.recycleview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.recycleview.setAdapter(kalkulatorAdapter);

        // Mengambil dan menampilkan data dari Firestore di RecyclerView
        collectFoods(collectionReference);
    }

    // Fungsi untuk mengumpulkan dan menampilkan data makanan dari Firestore
    private void collectFoods(CollectionReference collectionReference) {
        collectionReference.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Menangani kasus gagalnya pengambilan data
                    Toast.makeText(Activity_hitung_calories.this, "Gagal mengambil data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                // Menangani berhasilnya pengambilan data
                handleQuerySnapshot(querySnapshot);
            }
        });
    }


    // Fungsi untuk menangani QuerySnapshot yang diperoleh dari Firestore
    private void handleQuerySnapshot(QuerySnapshot querySnapshot) {
        if (querySnapshot != null && !querySnapshot.isEmpty()) {
            // Membersihkan foodDataList sebelum menambahkan hasil pencarian baru
            foodDataList.clear();

            // Iterasi melalui setiap dokumen dalam QuerySnapshot
            for (QueryDocumentSnapshot document : querySnapshot) {
                // Mengekstrak data dari dokumen
                Map<String, Object> data = document.getData();

                // Membuat objek FoodData dari data yang diekstrak
                FoodData food = new FoodData(
                        (String) data.get("id"),
                        (String) data.get("namaMakanan"),
                        (String) data.get("img"),
                        (String) data.get("totalCalories"),
                        (String) data.get("protein"),
                        (String) data.get("lemak"),
                        (String) data.get("karb"),
                        (String) data.get("description"),
                        (Boolean) data.get("kalkulator"),
                        (Long) data.get("count")
                );

                // Mencatat nama item makanan
                Log.d("nakun", String.valueOf(food.getNamaMakanan()));

                // Menambahkan objek FoodData ke daftar
                if (food.getKalkulator() == true) {
                    foodDataList.add(food);
                }
            }

            // Mencatat ukuran foodDataList
            Log.d("nakun", String.valueOf(foodDataList.size()));

            // Menampilkan hasil pencarian di RecyclerView
            if (foodDataList.size() > 0) {
                kalkulatorAdapter.submitList(foodDataList);

                // Hitung total kalori dari foodDataList
                int totalKalori = calculateTotalCalories(foodDataList);

                if (totalKalori > 0) {
                    // Menampilkan total kalori pada TextView
                    binding.hitungankalori.setText(totalKalori + " kcal");

                    // Menampilkan pesan bahwa total kalori telah dihitung
                    Toast.makeText(Activity_hitung_calories.this, "Total Kalori: " + totalKalori, Toast.LENGTH_SHORT).show();
                } else {
                    // Jika total kalori 0, tampilkan pesan bahwa tidak ada kalori
                    binding.hitungankalori.setText("0 kcal");
                    Toast.makeText(Activity_hitung_calories.this, "Tidak ada kalori", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Tampilkan pesan bahwa tidak ada hasil pencarian
                Toast.makeText(Activity_hitung_calories.this, "Tidak ada hasil pencarian", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // Fungsi untuk menghitung total kalori dari daftar FoodData
    private int calculateTotalCalories(List<FoodData> foodList) {
        int totalCalories = 0;

        for (FoodData food : foodList) {
            String target = food.getTotalCalories();

            // Memeriksa apakah totalCalories dan food.getTotalCalories() berisi nilai yang valid sebelum menambahkannya
            if (!TextUtils.isEmpty(target)) {
                try {
                    totalCalories += extractIntegerFromKcal(target, food.getCount());
                } catch (NumberFormatException e) {
                    Log.e("NumberFormatException", "Error parsing totalCalories: " + food.getTotalCalories(), e);
                }
            }
        }

        return totalCalories;
    }

    private int extractIntegerFromKcal(String kcalString, Long count) {
        // Hapus "kcal" dan spasi dari string
        String cleanString = kcalString.replace("kcal", "").trim();

        try {
            // Konversi string menjadi integer
            return (int) (Integer.parseInt(cleanString.trim())*count);
        } catch (NumberFormatException e) {
            // Tangani kesalahan jika tidak dapat mengonversi ke integer
            e.printStackTrace();
            return 0; // Atau nilai default sesuai kebutuhan
        }
    }


}