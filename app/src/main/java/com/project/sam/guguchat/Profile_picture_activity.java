package com.project.sam.guguchat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Profile_picture_activity extends AppCompatActivity {
    private static final String TAG = "Profile_picture_activit";

    private ImageView Profile_image;
    private FirebaseAuth mAuth;
    private DatabaseReference mUsersDatabase;

    private FirebaseUser mCurrrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture_activity);

        mCurrrentUser = FirebaseAuth.getInstance().getCurrentUser();

        Profile_image= ( ImageView) findViewById(R.id.profile_image);


        mAuth = FirebaseAuth.getInstance();
        String current_user_id = mCurrrentUser.getUid();


        mUsersDatabase =      FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id)  ;

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot dataSnapshot) {
                             String image = dataSnapshot.child("image").getValue().toString();
                             Picasso.with(Profile_picture_activity.this).load(image).placeholder(R.drawable.avatar).into(Profile_image);

                             }

                             @Override
                             public void onCancelled(DatabaseError databaseError) {

                             }
                         });

    }
}
