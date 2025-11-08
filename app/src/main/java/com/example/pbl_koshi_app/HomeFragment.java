package com.example.pbl_koshi_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.pbl_koshi_app.data.QuizActivity;
import com.example.pbl_koshi_app.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private final ActivityResultLauncher<Intent> startQuizActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // クイズ画面から結果が返ってきたときの処理
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    // QuizActivityから返されたIntentから"SPOT_ID"を取得
                    String spotId = result.getData().getStringExtra("SPOT_ID");

                    if (spotId != null && !spotId.isEmpty()) {
                        // 受け取ったspotIdを使ってSpotDetailFragmentへ遷移
                        Bundle bundle = new Bundle();
                        bundle.putString("spot_id", spotId); // nav_graphのargument名と一致させる
                        NavHostFragment.findNavController(HomeFragment.this)
                                .navigate(R.id.action_HomeFragment_to_SpotDetailFragment, bundle);
                    }
                }
            });

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
            startQuizActivityLauncher.launch(intent);
        });

        // ★★★ 図鑑(EncyclopediaActivity)画面への処理を追加 ★★★
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
