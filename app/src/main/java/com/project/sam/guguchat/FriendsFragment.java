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


public class FriendsFragment extends Fragment {

    private RecyclerView mFriendslist;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);

        mFriendslist = (RecyclerView) mMainView.findViewById(R.id.friend_list);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mFriendsDatabase.keepSynced(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);


        mFriendslist.setHasFixedSize(true);
        mFriendslist.setLayoutManager(new LinearLayoutManager(getContext()));


        // Inflate the layout for this fragment
        return mMainView;

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendRecyclerViewAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(

                Friends.class,
                R.layout.user_single,
                FriendsViewHolder.class,
                mFriendsDatabase
        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder friendsViewHolder, Friends friends, int position) {

                friendsViewHolder.setDate(friends.getDate());
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

                                CharSequence options[] = new CharSequence[]{  userName + "'s profile",
                                        "Send message to " + userName}; //  remove the username if crash

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                builder.setTitle("Select Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        //Click Event for each item

                                        if (which == 0){

                                            Intent profileIntent = new Intent(getContext() , ProfileActivity.class);
                                            profileIntent.putExtra("user_id", list_user_id);
                                            startActivity(profileIntent);

                                        }
                                        if (which == 1){
                                            Intent ChatIntent = new Intent(getContext() , ChatActivity.class);
                                            ChatIntent.putExtra("user_id", list_user_id);
                                            ChatIntent.putExtra("user_name",userName);
                                            startActivity(ChatIntent);


                                        }
                                    }
                                });

                                builder.show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        };

        mFriendslist.setAdapter(friendRecyclerViewAdapter);

    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public FriendsViewHolder(View itemView){

            super(itemView);
            mView = itemView;

        }

        /// i changed from setdate to userStatus
        public void userStatus (String date){

            TextView userStatusView = (TextView) mView.findViewById(R.id.usr_single_status);
            userStatusView.setText(date);
            userStatusView.setVisibility(View.INVISIBLE);
        }

        //Timestamp date to be removed next

        public void setDate (String date){

            TextView userStatusView = (TextView) mView.findViewById(R.id.time_stamp_single);
            userStatusView.setText("became friends Since \n " + date);
            userStatusView.setVisibility(View.VISIBLE);
        }

        public void setName (String name){

            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);


        }

        public void setUserImage(String thumb_image, Context ctx){

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);

            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);

        }


        public void setUserOnline (String online_status){

            //test to change icon color

            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.user_online);

            if (online_status.equals("true")){

                userOnlineView.setVisibility(View.VISIBLE);
            }else {
                userOnlineView.setVisibility(View.INVISIBLE);
            }

        }

        // adding timestamp
          /*public void SetUserTimestamp (boolean time_date){

                TextView  time_stamp =(TextView) mView.findViewById(R.id.time_stamp_single);

                if (time_date == true){

                    time_stamp.setVisibility(View.VISIBLE);

                }else {

                    time_stamp.setVisibility(View.INVISIBLE);

                }
            }*/
    }
}
