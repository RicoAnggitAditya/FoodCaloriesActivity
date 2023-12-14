package com.example.casptoneworkout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.casptoneworkout.databinding.ActivityHomePageBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomePageActivity extends AppCompatActivity {

    private ActivityHomePageBinding binding;

    private FirebaseFirestore firestore;

    private HomeAdapter homeBeginnerAdapter;
    private HomeAdapter homeIntermediateAdapter;
    private HomeAdapter homeProAdapter;



    private List<HomeData> homeBeginnerList = new ArrayList<>();
    private List<HomeData> homeIntermediateList = new ArrayList<>();
    private List<HomeData> homeProList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Menginflasikan layout menggunakan View Binding
        binding = ActivityHomePageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Menyiapkan onClickListener untuk tombol atau elemen UI di HomePageActivity
        binding.foodcalbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Membuat Intent untuk membuka HomePageActivity
                Intent intent = new Intent(HomePageActivity.this, FoodCaloriesActivity.class);

                // Memulai aktivitas tanpa animasi transisi
                startActivity(intent);

                // Memberikan kesan pindah halaman instan tanpa animasi tambahan
                overridePendingTransition(0, 0);

            }
        });

        // Menginisialisasi Firestore
        firestore = FirebaseFirestore.getInstance();

        // Nama koleksi Firestore
        String namaKoleksi = "homeworkout";

        CollectionReference collectionReference = firestore.collection(namaKoleksi);

        setUpRv(collectionReference);

    }

    // Fungsi untuk menyiapkan RecyclerView
    private void setUpRv(CollectionReference collectionReference) {
        // Menginisialisasi FoodAdapter
        homeBeginnerAdapter = new HomeAdapter();
        homeIntermediateAdapter = new HomeAdapter();
        homeProAdapter = new HomeAdapter();

        // Mengatur pendengar klik untuk item di RecyclerView
        HomeAdapter.setOnClickListener(onClicked);

        // Mengatur layout manager dan adapter untuk RecyclerView
        binding.recyclebeginner.setHasFixedSize(true);
        binding.recyclebeginner.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.recyclebeginner.setAdapter(homeBeginnerAdapter);

        binding.recycleintermediate.setHasFixedSize(true);
        binding.recycleintermediate.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.recycleintermediate.setAdapter(homeIntermediateAdapter);

        binding.recyclepro.setHasFixedSize(true);
        binding.recyclepro.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.recyclepro.setAdapter(homeProAdapter);

        // Mengambil dan menampilkan data dari Firestore di RecyclerView
        collectFoods(collectionReference);


    }

    private int calculateDynamicHeight(int itemCount) {
        // Hitung tinggi berdasarkan jumlah item dan tinggi setiap item
        // Anda dapat menyesuaikan logika ini sesuai kebutuhan
        int itemHeight = getResources().getDimensionPixelSize(R.dimen.item_height);
        int itemMargin = getResources().getDimensionPixelSize(R.dimen.item_margin_top);
        return itemCount * (itemHeight + itemMargin);
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
                    Toast.makeText(HomePageActivity.this, "Gagal mengambil data: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Fungsi untuk menangani QuerySnapshot yang diperoleh dari Firestore
    private void handleQuerySnapshot(QuerySnapshot querySnapshot) {
        if (querySnapshot != null && !querySnapshot.isEmpty()) {
            // Membersihkan foodDataList sebelum menambahkan hasil pencarian baru
            homeBeginnerList.clear();
            homeIntermediateList.clear();
            homeProList.clear();


            // Iterasi melalui setiap dokumen dalam QuerySnapshot
            for (QueryDocumentSnapshot document : querySnapshot) {
                // Mengekstrak data dari dokumen
                Map<String, Object> data = document.getData();

                // Membuat objek FoodData dari data yang diekstrak
                HomeData data1 = new HomeData(
                        (String) data.get("id"),
                        (String) data.get("detail"),
                        (String) data.get("namaWorkout"),
                        (String) data.get("level"),
                        (String) data.get("img"),
                        (String) data.get("videoUrl"),
                        (String) data.get("jumlah")
                );

                // Menambahkan objek FoodData ke daftar
                if (Objects.equals(data1.getLevel(), "Beginner")){
                    homeBeginnerList.add(data1);

                }
                if (Objects.equals(data1.getLevel(), "Intermediate")) {
                    homeIntermediateList.add(data1);
                }
                if (Objects.equals(data1.getLevel(), "Pro")) {
                    homeProList.add(data1);
                }
            }


            // Menampilkan hasil pencarian di RecyclerView
            if (homeBeginnerList.size() > 0) {
                homeBeginnerAdapter.submitList(homeBeginnerList);
            }
            if (homeIntermediateList.size() > 0) {
                homeIntermediateAdapter.submitList(homeIntermediateList);
            }
            if (homeProList.size() > 0) {
                homeProAdapter.submitList(homeProList);
            }
            if (homeBeginnerList.isEmpty()&& homeIntermediateList.isEmpty()&& homeProList.isEmpty() ){
                Toast.makeText(HomePageActivity.this, "Tidak ada hasil pencarian", Toast.LENGTH_SHORT).show();
            }

            // Hitung tinggi secara dinamis
            ViewGroup.LayoutParams params = binding.recyclebeginner.getLayoutParams();
            params.height = calculateDynamicHeight(homeBeginnerAdapter.getItemCount());
            binding.recyclebeginner.setLayoutParams(params);

            // Hitung tinggi secara dinamis
            ViewGroup.LayoutParams params1 = binding.recycleintermediate.getLayoutParams();
            params1.height = calculateDynamicHeight(homeIntermediateAdapter.getItemCount());
            binding.recycleintermediate.setLayoutParams(params1);

            // Hitung tinggi secara dinamis
            ViewGroup.LayoutParams params2 = binding.recyclepro.getLayoutParams();
            params2.height = calculateDynamicHeight(homeProAdapter.getItemCount());
            binding.recyclepro.setLayoutParams(params2);
            Log.d("cad", String.valueOf(homeBeginnerList.size()) +
                    String.valueOf(homeIntermediateList.size()) +
                    String.valueOf(homeProList.size()));

        } else {
            Toast.makeText(HomePageActivity.this, "Koleksi kosong", Toast.LENGTH_SHORT).show();
        }
    }

    // Fungsi untuk menangani peristiwa klik item di RecyclerView
    private HomeAdapter.OnItemClickListener onClicked = new HomeAdapter.OnItemClickListener() {
        @Override
        public void onClicked(HomeData data) {
            // Membuat Intent untuk navigasi ke FoodCaloriesDetailActivity
            Intent intent = new Intent(HomePageActivity.this, HomePageDetailActivity.class);
            // Meneruskan objek FoodData yang dipilih ke aktivitas detail
            intent.putExtra("HOME_DATA", data);
            // Memulai aktivitas detail
            startActivity(intent);
        }
    };


}
