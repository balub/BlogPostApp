package com.example.balu.blogpostapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class AddPostActivity extends AppCompatActivity {


    private EditText newPosttext;
    private Button postBtn;
    private ImageView postImage;
    private Uri postImgURI=null;
    private FirebaseFirestore mFirestore;
    private StorageReference mStorage;
    private FirebaseAuth mAuth;
    String currentUser;
    private ProgressBar postProgressBar;
    private Bitmap compressedImageFile;
    private long lastClickTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        getSupportActionBar().setTitle("Create New Post");
        newPosttext = (EditText) findViewById(R.id.postText);
        postBtn = (Button) findViewById(R.id.postBtn);
        postImage = (ImageView)findViewById(R.id.new_post_image);
        mFirestore = FirebaseFirestore.getInstance();
        mStorage= FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();
        postProgressBar = (ProgressBar)findViewById(R.id.post_progressbar);
        postProgressBar.setVisibility(View.INVISIBLE);

        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512,512)
                        .setAspectRatio(1,1)
                        .start(AddPostActivity.this);
            }
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                    return;
                }
                    lastClickTime = SystemClock.elapsedRealtime();
                postProgressBar.setVisibility(View.VISIBLE);
                final String dec = newPosttext.getText().toString();
                    if (!TextUtils.isEmpty(dec) && postImgURI != null) {
                        final String random = UUID.randomUUID().toString();
                        StorageReference Post = mStorage.child("post_images").child(random + ".jpg");
                        Post.putFile(postImgURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {

                            @Override
                            public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {

                                final String downloadUri = task.getResult().getDownloadUrl().toString();

                                if (task.isSuccessful()) {
                                    File newImgFile = new File(postImgURI.getPath());
                                    try {
                                        compressedImageFile = new Compressor(AddPostActivity.this)
                                                .setMaxHeight(100)
                                                .setMaxWidth(100)
                                                .setQuality(2)
                                                .compressToBitmap(newImgFile);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    byte[] thumbData = baos.toByteArray();
                                    UploadTask thumbFilePath = (UploadTask) mStorage.child("post_images/thumbs").child(random + ".jpg").putBytes(thumbData)
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    String downloadThumb = taskSnapshot.getDownloadUrl().toString();
                                                    Map<String, Object> newPost = new HashMap<>();
                                                    newPost.put("image_url", downloadUri);
                                                    newPost.put("desc", dec);
                                                    newPost.put("thumb", downloadThumb);
                                                    newPost.put("user_id", currentUser);
                                                    newPost.put("time_stamp", FieldValue.serverTimestamp());
                                                    mFirestore.collection("Posts").add(newPost).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                                            if (task.isSuccessful()) {
                                                                displayToast("Post Posted");
                                                                postProgressBar.setVisibility(View.INVISIBLE);
                                                                Intent intent = new Intent(AddPostActivity.this, MainActivity.class);
                                                                startActivity(intent);
                                                                finish();

                                                            } else {
                                                                displayToast("Post Not Posted");
                                                                postProgressBar.setVisibility(View.INVISIBLE);

                                                            }
                                                        }
                                                    });


                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });


                                } else {

                                }
                            }
                        });
                    } else {

                    }


            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                postImgURI = result.getUri();
                postImage.setImageURI(postImgURI);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    private void displayToast(String msg) {
        Toast.makeText(AddPostActivity.this,msg,Toast.LENGTH_SHORT).show();

    }
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int MAX_LENGTH=100;
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
