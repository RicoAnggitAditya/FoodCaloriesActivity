package com.example.casptoneworkout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.casptoneworkout.databinding.ListFoodcalBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Adapter untuk RecyclerView yang menampilkan daftar FoodData.
 */
public class FoodAdapter extends ListAdapter<FoodData, FoodAdapter.FoodViewHolder> {

    private FirebaseFirestore firestore;

    // Antarmuka untuk menangani klik item
    private static OnItemClickListener listener;

    /**
     * Konstruktor untuk FoodAdapter.
     */
    public FoodAdapter() {
        super(diffCallback);
    }

    /**
     * Metode untuk membuat FoodViewHolder berdasarkan tata letak XML.
     *
     * @param parent   Kelas ViewGroup yang akan menjadi induk dari tampilan yang baru dibuat setelah diisi.
     * @param viewType Jenis tampilan baru yang akan dibuat.
     * @return ViewHolder baru yang dibuat.
     */
    @Override
    public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_foodcal, parent, false);
        return new FoodViewHolder(itemView);
    }

    /**
     * Metode untuk mengikat data ke holder ViewHolder pada posisi tertentu.
     *
     * @param holder   ViewHolder yang akan diisi dengan data.
     * @param position Posisi item dalam data set.
     */
    @Override
    public void onBindViewHolder(FoodAdapter.FoodViewHolder holder, int position) {
        // ...

        // Mendapatkan data makanan pada posisi tertentu
        FoodData currentFood = getItem(position);

        // Menetapkan data ke ViewHolder
        holder.bind(currentFood);

        // Menetapkan OnClickListener untuk tombol "Add Hitung"
        holder.binding.BtnAddcal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Memperbarui data di database Firestore
                incrementCountAndUpdateFirestore(currentFood.getId(), currentFood.getCount());
            }
        });
    }

    // Metode untuk menambahkan nilai count dan memperbarui data di database Firestore
    private void incrementCountAndUpdateFirestore(String foodId, Long currentCount) {
        // Menginisialisasi Firestore
        firestore = FirebaseFirestore.getInstance();
        // Mendapatkan referensi dokumen makanan di Firestore
        DocumentReference foodRef = firestore.collection("foodcalories").document(foodId);

        // Menambahkan 1 ke nilai count saat ini
        Long newCount = currentCount + 1;

        // Memperbarui data di Firestore menggunakan Map
        Map<String, Object> updates = new HashMap<>();
        updates.put("count", newCount);
        updates.put("kalkulator", true);


        // Memperbarui data di Firestore
        foodRef.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Berhasil memperbarui data
                        Log.d("FirestoreUpdate", "Data berhasil diperbarui. Count baru: " + newCount);
                        // Tambahkan logika lain yang diperlukan setelah pembaruan sukses
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Gagal memperbarui data
                        Log.e("FirestoreUpdate", "Gagal memperbarui data.", e);
                        // Tambahkan logika lain yang diperlukan jika pembaruan gagal
                    }
                });
    }




    /**
     * Metode untuk menetapkan listener klik untuk adapter.
     *
     * @param listener Listener yang akan menangani klik item.
     */
    public static void setOnClickListener(OnItemClickListener listener) {
        FoodAdapter.listener = listener;
    }

    /**
     * Antarmuka untuk menangani klik item dalam RecyclerView.
     */
    public interface OnItemClickListener {
        void onClicked(FoodData food);
    }

    /**
     * ViewHolder yang berisi tampilan setiap item dalam RecyclerView.
     */
    static class FoodViewHolder extends RecyclerView.ViewHolder {
        private final ListFoodcalBinding binding;

        /**
         * Konstruktor untuk FoodViewHolder.
         *
         * @param view Tampilan yang akan diikat oleh ViewHolder.
         */
        public FoodViewHolder(View view) {
            super(view);
            binding = ListFoodcalBinding.bind(view);
        }

        /**
         * Metode untuk mengikat data ke tampilan ViewHolder.
         *
         * @param food Objek FoodData yang akan diikat.
         */
        public void bind(FoodData food) {
            binding.NamaMakanan.setText(food.getNamaMakanan());
            binding.TotalCalories.setText(food.getTotalCalories());

            // Menggunakan Glide untuk memuat gambar ke ImageView
            Glide.with(itemView.getContext())
                    .load(food.getImg()) // Sumber daya gambar (URL, path file, atau ID resource)
                    .into(binding.imgMenu);

            // Menetapkan onClickListener untuk item di dalam card
            binding.card.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onClicked(food);
                }
            });
        }
    }

    /**
     * Utilitas perbandingan item untuk memeriksa apakah item adalah item yang sama atau memiliki konten yang sama.
     */
    private static final DiffUtil.ItemCallback<FoodData> diffCallback = new DiffUtil.ItemCallback<FoodData>() {
        @Override
        public boolean areItemsTheSame(FoodData oldItem, FoodData newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(FoodData oldItem, FoodData newItem) {
            return oldItem.equals(newItem);
        }
    };
}


