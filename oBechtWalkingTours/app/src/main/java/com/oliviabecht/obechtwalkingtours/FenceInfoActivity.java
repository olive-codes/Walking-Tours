package com.oliviabecht.obechtwalkingtours;


import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.oliviabecht.obechtwalkingtours.databinding.ActivityFenceInfoBinding;

public class FenceInfoActivity extends AppCompatActivity {

    Picasso picasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        ActivityFenceInfoBinding binding = ActivityFenceInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        picasso = Picasso.get();
        ImageView photoImage = binding.notificationImageView;
        ConstraintLayout cs = binding.notificationCS;

        cs.setOnClickListener(v -> {

        });


        if (getIntent().hasExtra("FENCE_TITLE")) {
            String id = getIntent().getStringExtra("FENCE_TITLE");
            binding.notificationTitle.setText(id);
        }
        if (getIntent().hasExtra("FENCE_ADDRESS")) {
            String id = getIntent().getStringExtra("FENCE_ADDRESS");
            binding.notificationAddress.setText(id);
        }
        if (getIntent().hasExtra("FENCE_BODY")) {
            String id = getIntent().getStringExtra("FENCE_BODY");
            binding.notificationTextBody.setText(id);
        }
        if (getIntent().hasExtra("FENCE_IMAGE")) {
            String id = getIntent().getStringExtra("FENCE_IMAGE");

            picasso.load(id).resize(375, 180).centerCrop().into(photoImage);

        }

    }
}