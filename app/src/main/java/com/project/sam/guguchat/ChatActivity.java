package com.project.sam.guguchat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.afollestad.aesthetic.Aesthetic;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.sam.guguchat.Adapters.MessageAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    private String mChatuser;

    private DatabaseReference mRootref;
    private FirebaseAuth mAuth;

    private DatabaseReference mUserRef;
    private TextView mTitleView;
    private  TextView mLastSeen;
    private CircleImageView mProfile;

    private  String mCurrentUserId;

    private Toolbar mchatToolbar;

    private StorageReference mImageStorage;

    private ImageButton mChatSendBtn;
    private ImageButton mChatAddBtn;
    private EditText mChatMessageView;

    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mMessagesList;

    private final List<Messages>messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;

    private static final int TOTAL_ITEMS_TO_LOAD = 10;

    private static final int  GALLERY_PICK = 1;
    private  int mCurrentPage = 1 ;

    private int itemPos = 0;
    private String mLastKey = "";
    private String mPrevKey = "";



    // variables for edit / deletePage

    int contextMenuIndexClicked = -1;
    boolean isEditmode =  false;
    TextView editMessage;

//adding image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            Uri imageUri  = data.getData();


            final String current_user_ref =  "messages/" + mCurrentUserId + "/" + mChatuser;

            final String chat_user_ref =  "messages/" + mChatuser + "/" + mCurrentUserId;

            DatabaseReference user_messege_push = mRootref.child("messsages")
                    .child(mCurrentUserId).child(mChatuser).push();

            final String push_id = user_messege_push.getKey();

            StorageReference filepath = mImageStorage.child("messages_image").child(push_id +".jpg");

            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()){

                        String download_uri = task.getResult().getDownloadUrl().toString();


                        Map messageMap = new HashMap();
                        messageMap.put("message",download_uri);
                        messageMap.put("seen",false);
                        messageMap.put("type","images");
                        messageMap.put("time",ServerValue.TIMESTAMP);
                        messageMap.put("from", mCurrentUserId);

                        Map messgeUserMap = new HashMap();
                        messgeUserMap.put(current_user_ref + "/" + push_id,messageMap);
                        messgeUserMap.put(chat_user_ref + "/" + push_id,messageMap);


                        mChatMessageView.setText("");

                        mRootref.updateChildren(messgeUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null){
                                Log.d("CHAT_LOG: ",databaseError.getMessage().toString());
                                                    }
                            }
                        });

                    }
                }
            });

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
         
        
//        Aesthetic.attach(this); // MUST come before super.onCreate(...)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId =  mAuth.getCurrentUser().getUid();


        mchatToolbar = (Toolbar) findViewById(R.id.chat_app_bar);
        setSupportActionBar(mchatToolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }



        getSupportActionBar().setDisplayShowCustomEnabled(true);

        mRootref = FirebaseDatabase.getInstance().getReference();

        mChatuser = getIntent().getStringExtra("user_id");
        String userName = getIntent().getStringExtra("user_name");// username on chat

        getSupportActionBar().setTitle(userName);

        LayoutInflater inflater =  (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionbar_view  =inflater.inflate(R.layout.chat_custom_bar, null);

        getSupportActionBar().setCustomView(actionbar_view);

        //-- CUSTOM ACTION BAR ITEM ---------

        mTitleView = (TextView) findViewById(R.id. chat_custom_display_name);
        mLastSeen  = ( TextView) findViewById(R.id.Last_seen_chat_custom);
        mProfile = ( CircleImageView) findViewById(R.id.custom_bar_image);
        mChatAddBtn = (ImageButton)findViewById(R.id.chat_add_btn);
        mChatSendBtn = (ImageButton) findViewById(R.id. chat_send_btn);
        mChatMessageView = (EditText)findViewById(R.id.chat_meesage_view);

        //back button
//        ImageView backArrow = (ImageView) findViewById(R.id.imageButton2);
//        backArrow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
        mAdapter =  new MessageAdapter(messagesList);


        mMessagesList =  (RecyclerView) findViewById(R.id.messages_list);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_message);
        mLinearLayout = new LinearLayoutManager(this);

        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);

        mMessagesList.setAdapter(mAdapter);

        mImageStorage = FirebaseStorage.getInstance().getReference();

        mRefreshLayout.setColorSchemeResources(R.color.bg_screen1,R.color.colorLight,R.color.colorPrimaryDark);

        //add context  menu
        registerForContextMenu(mMessagesList);

        loadMessages();


        mTitleView.setText(userName);

        mRootref.child("Users").child(mChatuser).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mRootref.keepSynced(false);//can remove

                String online = dataSnapshot.child("online").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();




                if (online.equals("true") ){

                    mLastSeen.setText("online");
                }else {

                    GetLastSeen getLastSeen = new GetLastSeen();

                    long lastTime = Long.parseLong(online);
                    String lastSeenTime =  getLastSeen.getTimeAgo(lastTime,  getApplicationContext());

                    mLastSeen.setText(lastSeenTime);


                    //  loading image into custom custombar
                    Picasso.with(ChatActivity.this).load(image).placeholder(R.drawable.avatar).into(mProfile);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mRootref.child("Chat").child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(mChatuser)){

                    Map chatAddMap  = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp" , ServerValue.TIMESTAMP);


                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/ " + mCurrentUserId + "/" + mChatuser, chatAddMap);
                    chatUserMap.put("Chat/" + mChatuser + "/" + mCurrentUserId, chatAddMap);

                    mRootref.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError !=null){

                                Log.d("CHAT_LOG", databaseError.getMessage().toString());


                            }
                        }
                    });


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mChatAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"),GALLERY_PICK);


            }
        });




        // chatsendbtn
        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendMessage();

            }
        });

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mCurrentPage++;

                //messagesList.clear();
                itemPos = 0;

                loadMoreMessages();

            }
        });

    }

    private void loadMoreMessages() {
        DatabaseReference messageRef = mRootref.child("messages").child(mCurrentUserId).child(mChatuser);
        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages message = dataSnapshot.getValue(Messages.class);

                String messageKey = null;

                if (!mPrevKey.equals(messageKey)){
                    messagesList.add(itemPos++, message);

                }else {
                    mPrevKey = mLastKey;

                }
                if (itemPos == 1) {

                    messageKey = dataSnapshot.getKey();

                    mLastKey = messageKey;

                }


                Log.d("TotalKeys", "Last Key: " + mLastKey + "| Prev Key : " + mPrevKey + "|Message Key : " + messageKey);


                mAdapter.notifyDataSetChanged();

                mRefreshLayout.setRefreshing(false);
                mLinearLayout.scrollToPositionWithOffset(10, 0);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    // Load messages method
    private void loadMessages() {
        DatabaseReference messageRef = mRootref.child("messages").child(mCurrentUserId).child(mChatuser);
        Query messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);


        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages message = dataSnapshot.getValue(Messages.class);

                itemPos++;

                if (itemPos == 1){
                    String messageKey = dataSnapshot.getKey();

                    mLastKey=messageKey;
                    mPrevKey =  messageKey;
                }


                messagesList.add(message);
                mAdapter.notifyDataSetChanged();

                mMessagesList.scrollToPosition(messagesList.size()-1);
                mRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    // send message

    private void sendMessage() {
        String message = mChatMessageView.getText().toString();

        if (!TextUtils.isEmpty(message)){

            String current_user_ref = "messages/" + mCurrentUserId + "/" + mChatuser;
            String chat_user_ref = "messages/" + mChatuser + "/" + mCurrentUserId;

            DatabaseReference user_message_push = mRootref.child("messages")
                    .child(mCurrentUserId).child(mChatuser).push();

            String push_id =  user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put( "message", message);
            messageMap.put( "seen", false );
            messageMap.put( "type", "text"); //or image/emoji/video
            messageMap.put( "time", ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentUserId);


            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

            mChatMessageView.setText(""); // clear text from field

            mRootref.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if (databaseError != null){

                        Log.d("CHAT_LONG " , databaseError.getMessage().toString() );


                    }

                }
            });


        }


        // checks to
        if (mAuth.getCurrentUser() != null) {

            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        }

    }




    @Override
    protected void onResume() {
        super.onResume();
        Aesthetic.resume(this);
    }

    @Override
    protected void onPause() {
        Aesthetic.pause(this);
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.themes,menu);


        return true;
    }

    // Menu items
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == android.R.id.home)
            finish();


        if (item.getItemId() == R.id.black_theme){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Aesthetic.get()
                        .colorNavigationBarAuto()
                        .colorWindowBackground(getColor(R.color.blackBg))
                        .apply();
            }

        }

        if (item.getItemId() == R.id.theme){
            Aesthetic.get()
                    .colorWindowBackgroundRes(R.color.lightBlue)
                    .colorPrimary(getColor(R.color.bg_screen3))
                    .apply();

        }
        if (item.getItemId() == R.id.theme1){
            Aesthetic.get()
                    .colorWindowBackgroundRes(R.color.white)
                    .colorStatusBarAuto()
//                    .colorStatusBar(R.color.lightGreen)
                    .apply();

        }





        return true;
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//
//            //can add onstop and start to each activity add later
//
//            mUserRef.child("online").setValue(false);
//        }
    /*  @Override
        protected void onStart() {
        super.onStart();

        //checks if  user is signed in (non-null ) and update Ui according
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {

//            sendTostart();

        }else {

            mUserRef.child("online").setValue(true);
        }
    }

   @Override
    protected void onStop() {
        super.onStop();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {

            //can add onstop and start to each activity add later

            mUserRef.child("online").setValue(false);
        }
    }*/

}
