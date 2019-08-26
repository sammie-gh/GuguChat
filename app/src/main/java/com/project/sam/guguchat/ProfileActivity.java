package com.project.sam.guguchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";

    private ImageView mprofileImage;
    private TextView mProfileName, mProfileStatus, mProfileFriendsTotoal;
    private Button mProfileSendRequestBtn, mDeclineBtn;

    private DatabaseReference mUsersDatabase, mFriendRequestDatabase;
    private FirebaseUser mCurrent_user;
    private String sender_user_id;
    private DatabaseReference mNotifications;

    private String mUnfriendUser;
    private ProgressDialog mProgessDialog;
    private String mCurrent_state;
    private  DatabaseReference mFriendsDatabase;
    private DatabaseReference mRootRef;

    //Toolbar
    private Toolbar mProfileToolbar;
    private String mProfileuser;

    //experiment

    private DatabaseReference Req_ref,userRef;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mProfileToolbar = (Toolbar) findViewById(R.id.profile_abb_bar);
        setSupportActionBar(mProfileToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mRootRef = FirebaseDatabase.getInstance().getReference();

//TOOLBAR NAME
        mProfileuser = getIntent().getStringExtra("user_id");
        mRootRef.child("Users").child(mProfileuser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String pro_user_name =  dataSnapshot.child("name").getValue().toString();
                getSupportActionBar().setTitle(pro_user_name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });//

       mAuth = FirebaseAuth.getInstance();
        final String user_id = getIntent().getStringExtra("user_id");

        mUsersDatabase =         FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_request");
        mFriendsDatabase =       FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotifications =         FirebaseDatabase.getInstance().getReference().child("notifications");

        mRootRef =  FirebaseDatabase.getInstance().getReference(); //adding root directory

        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();

        mprofileImage = (ImageView) findViewById(R.id.profile_image);
        mProfileName = (TextView)  findViewById(R.id.profile_dislplay_name);
        mProfileFriendsTotoal= (TextView) findViewById(R.id.profile_total);
        mProfileStatus =(TextView) findViewById(R.id.profile_crrent_status);
        mProfileSendRequestBtn = (Button) findViewById(R.id.profile_send_req_btn);
        mDeclineBtn =  (Button) findViewById(R.id.Profile_decline_btn);

        mCurrent_state = "not_friends";

        mDeclineBtn.setVisibility(View.INVISIBLE);
        mDeclineBtn.setEnabled(false);

        mProgessDialog = new ProgressDialog(this);
        mProgessDialog.setTitle("Loading User Data");
        mProgessDialog.setMessage("'Please wait loading user Info.'");
        mProgessDialog.setCanceledOnTouchOutside(false);
        mProgessDialog.show();

//        //experiment
     Req_ref = FirebaseDatabase.getInstance().getReference().child("Friend_request");
        sender_user_id = mAuth.getCurrentUser().getUid();

        //profile image
        mprofileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick:profole image");
                Toast.makeText(ProfileActivity.this, "picture selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this,Profile_picture_activity.class);
                startActivity(intent);


            }
        });


        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(display_name);
                mProfileStatus.setText(status);

                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.avatar).into(mprofileImage);


                //--------- FRIENDS LIST ? REQUEST-----------

                mFriendRequestDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(user_id)){

                            String req_type = dataSnapshot.child(user_id).child("req_type").getValue().toString();

                            if (req_type.equals("received")){


                                mCurrent_state = "req_received";
                                mProfileSendRequestBtn.setText("Accept Friend Request");

                                mDeclineBtn.setVisibility(View.VISIBLE);
                                mDeclineBtn.setEnabled(true);

                            }else if (req_type.equals("sent")){

                                mCurrent_state = "req_sent";
                                mProfileSendRequestBtn.setText("Cancel Friend Request");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);

                            }

                            mProgessDialog.dismiss();

                        }else {

                            mFriendsDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.hasChild(user_id)){

                                        mCurrent_state = "friends";
                                        mUnfriendUser = getIntent().getStringExtra("user_id");
                                        mRootRef.child("Users").child(mUnfriendUser).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                String user_name = dataSnapshot.child("name").getValue().toString();
                                                mProfileSendRequestBtn.setText("UnFriend " +
                                                        user_name);
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                        mDeclineBtn.setVisibility(View.INVISIBLE);
                                        mDeclineBtn.setEnabled(false);

                                    }

                                    mProgessDialog.dismiss();

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                    mProgessDialog.dismiss();

                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        //mProgessDialog.dismiss();

                    }
                });




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

              if (!sender_user_id.equals(user_id))
        {
            mProfileSendRequestBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mProfileSendRequestBtn.setEnabled(false);

                    // ---- NOT FRIEND STAGE---

                    if (mCurrent_state.equals("not_friends") ){

                        DatabaseReference newNotificationref =  mRootRef.child("notifications").child(user_id).push();
                        String newNotificationId = newNotificationref.getKey();

                        HashMap<String, String> notificationsData = new HashMap<>();
                        notificationsData.put("from", mCurrent_user.getUid());
                        notificationsData.put("type", "request");

                        Map requestMap =  new HashMap();

                        //mine is f.request
                        requestMap.put( "Friend_request/" + mCurrent_user.getUid() + "/" + user_id + "/req_type", "sent");
                        requestMap.put( "Friend_request/"  + user_id +"/" + mCurrent_user.getUid() + "/req_type", "received");
                        requestMap.put("notifications/" + user_id + "/" +  newNotificationId, notificationsData);

                        mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                if (databaseError != null ){

                                    Toast.makeText( ProfileActivity.this, "There was some error in sending request", Toast.LENGTH_SHORT).show();

                                }

                                mProfileSendRequestBtn.setEnabled(true);

                                mCurrent_state = "req_sent";
                                mProfileSendRequestBtn.setText("Cancel Friend Request");

                            }
                        });
                    }

                    // ---- CANCEL FRIEND STAGE---
                    if (mCurrent_state.equals("req_sent")){

                        mFriendRequestDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        mFriendRequestDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        Toasty.success(ProfileActivity.this, "request Sent ", Toast.LENGTH_SHORT).show();
                                                        mProfileSendRequestBtn.setEnabled(true);
                                                        mCurrent_state = "not_friends";
                                                        mProfileSendRequestBtn.setText("Send Friend Request");
                                                        mDeclineBtn.setVisibility(View.INVISIBLE);
                                                        mDeclineBtn.setEnabled(false);
                                                    }
                                                });

                                    }
                                });


                    }

                    // --- REQ RECEIVE STATE--

                    if (mCurrent_state.equals("req_received")){

                        final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                        Map friendsMap =  new HashMap();
                        friendsMap.put("Friends/" + mCurrent_user.getUid() + "/" + user_id +  "/date", currentDate);
                        friendsMap.put("Friends/" + user_id + "/" + mCurrent_user.getUid() +  "/date", currentDate);

                        friendsMap.put("Friend_request/" + mCurrent_user.getUid() + "/" + user_id, null);
                        friendsMap.put("Friend_request/" + user_id + "/" + mCurrent_user.getUid(), null);


                        mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                if (databaseError == null){

                                    mProfileSendRequestBtn.setEnabled(true);
                                    mCurrent_state = "friends";
                                    mProfileSendRequestBtn.setText("UnFriend  this User");
                                    //test can be restore back  user_id;

                                    mDeclineBtn.setVisibility(View.INVISIBLE);
                                    mDeclineBtn.setEnabled(false);


                                } else {

                                    String error = databaseError.getMessage();

                                    Toast.makeText( ProfileActivity.this, error, Toast.LENGTH_SHORT).show();

                                }
                            }


                        });



                    }
                    // ---- UN-FRIENDS--------

                    if (mCurrent_state.equals("friends")){
                        Map unfriendMap = new HashMap();
                        unfriendMap.put("Friends/" + mCurrent_user.getUid() + "/" + user_id, null );
                        unfriendMap.put("Friends/" + user_id + "/" + mCurrent_user.getUid(), null);

                        mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                if (databaseError == null){

                                    mCurrent_state = "not_friends";
                                    mProfileSendRequestBtn.setText("Send Friend Request");
                                    //test can be restore back  user_id;

                                    mDeclineBtn.setVisibility(View.INVISIBLE);
                                    mDeclineBtn.setEnabled(false);


                                } else {

                                    String error = databaseError.getMessage();

                                    Toast.makeText( ProfileActivity.this, error, Toast.LENGTH_SHORT).show();

                                }

                                mProfileSendRequestBtn.setEnabled(true);
                            }


                        });

                    }

                    //----- Decline Friend Request ------

                    if (mCurrent_state.equals("Friend_request")){

                        //if work change it to declineMap
                        Map declineMap = new HashMap();
                        declineMap.put("Friend_request/" + mCurrent_user.getUid() + "/" + user_id, null );
                        declineMap.put("Friend_request/" + user_id + "/" + mCurrent_user.getUid(), null);

                        mRootRef.updateChildren(declineMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                if (databaseError == null){

                                    mCurrent_state = "not_friends";
                                    mProfileSendRequestBtn.setText("Send Friend Request");
                                    //Test can be restore back  user_id;

                                    mDeclineBtn.setVisibility(View.INVISIBLE);
                                    mDeclineBtn.setEnabled(false);


                                } else {

                                    String error = databaseError.getMessage();

                                    Toast.makeText( ProfileActivity.this, error, Toast.LENGTH_SHORT).show();

                                }


                                mProfileSendRequestBtn.setEnabled(true);
                            }


                        });



                    }

                }
            });

        }        else {

                  mDeclineBtn.setVisibility(View.INVISIBLE);
                  mProfileSendRequestBtn.setVisibility(View.INVISIBLE);
              }




    }
}
