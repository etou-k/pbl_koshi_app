package com.example.pbl_koshi_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.Locale;

public class SpotDetailActivity extends AppCompatActivity {

    private static final double DISTANCE_THRESHOLD_METERS = 100.0; // 宝箱を開けられる距離（メートル）

    private Spot currentSpot;
    private ImageButton triviaTreasureButton;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private TextView distanceTextView;

    // 位置情報のパーミッションリクエストランチャー
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    startLocationUpdates(); // 許可されたら位置情報取得開始
                } else {
                    Toast.makeText(this, "位置情報の許可が必要です", Toast.LENGTH_SHORT).show();
                    updateTreasureButtonState(false); // 許可されなければ宝箱は開けられない
                    distanceTextView.setText("宝箱まで計測不可");
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_spot_detail);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        ImageView spotImageView = findViewById(R.id.spotImageView);
        TextView spotNameTextView = findViewById(R.id.spotNameTextView);
        TextView spotDescriptionTextView = findViewById(R.id.spotDescriptionTextView);
        Button backToHomeButton = findViewById(R.id.button_back_to_home);
        triviaTreasureButton = findViewById(R.id.button_trivia_treasure);
        distanceTextView = findViewById(R.id.text_distance_to_treasure);

        Intent intent = getIntent();
        currentSpot = (Spot) intent.getSerializableExtra("spot");

        if (currentSpot != null) {
            spotImageView.setImageResource(currentSpot.getImageResourceId());
            spotNameTextView.setText(currentSpot.getName());
            spotDescriptionTextView.setText(currentSpot.getDescription());
        } else {
            // エラーハンドリング
            Toast.makeText(this, "観光地データの読み込みに失敗しました", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        backToHomeButton.setOnClickListener(v -> {
            Intent mainIntent = new Intent(SpotDetailActivity.this, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(mainIntent);
        });

        triviaTreasureButton.setOnClickListener(v -> {
            Intent triviaIntent = new Intent(SpotDetailActivity.this, TriviaActivity.class);
            triviaIntent.putExtra("spot", currentSpot);
            startActivity(triviaIntent);
        });


            // 最初は宝箱を無効状態にしておく
        updateTreasureButtonState(false);

        // 位置情報更新のコールバックを定義
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (location != null && currentSpot != null) {
                        float[] results = new float[1];
                        Location.distanceBetween(
                                location.getLatitude(), location.getLongitude(),
                                currentSpot.getLatitude(), currentSpot.getLongitude(),
                                results);
                        float distanceInMeters = results[0];

                        // 距離に応じて宝箱の状態を更新
                        updateTreasureButtonState(distanceInMeters < DISTANCE_THRESHOLD_METERS);
                        updateDistanceText(distanceInMeters);

                        Toast.makeText(SpotDetailActivity.this, "現在地取得成功！ 距離: " + distanceInMeters + " m", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLocationPermissionAndStartUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 画面が見えなくなったら位置情報の更新を停止し、バッテリー消費を抑える
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void checkLocationPermissionAndStartUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // パーミッションがない場合はリクエスト
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            // パーミッションがある場合は位置情報の取得を開始
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setMinUpdateIntervalMillis(5000)
                .build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return; // このチェックはstartLocationUpdatesの前に既に行われているはず
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    // 宝箱ボタンの状態を更新するメソッド
    private void updateTreasureButtonState(boolean enabled) {
        triviaTreasureButton.setEnabled(enabled);
        triviaTreasureButton.setAlpha(enabled ? 1.0f : 0.3f); // 有効なら不透明、無効なら半透明
    }

    private void updateDistanceText(float distanceInMeters) {
        String distanceText;
        if (distanceInMeters < DISTANCE_THRESHOLD_METERS) {
            distanceText = "宝箱まであと少し！";
        } else if (distanceInMeters < 1000) {
            distanceText = String.format(Locale.JAPAN, "宝箱まで %d m", (int) distanceInMeters);
        } else {
            distanceText = String.format(Locale.JAPAN, "宝箱まで %.1f km", distanceInMeters / 1000.0f);
        }
        distanceTextView.setText(distanceText);
    }
}