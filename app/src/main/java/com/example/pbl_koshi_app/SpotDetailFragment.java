package com.example.pbl_koshi_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.pbl_koshi_app.data.SpotMaster;
import com.example.pbl_koshi_app.databinding.FragmentSpotDetailBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.Locale;

public class SpotDetailFragment extends Fragment {

    private static final double DISTANCE_THRESHOLD_METERS = 100.0; // 宝箱を開けられる距離（メートル）

    private FragmentSpotDetailBinding binding; // ViewBinding を使用
    private Spot currentSpot;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    // 位置情報のパーミッションリクエストランチャー
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    startLocationUpdates(); // 許可されたら位置情報取得開始
                } else {
                    Toast.makeText(requireContext(), "位置情報の許可が必要です", Toast.LENGTH_SHORT).show();
                    updateTreasureButtonState(false); // 許可されなければ宝箱は開けられない
                    binding.textDistanceToTreasure.setText("宝箱まで計測不可");
                }
            }
    );

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // FragmentがUIを作成する前の、より早いこの段階で初期化します。
        // これで、以降のどのライフサイクルメソッドからも安全に参照できます。
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // ViewBinding を使ってレイアウトを生成
        binding = FragmentSpotDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // --- 1. 引数からSpot IDを受け取り、スポット情報を取得・表示 ---
        String spotId = null;
        if (getArguments() != null) {
            spotId = getArguments().getString("spot_id");
        }
        // SpotMasterから該当するSpotオブジェクトを取得
        currentSpot = SpotMaster.getInstance(requireContext()).findSpotById(spotId);

        if (currentSpot != null) {
            // UIにスポット情報をセット
            binding.spotImageView.setImageResource(currentSpot.getImageResourceId());
            binding.spotNameTextView.setText(currentSpot.getName());
            binding.spotDescriptionTextView.setText(currentSpot.getDescription());
        } else {
            // エラーハンドリング
            binding.spotNameTextView.setText("スポット情報が見つかりません");
            Toast.makeText(requireContext(), "スポット情報の読み込みに失敗しました", Toast.LENGTH_LONG).show();
            
            binding.buttonTriviaTreasure.setEnabled(false);
            return; // 処理を中断
        }

        // --- 2. ボタンのクリックリスナーを設定 ---
        setupButtonClickListeners();

        // --- 3. 位置情報更新のコールバックを定義 ---
        setupLocationCallback();

        // 初期状態では宝箱を無効状態に設定
        updateTreasureButtonState(false);
    }

    private void setupButtonClickListeners() {
        // 「ホームに戻る」ボタン
        binding.buttonBackToHome.setOnClickListener(v -> {
            NavHostFragment.findNavController(SpotDetailFragment.this)
                    .navigate(R.id.action_SpotDetailFragment_to_HomeFragment);
        });

        // 「豆知識の宝箱」ボタン
        binding.buttonTriviaTreasure.setOnClickListener(v -> {
            // TriviaActivityを起動
            Intent triviaIntent = new Intent(requireActivity(), TriviaActivity.class);
            triviaIntent.putExtra("spot", currentSpot);
            startActivity(triviaIntent);
        });

    }

    private void setupLocationCallback() {
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

                        // 距離に応じて宝箱の状態とテキストを更新
                        updateTreasureButtonState(distanceInMeters < DISTANCE_THRESHOLD_METERS);
                        updateDistanceText(distanceInMeters);
                    }
                }
            }
        };
    }

    // --- ライフサイクルメソッド ---
    @Override
    public void onResume() {
        super.onResume();
        // 画面が表示されるたびに位置情報の権限を確認し、取得を開始
        checkLocationPermissionAndStartUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        // 画面が非表示になったら位置情報の更新を停止（バッテリー節約）
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // メモリリークを防ぐためにbindingをnullにする
        binding = null;
    }

    // --- 位置情報関連のメソッド ---
    private void checkLocationPermissionAndStartUpdates() {
        // Fragmentでは requireContext() を使う
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setMinUpdateIntervalMillis(5000)
                .build();

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return; // 権限がない場合は処理を中断
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    // --- UI更新メソッド ---
    private void updateTreasureButtonState(boolean enabled) {
        if (binding == null) return; // bindingがnullの場合は何もしない
        binding.buttonTriviaTreasure.setEnabled(enabled);
        binding.buttonTriviaTreasure.setAlpha(enabled ? 1.0f : 0.3f);
    }

    private void updateDistanceText(float distanceInMeters) {
        if (binding == null) return; // bindingがnullの場合は何もしない
        String distanceText;
        if (distanceInMeters < DISTANCE_THRESHOLD_METERS) {
            distanceText = "宝箱まであと少し！";
        } else if (distanceInMeters < 1000) {
            distanceText = String.format(Locale.JAPAN, "宝箱まで %d m", (int) distanceInMeters);
        } else {
            distanceText = String.format(Locale.JAPAN, "宝箱まで %.1f km", distanceInMeters / 1000.0f);
        }
        binding.textDistanceToTreasure.setText(distanceText);
    }
}
