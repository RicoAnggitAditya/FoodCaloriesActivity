package com.example.casptoneworkout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.casptoneworkout.databinding.ActivityFoodcaloriesBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FoodCaloriesActivity extends AppCompatActivity {
    // Objek binding untuk layout aktivitas
    private ActivityFoodcaloriesBinding binding;

    // Instans Firestore untuk berinteraksi dengan Firebase Firestore
    private FirebaseFirestore firestore;

    // Adapter untuk RecyclerView
    private FoodAdapter foodAdapter;

    // Daftar untuk menyimpan objek FoodData yang diambil dari Firestore
    private List<FoodData> foodDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Menginflasikan layout menggunakan view binding
        binding = ActivityFoodcaloriesBinding.inflate(getLayoutInflater());
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


        // Menyiapkan onClickListener untuk tombol "btnSearch"
        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mendapatkan query pencarian dari EditText
                String query = binding.search.getText().toString();

                // Mencari item yang sesuai dalam foodDataList
                searchFood(query);
            }
        });

        // Menyiapkan onClickListener untuk tombol atau elemen UI di ActivityFoodCalories
        binding.newsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Membuat Intent untuk membuka ActivityHomePage
                Intent intent = new Intent(FoodCaloriesActivity.this, HomePageActivity.class);

                // Memulai aktivitas tanpa animasi transisi
                startActivity(intent);

                // Memberikan kesan pindah halaman instan tanpa animasi tambahan
                overridePendingTransition(0, 0);
            }
        });

    }

    // Fungsi untuk menyiapkan RecyclerView
    private void setUpRv(CollectionReference collectionReference) {
        // Menginisialisasi FoodAdapter
        foodAdapter = new FoodAdapter();

        // Mengatur pendengar klik untuk item di RecyclerView
        FoodAdapter.setOnClickListener(onClicked);

        // Mengatur layout manager dan adapter untuk RecyclerView
        binding.recycleview.setHasFixedSize(true);
        binding.recycleview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.recycleview.setAdapter(foodAdapter);

        // Mengambil dan menampilkan data dari Firestore di RecyclerView
        collectFoods(collectionReference);
    }

    // Fungsi untuk mengumpulkan dan menampilkan data makanan dari Firestore
    private void collectFoods(CollectionReference collectionReference) {
        // Secara asinkron mengambil data dari Firestore
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Menangani berhasilnya pengambilan data
                    handleQuerySnapshot(task.getResult());
                } else {
                    // Menangani kasus gagalnya pengambilan data
                    Toast.makeText(FoodCaloriesActivity.this, "Gagal mengambil data: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
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
                        (String) data.get("description")
                );

                // Mencatat nama item makanan
                Log.d("nakun", String.valueOf(food.getNamaMakanan()));

                // Menambahkan objek FoodData ke daftar
                foodDataList.add(food);
            }

            // Mencatat ukuran foodDataList
            Log.d("nakun", String.valueOf(foodDataList.size()));

            // Menampilkan hasil pencarian di RecyclerView
            if (foodDataList.size() > 0) {
                foodAdapter.submitList(foodDataList);
            } else {
                Toast.makeText(FoodCaloriesActivity.this, "Tidak ada hasil pencarian", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(FoodCaloriesActivity.this, "Koleksi kosong", Toast.LENGTH_SHORT).show();
        }
    }

    // Fungsi untuk menangani peristiwa klik item di RecyclerView
    private FoodAdapter.OnItemClickListener onClicked = new FoodAdapter.OnItemClickListener() {
        @Override
        public void onClicked(FoodData food) {
            // Membuat Intent untuk navigasi ke FoodCaloriesDetailActivity
            Intent intent = new Intent(FoodCaloriesActivity.this, FoodCaloriesDetailActivity.class);
            // Meneruskan objek FoodData yang dipilih ke aktivitas detail
            intent.putExtra("FOOD_DATA", food);
            // Memulai aktivitas detail
            startActivity(intent);
        }
    };

    // Fungsi untuk melakukan pencarian berdasarkan query yang diberikan
    private void searchFood(String query) {
        // Memeriksa apakah query kosong
        if (TextUtils.isEmpty(query) && foodDataList.size() > 0) {
            // Menampilkan daftar asli jika query kosong
            foodAdapter.submitList(foodDataList);
        } else {
            // Membuat daftar baru untuk menyimpan hasil pencarian
            List<FoodData> listSearch = new ArrayList<>();

            // Iterasi melalui setiap item makanan dalam daftar asli
            for (FoodData food : foodDataList) {
                // Mendapatkan nama item makanan dan mengubahnya menjadi huruf kecil
                String namaMakanan = food.getNamaMakanan().toLowerCase();

                // Memeriksa apakah nama item makanan mengandung query (case-insensitive)
                if (namaMakanan.contains(query.toLowerCase())) {
                    // Menambahkan item makanan yang cocok ke daftar hasil pencarian
                    listSearch.add(food);
                }
            }

            // Menampilkan hasil pencarian di RecyclerView
            foodAdapter.submitList(listSearch);
        }
    }
}
