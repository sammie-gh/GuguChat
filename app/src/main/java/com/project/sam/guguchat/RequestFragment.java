package com.project.sam.guguchat;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import info.hoang8f.widget.FButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {

    private RecyclerView myRequestlist;

    private View myMAinView;
    private DatabaseReference FriendsRequestRef;
    private FirebaseAuth mAuth;

    private   String mCurrent_user_id;

     private DatabaseReference userRef,FriendDatabaseRef,FriendreqDatabaseRef;
     private FButton btnAccept,btnDecline;




    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myMAinView= inflater.inflate(R.layout.fragment_request, container, false);

        myRequestlist = (RecyclerView) myMAinView.findViewById(R.id.request_list);

        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        FriendsRequestRef = FirebaseDatabase.getInstance().getReference().child("Friend_request").child(mCurrent_user_id);


        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        FriendDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        FriendreqDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friend_request");









        myRequestlist.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager =  new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        myRequestlist.setLayoutManager(linearLayoutManager);

        // Inflate the layout for this fragment

        return myMAinView;
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Request, RequestViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Request, RequestViewHolder>(
                        Request.class,
                R.layout.request_layout,
                RequestFragment.RequestViewHolder.class,
                FriendsRequestRef


        ) {
            @Override
            protected void populateViewHolder(final RequestViewHolder viewHolder, Request model, int position) {

                final  String list_user_id  = getRef(position).getKey();

                DatabaseReference get_type = getRef(position).child("req_type").getRef();

                get_type.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String request_type = dataSnapshot.getValue().toString();

                            if (request_type.equals("received"))
                            {



                                userRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot)
                                    {

                                        final String userName = dataSnapshot.child("name").getValue().toString(); //get username from db
                                        final String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                                        final    String userStatus = dataSnapshot.child("status").getValue().toString(); // adding status to friends list

                                        FButton req_sent_btn = (FButton) viewHolder.mView.findViewById(R.id.btnCancel);

                                        req_sent_btn.setButtonColor(getResources().getColor(R.color.colorPrimary));
                                        req_sent_btn.setShadowColor(getResources().getColor(R.color.colorAccent));
                                        req_sent_btn.setText("Request received from " +userName );

                                        viewHolder.setUsername(userName);
                                        viewHolder.setStatus(userStatus);
                                        viewHolder.setThumb_image(userThumb ,getContext());

                                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                CharSequence options[] =
                                                        new CharSequence[]

                                        {"Accept Friend Request from " + userName,
                                        "Cancel Friend Request from " + userName};// remove the username if crashes

                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                                builder.setTitle("Friend  Request Options");
                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        //Click Event for each item

                                                        if (which == 0){
                                                            Calendar calForDate = Calendar.getInstance();
                                                            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                                                            final String saveCurrentDate= currentDate.format(calForDate.getTime());


                                                            FriendDatabaseRef.child(mCurrent_user_id).child(list_user_id).child("date").setValue(saveCurrentDate)
                                                          .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                              @Override
                                                              public void onSuccess(Void aVoid) {
                                                                  FriendDatabaseRef.child(list_user_id).child(mCurrent_user_id).child("date").setValue(saveCurrentDate)
                                                                  .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                      @Override
                                                                      public void onSuccess(Void aVoid) {
                                                                          FriendsRequestRef.child(mCurrent_user_id).child(list_user_id).removeValue()
                                                                                  .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                      @Override
                                                                                      public void onComplete(@NonNull Task<Void> task) {
                                                                                          if (task.isSuccessful())
                                                                                          {
                                                                                              FriendreqDatabaseRef.child(list_user_id).child(mCurrent_user_id).removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful())
                                                                                                {
                                                                                                    Toasty.success(getContext(), "Friend request accepted from" + userName , Toast.LENGTH_SHORT).show(); // remove username if not working well

                                                                                                }

                                                                                            }
                                                                                        }) ;
                                                                                          }
                                                                                      }
                                                                                  });

                                                                      }
                                                                  });


                                                              }
                                                          });

                                                        }
                                                        if (which == 1){

                                                            // Cancel for position 1
                                                            FriendreqDatabaseRef.child(mCurrent_user_id).child(list_user_id).removeValue()
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {

                                                                            FriendreqDatabaseRef.child(mCurrent_user_id).child(list_user_id).removeValue()
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {

                                                                                    Toasty.warning(getContext(), "Friend request cancelled from " + userName , Toast.LENGTH_SHORT).show(); // remove username if not working well



                                                                                }
                                                                            });

                                                                        }
                                                                    });


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
                            else  if (request_type.equals("sent"))
                            {
                                userRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                      final  String userName = dataSnapshot.child("name").getValue().toString(); //get username from db
                                       // viewHolder.setUsername(userName);

                                        FButton req_sent_btn = (FButton) viewHolder.mView.findViewById(R.id.btnCancel);
                                        req_sent_btn.setButtonColor(getResources().getColor(R.color.colorPrimaryDark));
                                        req_sent_btn.setShadowColor(getResources().getColor(R.color.colorAccent));
                                        req_sent_btn.setText("Request Sent to " + userName);

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {


                                    }
                                })   ;



                               // viewHolder.mView.findViewById(R.id.btnCancel).setVisibility(View.INVISIBLE);



                                userRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot)
                                    {

                                        final String userName = dataSnapshot.child("name").getValue().toString(); //get username from db
                                        final String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                                        final    String userStatus = dataSnapshot.child("status").getValue().toString(); // adding status to friends list

                                        viewHolder.setUsername(userName);
                                        viewHolder.setStatus(userStatus);
                                        viewHolder.setThumb_image(userThumb ,getContext());

                                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                CharSequence options[] =
                                                        new CharSequence[]

                                                                {

                                                                        "Cancel Friend Request from " + userName,

                                                                };// remove the username if crashes

                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                                builder.setTitle("Friend Request Sent");
                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        //Click Event for each item

                                                        if (which == 0){

                                                            // Cancell for position 1
                                                            FriendreqDatabaseRef.child(mCurrent_user_id).child(list_user_id).removeValue()
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {

                                                                            FriendreqDatabaseRef.child(mCurrent_user_id).child(list_user_id).removeValue()
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {

                                                                                            Toasty.error(getContext(), "Friend request cancelled from " + userName , Toast.LENGTH_SHORT).show(); // remove username if not working well


                                                                                        }
                                                                                    });

                                                                        }
                                                                    });


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
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });






            }
        };
        myRequestlist.setAdapter(firebaseRecyclerAdapter);
    }


    public static  class RequestViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public RequestViewHolder(View itemView) {
            super(itemView);

            mView = itemView;


        }


        public void setUsername(String userName) {

            TextView userNameDisplay = (TextView) mView.findViewById(R.id.request_name);
            userNameDisplay.setText(userName); //inisde this we have the getter from firebase


        }

        public void setThumb_image(String userThumb, final Context ctx) {

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.request_image);

            Picasso.with(ctx).load(userThumb).placeholder(R.drawable.default_avatar).into(userImageView);//if not work implement other 
        }

        public void setStatus(String userStatus) {

            TextView status = (TextView) mView.findViewById(R.id.request_status);
            status.setText(userStatus);
        }
    }




}
