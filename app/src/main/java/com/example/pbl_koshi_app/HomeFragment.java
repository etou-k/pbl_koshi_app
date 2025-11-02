package com.example.pbl_koshi_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pbl_koshi_app.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

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
            Intent intent = new Intent(getActivity(), com.example.pbl_koshi_app.data.QuizActivity.class);
            startActivity(intent);
        });

        // ★★★ 図鑑(EncyclopediaActivity)画面への処理を追加 ★★★
        // buttonEncyclopedia は fragment_home.xml で定義したIDに合わせてください
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
