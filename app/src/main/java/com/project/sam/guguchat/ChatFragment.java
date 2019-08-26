package com.project.sam.guguchat;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {
    private RecyclerView myChatList;
    private View myChatView;

    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;

    private String mCurrent_user_id;


    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myChatView = inflater.inflate(R.layout.fragment_chat, container, false);

        myChatList = (RecyclerView) myChatView.findViewById(R.id.chats_list);

        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);


        myChatList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        myChatList.setLayoutManager(linearLayoutManager);

        return myChatView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Chats, ChatFragment.ChatsViewHolder> friendRecyclerViewAdapter =
                new FirebaseRecyclerAdapter<Chats, ChatsViewHolder>(

                Chats.class,
                R.layout.user_single,
                ChatFragment.ChatsViewHolder.class,
                mFriendsDatabase
        ) {
            @Override
            protected void populateViewHolder(final ChatFragment.ChatsViewHolder friendsViewHolder, Chats friends, int position) {

                //friendsViewHolder.setDate(friends.getDate());//changet to model
                // renable to add date

                //can be changed to set date
                final String list_user_id = getRef(position).getKey();
                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString(); //get username from db
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                        String userStatus = dataSnapshot.child("status").getValue().toString(); // adding status to friends list

                        if (dataSnapshot.hasChild("online")){

                            String userOnline =  dataSnapshot.child("online").getValue().toString();
                            friendsViewHolder.setUserOnline(userOnline);

                        }


                        friendsViewHolder.setName(userName);
                        friendsViewHolder.setUserImage(userThumb, getContext());
                        friendsViewHolder.userStatus(userStatus);

                        friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent ChatIntent = new Intent(getContext() , ChatActivity.class);
                                    ChatIntent.putExtra("user_id", list_user_id);
                                    ChatIntent.putExtra("user_name",userName);
                                    startActivity(ChatIntent);


                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        };

        myChatList.setAdapter(friendRecyclerViewAdapter);

    }




    public static class ChatsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ChatsViewHolder(View itemView) {

            super(itemView);
            mView = itemView;

        }

        /// i changed from setdate to userStatus
        public void userStatus(String userStatus) {

            TextView userStatusView = (TextView) mView.findViewById(R.id.usr_single_status);
            userStatusView.setText(userStatus);
        }



        public void setName(String name) {

            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);


        }

        public void setUserImage(String thumb_image, Context ctx) {

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);

            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);

        }


        public void setUserOnline(String online_status) {

            //test to change icon color

            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.user_online);

            if (online_status.equals("true")) {

                userOnlineView.setVisibility(View.VISIBLE);
            } else {
                userOnlineView.setVisibility(View.INVISIBLE);
            }

        }
    }




    }
