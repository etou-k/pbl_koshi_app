package com.example.pbl_koshi_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast; // Toastをインポート

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pbl_koshi_app.data.QuizActivity;
import com.example.pbl_koshi_app.data.SpotDataLoader; // SpotDataLoaderをインポート
import com.example.pbl_koshi_app.databinding.FragmentHomeBinding;

import java.util.List; // Listをインポート

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    // ★★★ ここからが修正の核心 ★★★
    private final ActivityResultLauncher<Intent> startQuizActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // クイズ画面から結果が返ってきたときの処理
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    // QuizActivityから返されたIntentから"SPOT_ID"を取得
                    String spotId = result.getData().getStringExtra("SPOT_ID");

                    if (spotId != null && !spotId.isEmpty()) {
                        // 1. 全観光地リストをJSONから読み込む
                        List<Spot> allSpots = SpotDataLoader.loadSpots(requireContext());

                        // 2. IDが一致するSpotオブジェクトを探す
                        Spot targetSpot = null;
                        for (Spot spot : allSpots) {
                            if (spot.getId().equals(spotId)) {
                                targetSpot = spot;
                                break; // 見つかったらループを抜ける
                            }
                        }

                        // 3. 見つかったSpotオブジェクトをSpotDetailActivityに渡して起動
                        if (targetSpot != null) {
                            Intent intent = new Intent(getActivity(), SpotDetailActivity.class);
                            intent.putExtra("spot", targetSpot);
                            startActivity(intent);
                        } else {
                            // 万が一、IDに一致するSpotが見つからなかった場合
                            Toast.makeText(getContext(), "該当する観光地の詳細が見つかりませんでした", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
    // ★★★ ここまで ★★★

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 観光スポット(SpotListActivity)画面へ
        binding.buttonSpot.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SpotListActivity.class);
            startActivity(intent);
        });

        // クイズ(QuizActivity)画面へ
        binding.buttonQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), QuizActivity.class);
            startQuizActivityLauncher.launch(intent); // 通常のstartActivityではなく、ランチャー経由で起動
        });

        // 図鑑(EncyclopediaActivity)画面へ
        binding.buttonEncyclopedia.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EncyclopediaActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}