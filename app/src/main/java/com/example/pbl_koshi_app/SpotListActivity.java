package com.example.pbl_koshi_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class SpotListActivity extends AppCompatActivity {

    private List<Spot> spotList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_list);

        // SpotDataクラスから観光地リストを取得
        spotList = SpotData.getSpots();

        ListView spotListView = findViewById(R.id.spotListView);
        List<String> spotNames = new ArrayList<>();
        for (Spot spot : spotList) {
            spotNames.add(spot.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, spotNames);
        spotListView.setAdapter(adapter);

        spotListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SpotListActivity.this, SpotDetailActivity.class);
                intent.putExtra("spot", spotList.get(position));
                startActivity(intent);
            }
        });
    }
}
