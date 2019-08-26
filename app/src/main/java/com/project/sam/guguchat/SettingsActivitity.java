package com.project.sam.guguchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import id.zelory.compressor.Compressor;

import static android.R.attr.id;

public class    SettingsActivitity extends AppCompatActivity {

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrrentUser;

    // android layout

    private CircleImageView mDisplayImage;
    private TextView mName;
    private TextView mStatus;

    //    private  Button mImageBtn;
    private static  final int GALLERY_PICK =1;

    // storage fb
    private StorageReference mImageStorage;

    private ProgressDialog mProgressDial;

    private Toolbar mSettingsBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_activitity);


        //Toolbar
        mSettingsBar = (Toolbar) findViewById(R.id.settings_appbar);
        setSupportActionBar(mSettingsBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");

        mDisplayImage = (CircleImageView)  findViewById(R.id.settings_image);
        mName = (TextView) findViewById(R.id.display_txt);
        mStatus = (TextView) findViewById(R.id.status_txt);


        mImageStorage = FirebaseStorage.getInstance().getReference();

        mCurrrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = mCurrrentUser.getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase.keepSynced(true);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                mName.setText(name);
                mStatus.setText(status);

                // load default hen no image is avai...
                if (!image.equals("default")){

                }

                //  Picasso.with(SettingsActivitity.this).load(image).placeholder(R.drawable.default_avatar).into(mDisplayImage);

                Picasso.with(SettingsActivitity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.default_avatar).into(mDisplayImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(SettingsActivitity.this).load(image).placeholder(R.drawable.default_avatar).into(mDisplayImage);
                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

       /* mStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String status_value = mStatus.getText().toString();

                Intent status_intent = new Intent(SettingsActivitity.this
                        ,StatusActivity.class);
                status_intent.putExtra("status_value", status_value);

                startActivity(status_intent);
            }
        });*/

        //click on  teststatus
        mStatus.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                String status_value = mStatus.getText().toString();

                Intent status_intent = new Intent(SettingsActivitity.this
                        ,StatusActivity.class);
                status_intent.putExtra("status_value", status_value);

                startActivity(status_intent);

            }
        });


        mDisplayImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"),GALLERY_PICK);
                */
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivitity.this);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && requestCode == RESULT_OK ) {

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);
        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mProgressDial = new ProgressDialog(SettingsActivitity.this);
                mProgressDial.setTitle("Uploading Image");
                mProgressDial.setMessage("Please wait  ");
                mProgressDial.setCanceledOnTouchOutside(false);
                mProgressDial.show();

                Uri resultUri = result.getUri();

                File thumb_filepath = new File (resultUri.getPath());

                String current_user_id = mCurrrentUser.getUid();

                Bitmap thumb_bitmap = new Compressor(this)
                        .setMaxWidth(200)
                        .setMaxHeight(200)
                        .setQuality(75)
                        .compressToBitmap(thumb_filepath);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                StorageReference filepath = mImageStorage.child("proflie_images").child(current_user_id +".jpg");
                final StorageReference thumbs_filepath = mImageStorage.child("profile_images").child("thumbs").child(current_user_id + "jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()){

                            @SuppressWarnings("VisibleForTests") final
                            String download_url = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumbs_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    @SuppressWarnings("VisibleForTests")
                                    String thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();

                                    if (thumb_task.isSuccessful()){

                                        Map update_hashMap = new HashMap();
                                        update_hashMap.put("image",download_url);
                                        update_hashMap.put("thumb_image",thumb_downloadUrl);


                                        mUserDatabase.updateChildren(update_hashMap ).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()){

                                                    mProgressDial.dismiss();

                                                }

                                            }
                                        });

                                    }else {

                                        Toast.makeText(SettingsActivitity.this, "Error in Uploading thumbnail ", Toast.LENGTH_LONG).show();
                                        mProgressDial.dismiss();

                                    }
                                }
                            });

                        }else {

                            Toast.makeText(SettingsActivitity.this, "Error in Uploading ", Toast.LENGTH_LONG).show();
                            mProgressDial.dismiss();


                        }
                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }

        }
    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
