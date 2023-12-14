package com.example.casptoneworkout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.casptoneworkout.databinding.ListFoodcalBinding;

import java.util.List;


/**
 * Adapter untuk RecyclerView yang menampilkan daftar FoodData.
 */
public class FoodAdapter extends ListAdapter<FoodData, FoodAdapter.FoodViewHolder> {

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
    public void onBindViewHolder(FoodViewHolder holder, int position) {
        holder.bind(getItem(position));
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



