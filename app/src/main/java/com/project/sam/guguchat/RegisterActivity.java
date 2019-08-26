package com.project.sam.guguchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

import static com.project.sam.guguchat.R.id.reg_create_btn;

public class RegisterActivity extends AppCompatActivity {

    // Firebase Auth
    private FirebaseAuth mAuth;
    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mCreateBtn;

    private DatabaseReference mDatabase,mUserDatabase;

    //progress
    private ProgressDialog mRegProgress;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Toolbar set
        mToolbar = (Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRegProgress = new ProgressDialog(this);



        // Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mDisplayName = (TextInputLayout) findViewById(R.id.reg_display_name);
        mEmail =(TextInputLayout) findViewById(R.id.reg_email);
        mPassword = (TextInputLayout) findViewById(R.id.reg_password);
        mCreateBtn = (Button) findViewById(reg_create_btn);

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                boolean valid = true;

                String display_name = mDisplayName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                if (!TextUtils.isEmpty(display_name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){

                    if (display_name.isEmpty() || display_name.length() < 3) {
                        mDisplayName.setError("at least 3 characters");
                        valid = false;
                    } else {
                        mDisplayName.setError(null);
                    }

                    if (password.isEmpty() || password.length() < 6 || password.length() > 12) {
                        mPassword.setError("between 6and 12 alphanumeric characters");
                        valid = false;
                    } else {
                        mPassword.setError(null);
                    }

                    if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        mEmail.setError("enter a valid email address");
                        valid = false;
                    } else {
                        mEmail.setError(null);
                    }





                    mRegProgress.setTitle("Registering User");
                    mRegProgress.setMessage("Please Wait While we create your account ! ");
                    mRegProgress.show();
                    mRegProgress.setCanceledOnTouchOutside(false);



                    register_user(display_name, email , password);
                }

            }
        });
    }

    private void register_user(final String display_name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){


                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String  uid = current_user.getUid();

                    //adding images,status,display_name
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("name",display_name);
                    userMap.put("status","Wassup !, Im on Gugu chat App.");
                    userMap.put("image","default");
                    userMap.put("thumb_image","default");

                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                mRegProgress.dismiss(); // must be moved to  top of task.is....

                                String current_user_id = mAuth.getCurrentUser().getUid();

                                String deviceToken = FirebaseInstanceId.getInstance().getToken();


                                mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {


                                        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish();

                                    }
                                });


                            }
                        }
                    });


                }else {
                    mRegProgress.hide();
                    Toast.makeText(RegisterActivity.this,"Cannot sign in. please check and try again" ,Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
}
