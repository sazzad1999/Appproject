package com.example.myapplicationddd;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class CommentsViewHolder extends RecyclerView.ViewHolder {
    ImageView imgUser;
    TextView comment,date,time,user;
    CardView commentCardView;

    public CommentsViewHolder(@NonNull View itemView) {
        super(itemView);

        comment = itemView.findViewById(R.id.resultComment);
        date = itemView.findViewById(R.id.date);
        time = itemView.findViewById(R.id.time);
        user = itemView.findViewById(R.id.user);
        imgUser = itemView.findViewById(R.id.img_User);

        commentCardView = itemView.findViewById(R.id.comment_card_view);
    }
}
