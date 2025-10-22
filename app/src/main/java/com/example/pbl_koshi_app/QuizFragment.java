package com.example.pbl_koshi_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pbl_koshi_app.databinding.FragmentQuizBinding;

public class QuizFragment extends Fragment {

    private FragmentQuizBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentQuizBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ここにクイズのロジックを実装していく
        // 例えば、SpotQuizのデータを読み込んで最初の問題を表示するなど
        binding.textviewQuizQuestion.setText("クイズの問題がここに表示されます");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
