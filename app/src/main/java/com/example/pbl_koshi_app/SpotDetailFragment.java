package com.example.pbl_koshi_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.pbl_koshi_app.databinding.FragmentSpotDetailBinding; // ★ Bindingクラス
import com.example.pbl_koshi_app.Spot; // Spotクラスをインポート
import com.example.pbl_koshi_app.data.SpotMaster; // SpotMasterをインポート

public class SpotDetailFragment extends Fragment {

    private FragmentSpotDetailBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSpotDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ★★★ 引数(arguments)から spot_id を受け取る ★★★
        String spotId = null;
        if (getArguments() != null) {
            spotId = getArguments().getString("spot_id");
        }

        // spotIdを使ってSpotMasterからSpotオブジェクトを取得
        // (SpotMasterはJSONから全データを読み込んで保持するクラスと仮定)
        Spot currentSpot = SpotMaster.getInstance(getContext()).findSpotById(spotId);

        // 取得したスポット情報をUIにセットする
        if (currentSpot != null) {
            binding.spotImageView.setImageResource(currentSpot.getImageResourceId()); // 仮のID
            binding.spotNameTextView.setText(currentSpot.getName());
            binding.spotDescriptionTextView.setText(currentSpot.getDescription());
        } else {
            // エラー処理: IDが見つからなかった場合など
            binding.spotNameTextView.setText("スポット情報が見つかりません");
        }

        // 「ホームに戻る」ボタンにクリックリスナーを設定
        // binding.buttonToHome は、レイアウトファイル(activity_spot_detail.xml)で定義したボタンのIDとします
        binding.buttonBackToHome.setOnClickListener(v -> {
            // nav_graph.xmlで定義したactionのIDを使って画面遷移を実行
            NavHostFragment.findNavController(SpotDetailFragment.this)
                    .navigate(R.id.action_SpotDetailFragment_to_HomeFragment);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
