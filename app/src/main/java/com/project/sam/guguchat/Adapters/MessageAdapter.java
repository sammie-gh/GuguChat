package com.project.sam.guguchat.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.sam.guguchat.Messages;
import com.project.sam.guguchat.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by A.Richard on 29/07/2017.
 */

public class MessageAdapter extends RecyclerView
        .Adapter<MessageAdapter
        .MessageViewHolder>{



    private List<Messages> mMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    public MessageAdapter(List<Messages> messagesList){
        this.mMessagesList = messagesList;


        mAuth = FirebaseAuth.getInstance(); //Initailizing



    }

   @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent,  int viewType ){

       View v = LayoutInflater.from(parent.getContext())
               .inflate(R.layout.message_single_layout , parent, false);


       return new MessageViewHolder(v);


   }

   public class MessageViewHolder extends  RecyclerView.ViewHolder{
        public TextView  messagesText;

        public CircleImageView profileImage;
       public TextView displayName;
       public ImageView messageimage;



        public MessageViewHolder( View view){
            super(view);

            messagesText = (TextView) view.findViewById(R.id.message_text_layout);

            profileImage = (CircleImageView) view.findViewById(R.id.message_single_profile);
            displayName = (TextView) view.findViewById(R.id.message_Display_name);
            messageimage = (ImageView) view.findViewById(R.id.message_image_layout);


        }

   }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, final int position) {


        Messages c = mMessagesList.get(position);


        String from_user = c.getFrom();
        String message_type = c.getType();


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);


        //getting uid
        String current_user_id = mAuth.getCurrentUser().getUid();


        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();

                viewHolder.displayName.setText(name);

                Picasso.with(viewHolder.profileImage.getContext()).load(image)
                        .placeholder(R.drawable.default_avatar).into(viewHolder.profileImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if (message_type.equals("text")){
            viewHolder.messagesText.setText(c.getMessage());
            viewHolder.messageimage.setVisibility(View.INVISIBLE);
        }else {
            viewHolder.messagesText.setVisibility(View.INVISIBLE);

            Picasso.with(viewHolder.profileImage.getContext()).load(c.getMessage())
                    .placeholder(R.drawable.default_avatar).into(viewHolder.messageimage);
        }
    }

    @Override
    public int getItemCount() {
        return mMessagesList.size();

    }

}
