package com.example.casptoneworkout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.casptoneworkout.databinding.ListFoodcalBinding;
import com.example.casptoneworkout.databinding.ListHomeworkoutBinding;

// Adapter untuk daftar data latihan di beranda
public class HomeAdapter extends ListAdapter<HomeData, HomeAdapter.HomeViewHolder> {

    // Listener untuk menangani klik pada item
    private static HomeAdapter.OnItemClickListener listener;

    // Konstruktor adapter
    public HomeAdapter() {
        super(diffCallback);
    }

    // Membuat ViewHolder baru
    @Override
    public HomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_homeworkout, parent, false);
        return new HomeViewHolder(itemView);
    }

    // Mengikat data ke ViewHolder
    @Override
    public void onBindViewHolder(HomeViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    // Menetapkan listener klik
    public static void setOnClickListener(HomeAdapter.OnItemClickListener listener) {
        HomeAdapter.listener = listener;
    }

    // Interface untuk menangani klik pada item
    public interface OnItemClickListener {
        void onClicked(HomeData data);
    }

    // ViewHolder untuk setiap item dalam RecyclerView
    static class HomeViewHolder extends RecyclerView.ViewHolder {
        private final ListHomeworkoutBinding binding;

        // Konstruktor ViewHolder
        public HomeViewHolder(View view) {
            super(view);
            binding = ListHomeworkoutBinding.bind(view);
        }

        // Mengikat data ke tampilan dalam ViewHolder
        public void bind(HomeData data) {
            binding.namaworkout.setText(data.getNamaWorkout());
            binding.detailcard.setText(data.getDetail());
            binding.jumlah.setText(data.getJumlah());

            Glide.with(itemView.getContext())
                    .load(data.getImg())
                    .into(binding.imgWorkout);

            // Menangani klik pada item
            binding.card.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onClicked(data);
                }
            });
        }
    }

    // Utilitas untuk membandingkan item dalam daftar
    private static final DiffUtil.ItemCallback<HomeData> diffCallback = new DiffUtil.ItemCallback<HomeData>() {
        @Override
        public boolean areItemsTheSame(HomeData oldItem, HomeData newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(HomeData oldItem, HomeData newItem) {
            return oldItem.equals(newItem);
        }
    };
}
