package com.bowfletchers.chatberry.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.print.PrinterId;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bowfletchers.chatberry.ClassLibrary.Member;
import com.bowfletchers.chatberry.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class User_profile extends AppCompatActivity {

    private final int REQUEST_CODE_IMAGE = 1;
    private final String STORE_URL = "gs://chatberry-201de.appspot.com";

    ImageView imageViewUserPhoto;
    TextView textViewUserName;
    Switch aSwitchOnlineStatus;
    Button buttonDone;
    Button buttonSignout;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseStorage firebaseStore;
    StorageReference storageReference;

    String newUserDisplayName;
    String newUserPhoToURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        referenceViews();

        // init Fire auth instance
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firebaseStore = FirebaseStorage.getInstance();
        storageReference = firebaseStore.getReferenceFromUrl(STORE_URL);

        displayDefaultUserImage();

        // open camera when user click on image view
        imageViewUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_CODE_IMAGE);
            }
        });

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // save user photo to firebase store
                uploadUserPhotoToStore();

                // update user profile with name and photo url
                updateUserProfileInfo();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // get the captured image to image view
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK && data != null) {
            Bitmap bitmapImg = (Bitmap) data.getExtras().get("data");
            imageViewUserPhoto.setImageBitmap(bitmapImg);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void referenceViews() {
        imageViewUserPhoto = findViewById(R.id.user_profile_avatar);
        textViewUserName = findViewById(R.id.user_profile_name);
        aSwitchOnlineStatus = findViewById(R.id.user_profile_onlineStatus_switch);
        buttonDone = findViewById(R.id.user_profile_button_done);
        buttonSignout = findViewById(R.id.user_profile_button_logout);
    }

    private void displayDefaultUserImage(){
        String url = "https://cdn.pixabay.com/photo/2016/08/08/09/17/avatar-1577909__340.png";
        Glide.with(this).load(url).into(imageViewUserPhoto);

    }

    private void uploadUserPhotoToStore() {
        // set components to create img name
        String userName = currentUser.getDisplayName();
        String userId = currentUser.getUid();
        String imageName = userName + userId + ".png";

        // upload current img to store
        final StorageReference userImageRef = storageReference.child("images/" + imageName);
        imageViewUserPhoto.setDrawingCacheEnabled(true);
        imageViewUserPhoto.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageViewUserPhoto.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = userImageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(User_profile.this, "Upload image failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // get user img download url from store
                userImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String downloadUrl = uri.toString();

                    }
                });
            }
        });
    }

    private void updateUserProfileInfo() {

    }
}
