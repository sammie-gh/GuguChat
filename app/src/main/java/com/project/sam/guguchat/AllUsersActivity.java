package com.project.sam.guguchat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private FirebaseAuth mAuth;

    private RecyclerView mallUsersList;
    private DatabaseReference mUserRef;
    private DatabaseReference mUsersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);


        mToolbar = (Toolbar) findViewById(R.id.all_user_appBar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);


        mallUsersList = (RecyclerView ) findViewById(R.id.all_users_list);
        mallUsersList.setHasFixedSize(true);
        mallUsersList.setLayoutManager(new LinearLayoutManager(this));

        //  mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()); // adding pressence


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(
                Users.class,
                R.layout.user_single,
                UsersViewHolder.class,
                mUsersDatabase


        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder usersViewHolder, Users users, int position) {

                usersViewHolder.setName(users.getName());
                usersViewHolder.setStatus(users.getStatus());

                usersViewHolder.setUserImage(users.getThumb_image(), getApplicationContext());

                final String user_id = getRef(position).getKey();


                usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent profileIntent = new Intent(AllUsersActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("user_id", user_id);
                        startActivity(profileIntent);

                    }
                });

            }
        };

        mallUsersList.setAdapter(firebaseRecyclerAdapter);

        // adding presence
        //mUserRef.child("online").setValue(true);
   /* }

    @Override
    protected void onStop() {
        super.onStop();

// reEnable if not working
        //can add onStop and Start to each activity add later

        mUserRef.child("online").setValue(false);

    }*/

    }
    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setName(String name) {

            TextView mUserNameView = (TextView) mView.findViewById(R.id.user_single_name);
            mUserNameView.setText(name);

        }

        public void setStatus(String status) {
            TextView userStatusView = (TextView) mView.findViewById(R.id.usr_single_status);
            userStatusView.setText(status);
        }

        public void setUserImage(String thumb_image, Context ctx){

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);

            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);

        }
    }

}
