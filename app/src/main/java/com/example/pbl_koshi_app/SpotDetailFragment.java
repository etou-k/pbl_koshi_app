package com.example.pbl_koshi_app;

import android.content.Intent;
import android.net.Uri;
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

            // 「地図」ボタンのクリック処理
            binding.buttonShowOnMap.setOnClickListener(v -> {
                // スポットの緯度・経度を取得
                double latitude = currentSpot.getLatitude();
                double longitude = currentSpot.getLongitude();
                String spotName = currentSpot.getName();

                // GoogleマップのURIを作成
                // geo:緯度,経度?q=緯度,経度(ピンの名前)
                String uriString = "geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "(" + Uri.encode(spotName) + ")";
                Uri gmmIntentUri = Uri.parse(uriString);

                // マップアプリを起動するためのインテントを作成
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                // Googleマップアプリを指定する (任意ですが、確実性が増します)
                mapIntent.setPackage("com.google.android.apps.maps");

                // デバイスにGoogleマップがインストールされているか確認
                if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            });

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
