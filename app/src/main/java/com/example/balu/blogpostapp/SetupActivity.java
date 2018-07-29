package com.example.balu.blogpostapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private CircleImageView setupImage;
    private Uri mainImageUri;
    private EditText nameText;
    private Button signupBtn;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFireStore;
    private String user_id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        getSupportActionBar().setTitle("Account Settings");
        setupImage = (CircleImageView)findViewById(R.id.setup_image);
        nameText = (EditText) findViewById(R.id.nameText);
        signupBtn = (Button) findViewById(R.id.signupBtn);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mFireStore = FirebaseFirestore.getInstance();
        user_id = mAuth.getCurrentUser().getUid();

        mFireStore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    displayToast("Data Retriev task Succuss");

                    if(task.getResult().exists()){
                        displayToast("Data Exists");
                        String name = task.getResult().getString("Name");
                        String image = task.getResult().getString("ImgLink");
                        nameText.setText(name);
                        //RequestOptions placeHolderRequest = new RequestOptions();
                        //placeHolderRequest.placeholder(R.drawable.erdtqth);
                        Glide.with(SetupActivity.this).load(image).into(setupImage);

                    }
                }else{
                    displayToast("Data Retrive task Failed");
                }
            }


        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = nameText.getText().toString();
                if(!TextUtils.isEmpty(name)&& mainImageUri!=null){
                    final StorageReference image_path = mStorageRef.child("profile_pics").child(user_id+".jpg");
                    Log.d("Img Path",image_path.toString());
                    image_path.putFile(mainImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            displayToast("Upload Success");
                            HashMap<String,String> userDetails = new HashMap<String,String>();
                            userDetails.put("Name",name);
                            userDetails.put("ImgLink",image_path.toString());
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            mFireStore.collection("Users").document(user_id).set(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        displayToast(" Details Upload Success");
                                        Intent intent = new Intent(SetupActivity.this,MainActivity.class);
                                        startActivity(intent);
                                    }else{
                                        displayToast(" Details Upload Failed");

                                    }
                                }
                            });
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                    displayToast("Upload Failed");
                                }
                            });
                }
            }
        });
        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(SetupActivity.this,new String []{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    }else{
                        ImageCropper();

                    }
                }else {
                    ImageCropper();;
                }
            }

            private void ImageCropper() {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(SetupActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageUri = result.getUri();
                setupImage.setImageURI(mainImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    private void displayToast(String msg) {
        Toast.makeText(SetupActivity.this,msg,Toast.LENGTH_SHORT).show();

    }
}
