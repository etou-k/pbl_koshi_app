package com.example.pbl_koshi_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SpotDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_detail);

        ImageView spotImageView = findViewById(R.id.spotImageView);
        TextView spotNameTextView = findViewById(R.id.spotNameTextView);
        TextView spotDescriptionTextView = findViewById(R.id.spotDescriptionTextView);

        Intent intent = getIntent();
        Spot spot = (Spot) intent.getSerializableExtra("spot");

        if (spot != null) {
            spotImageView.setImageResource(spot.getImageResourceId());
            spotNameTextView.setText(spot.getName());
            spotDescriptionTextView.setText(spot.getDescription());
        }
    }
}
