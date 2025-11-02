package com.example.pbl_koshi_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class EncyclopediaAdapter extends RecyclerView.Adapter<EncyclopediaAdapter.TriviaViewHolder> {

    // データクラスを内部で定義
    public static class TriviaData {
        public String spotName;
        public String triviaText;

        public TriviaData(String spotName, String triviaText) {
            this.spotName = spotName;
            this.triviaText = triviaText;
        }
    }

    private final List<TriviaData> triviaDataList;

    public EncyclopediaAdapter(List<TriviaData> triviaDataList) {
        this.triviaDataList = triviaDataList;
    }

    @NonNull
    @Override
    public TriviaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_trivia, parent, false);
        return new TriviaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TriviaViewHolder holder, int position) {
        TriviaData currentItem = triviaDataList.get(position);
        holder.spotNameTextView.setText(currentItem.spotName);
        holder.triviaTextView.setText(currentItem.triviaText);
    }

    @Override
    public int getItemCount() {
        return triviaDataList.size();
    }

    public static class TriviaViewHolder extends RecyclerView.ViewHolder {
        public TextView spotNameTextView;
        public TextView triviaTextView;

        public TriviaViewHolder(@NonNull View itemView) {
            super(itemView);
            spotNameTextView = itemView.findViewById(R.id.text_item_spot_name);
            triviaTextView = itemView.findViewById(R.id.text_item_trivia);
        }
    }
}
