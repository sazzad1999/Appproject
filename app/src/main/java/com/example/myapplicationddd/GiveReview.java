package com.example.myapplicationddd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class GiveReview extends AppCompatActivity {


    EditText imgname;
    ImageView imageView;
    TextView pickImage, upload;
    DatabaseReference databaseReference, userRef,userProfileReference;;
    StorageReference storageReference;
    Uri imageUri;
    String userId = "";

    private int PICK_IMAGE_REQUEST = 1;
    private ProgressDialog progressDialog;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private static final int REQUEST_LOCATION_SETTINGS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_review);


        imgname= (EditText)findViewById(R.id.title);
        pickImage= (TextView) findViewById(R.id.selectId);
        upload = (TextView) findViewById(R.id.uploadId);
        progressDialog = new ProgressDialog(this);
        imageView = (ImageView)findViewById(R.id.imageId);

        databaseReference = FirebaseDatabase.getInstance().getReference("Upload");
        storageReference = FirebaseStorage.getInstance().getReference("Upload");



        //In order to display username
        userRef = FirebaseDatabase.getInstance().getReference().child("Userprofile");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(GiveReview.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                } else {
                    ActivityCompat.requestPermissions(GiveReview.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION_REQUEST_CODE);
                }

                userRef.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String username = snapshot.child("uname").getValue().toString();
                            String userImage = snapshot.child("uimage").getValue().toString();
                            saveData(username,userImage);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });




        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });


    }





    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == this.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imageView);

        }


    }

    //Image Extension
    public String getFileExtension(Uri imageUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }




    private void saveData(String username, String userImage){
        String imageName = imgname.getText().toString().trim();

        if(imageName.isEmpty()){
            imgname.setError("Enter a Title");

        }

        else {
            progressDialog.setMessage("Wait while Sharing");
            progressDialog.setTitle("Review");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            StorageReference ref = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            ref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(GiveReview.this, "Shared", Toast.LENGTH_SHORT).show();

                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            Uri downloadUri = uriTask.getResult();

                            ReviewUpload reviewUpload = new ReviewUpload(userId,imageName, downloadUri.toString(), username, userImage);
                            String uploadId = databaseReference.push().getKey();
                            databaseReference.child(uploadId).setValue(reviewUpload);
                            progressDialog.dismiss();
                            nextActivity();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(GiveReview.this, "Unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void nextActivity(){
        imgname.setText("");
        Intent intent = new Intent(GiveReview.this, ReviewDisplay.class);
        startActivity(intent);
    }


}