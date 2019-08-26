package com.project.sam.guguchat;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import es.dmoral.toasty.Toasty;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextInputLayout mStatus;
    private Button mSavebtn;

    private ProgressDialog mProgress;

    //Firebase

    private DatabaseReference mStatusDatabase;
    private FirebaseUser mCurrentUser;

    //Emojicons
    private EmojIconActions mEmojicon;
    private EmojiconEditText mEmojiconEditText;
    private ImageButton mEmojiButton;
    private   View mRootView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        //firebase

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid =  mCurrentUser.getUid();

        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);



        mToolbar = (Toolbar) findViewById(R.id.status_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Emojicons style
        mRootView = (RelativeLayout) findViewById(R.id.activity_status);
        mEmojiButton = (ImageButton) findViewById(R.id.emoji_status_btn);
        mEmojiconEditText = (EmojiconEditText) findViewById(R.id.emojicon_status_edit);
        mEmojicon = new EmojIconActions(this,mRootView,mEmojiButton,mEmojiconEditText);
        mEmojicon.ShowEmojicon();




        String staus_value= getIntent().getStringExtra("status_value");

        mStatus = (TextInputLayout) findViewById(R.id.status_input);
        mSavebtn =  (Button) findViewById( R.id.save_status_btn);

        mStatus.getEditText().setText(staus_value);

        mSavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //progress
                mProgress = new ProgressDialog(StatusActivity.this);

                mProgress.setTitle("Updating Changes");
                mProgress.setMessage("Almost done !!! ");
                mProgress.show();
                mProgress.setCanceledOnTouchOutside(false);

                String status = mStatus.getEditText().getText().toString();

                mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){

                            mProgress.dismiss();

                        }else {

                            Toasty.error(getApplicationContext(),"Error updating Changes", Toast.LENGTH_SHORT);

                        }

                    }
                });


            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.themes,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;



    }
}
