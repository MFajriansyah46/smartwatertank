package com.bangraja.smartwatertank.view.custom;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class RiverEffect {

    private ObjectAnimator riverAnimator;

    private final View riverEffect;
    public RiverEffect(View riverEffect){
        this.riverEffect = riverEffect;
    }
    public void startRiverEffect() {
        riverEffect.setVisibility(View.VISIBLE);

        if (riverAnimator != null && riverAnimator.isRunning()) {
            return; // Jangan jalankan lagi kalau sudah aktif
        }

        // Pastikan riverEffect sudah di-layout sebelum ambil width
        riverEffect.post(() -> {
            // Cek panjang View agar aliran air bisa bergerak dengan seamless
            float width = riverEffect.getWidth();

            // Animasi untuk translate dari kiri ke kanan
            riverAnimator = ObjectAnimator.ofFloat(
                    riverEffect,
                    "translationX",
                    -width, width // Mulai dari -width (kiri) dan gerak ke kanan
            );
            riverAnimator.setDuration(2000);
            riverAnimator.setRepeatCount(ValueAnimator.INFINITE); // Repeat terus menerus
            riverAnimator.setRepeatMode(ValueAnimator.RESTART); // Restart untuk seamless
            riverAnimator.setInterpolator(new LinearInterpolator());
            riverAnimator.start();
        });
    }

    public void stopRiverEffect() {
        if (riverAnimator != null) {
            riverAnimator.cancel();
            riverAnimator = null;
        }
        riverEffect.setVisibility(View.GONE);
    }
}
