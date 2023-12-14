package com.example.casptoneworkout;

import java.io.Serializable;
import java.util.Date;

public class HomeData implements Serializable {
    private String id;
    private String detail;
    private String namaWorkout;
    private String level;
    private String img;
    private String videoUrl;
    private String jumlah;

    public HomeData() {
        // Diperlukan untuk deserialization
    }

    public HomeData(String id, String detail, String namaWorkout, String level, String img, String videoUrl, String jumlah) {
        this.id = id;
        this.detail = detail;
        this.namaWorkout = namaWorkout;
        this.level = level;
        this.img = img;
        this.videoUrl = videoUrl;
        this.jumlah = jumlah;
    }

    public String getId() {
        return id;
    }

    public String getDetail() {
        return detail;
    }

    public String getNamaWorkout() {
        return namaWorkout;
    }

    public String getLevel() {
        return level;
    }

    public String getImg() {
        return img;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getJumlah() {
        return jumlah;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        HomeData homeData = (HomeData) obj;

        if (!getId().equals(homeData.getId())) return false;
        if (!getNamaWorkout().equals(homeData.getNamaWorkout())) return false;

        return true;
    }
}

