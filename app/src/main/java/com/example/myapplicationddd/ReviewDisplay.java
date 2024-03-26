package com.example.myapplicationddd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReviewDisplay extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<ReviewUpload> reviewUploadList;
    DatabaseReference databaseReference, likeref;
    private ProgressBar progressBar;

    private FloatingActionButton floatingActionButton;
    Boolean testclick = false;

    String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_display);



        recyclerView = findViewById(R.id.reView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = findViewById(R.id.recycleProgressId);

        floatingActionButton = findViewById(R.id.fabUplaodId);

        reviewUploadList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Upload");
        likeref =  FirebaseDatabase.getInstance().getReference("Likes");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ReviewDisplay.this,GiveReview.class);
                startActivity(i);
            }
        });


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot datasnap : snapshot.getChildren()){
                    ReviewUpload reviewUpload = datasnap.getValue(ReviewUpload.class);
                    reviewUploadList.add(reviewUpload);
                }

                progressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(ReviewDisplay.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });



        FirebaseRecyclerOptions<ReviewUpload> options =
                new FirebaseRecyclerOptions.Builder<ReviewUpload>()
                        .setQuery(databaseReference, ReviewUpload.class)
                        .build();


        FirebaseRecyclerAdapter<ReviewUpload, MyViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ReviewUpload, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull ReviewUpload model) {
                ReviewUpload reviewUpload = reviewUploadList.get(position);
                holder.textView.setText(reviewUpload.getImageName());
                holder.userTextView.setText(reviewUpload.getUsername());
                Glide.with(getApplicationContext()).load(reviewUpload.getUserImage()).into(holder.userImage);

                Picasso.get().load(reviewUpload.getImageUri())
                        .fit()
                        .centerCrop()
                        .into(holder.imageView);




                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                String userId = firebaseUser.getUid();
                String postkey = getRef(position).getKey();

                holder.getLikeStatus(postkey,userId);


                holder.likeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        testclick = true;

                        likeref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(testclick == true){
                                    if(snapshot.child(postkey).hasChild(userId)){
                                        likeref.child(postkey).child(userId).removeValue();
                                        testclick = false;
                                    }
                                    else{
                                        likeref.child(postkey).child(userId).setValue(true);
                                        testclick = false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

                holder.commentsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(),CommentPanel.class);
                        intent.putExtra("postkey",postkey);
                        startActivity(intent);
                    }
                });



            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sample_like_comment_layout,parent,false);
                return new MyViewHolder(view);

            }
        };

        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

}