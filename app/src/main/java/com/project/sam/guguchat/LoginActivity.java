package com.project.sam.guguchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import es.dmoral.toasty.Toasty;


public class LoginActivity extends AppCompatActivity {


    Button forget,login, forgotButton;
    AlphaAnimation fadeIn, fadeOut;
    LinearLayout loginForm, forgotForm;

    private Toolbar mToolbar;
    private EditText mLoginEmail;
    private  EditText mLoginPassword, recoverEmail;
    private Button mLogin_btn;

    private FirebaseAuth mAuth;

    private ProgressDialog mLoginProgress;
    private DatabaseReference mUserDatabase;

    private static LinearLayout loginLayout;

    private static Animation shakeAnimation;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginLayout = (LinearLayout) findViewById(R.id.Frame_login);
        // Load ShakeAnimation
        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake
        );

        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login");

        mLoginProgress = new ProgressDialog(this);

        login = (Button) findViewById(R.id.login);
        loginForm = (LinearLayout) findViewById(R.id.loginForm);
        forgotForm = (LinearLayout) findViewById(R.id.forgotForm);
        recoverEmail = (EditText) findViewById(R.id.recoverEmail);
        forget = (Button) findViewById(R.id.forgot);
        forgotButton = (Button) findViewById(R.id.forgotButton);

        //Amimation
        fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setStartOffset(100);
        fadeOut.setDuration(500);

        calAction();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mLogin_btn = (Button) findViewById(R.id.login_btn);
        mLoginProgress = new ProgressDialog(this);
        mLoginEmail = (EditText) findViewById(R.id.login_email_txt);
        mLoginPassword = (EditText) findViewById(R.id.login_password_txt);

        mLogin_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                checkLogin();

            }

            //check login method to fix crash
            private void checkLogin() {

                boolean valid = true;

                String email = mLoginEmail.getText().toString();
                String password = mLoginPassword.getText().toString();
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

                    mLoginProgress.setTitle("Logging In");
                    mLoginProgress.setMessage("Please wait while we check your credentials.");
                    mLoginProgress.setCanceledOnTouchOutside(false);

                    mLoginProgress.show();
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if ((task.isSuccessful())) {
                                mLoginProgress.dismiss();

                                //add checkuser
                                checkUserExist();

                                String current_user_id = mAuth.getCurrentUser().getUid();
                                String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                });

                            } else {
                                mLoginProgress.hide();
                                Toasty.error(LoginActivity.this, "Cannot sign in. please check and try again", Toast.LENGTH_SHORT).show();


                            }


                        }
                    });
                }


                /// new code
                // Check for both field is empty or not
                if (email.equals("") || email.length() == 0) {
                    loginLayout.startAnimation(shakeAnimation);

                    Toasty.error(LoginActivity.this, "Check Email field", Toast.LENGTH_SHORT).show();

                } else if (password.equals("") || password.length() == 0) {
                    loginLayout.startAnimation(shakeAnimation);

                    Toasty.error(LoginActivity.this, "Check Password field", Toast.LENGTH_SHORT).show();
                } else if (email.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    mLoginEmail.setError("enter a valid email address");
                    valid = false;
                } else {
                    mLoginEmail.setError(null);
                }

                if (password.isEmpty() || password.length() < 6 || password.length() > 12) {
                    mLoginPassword.setError("between 6 and 12 alphanumeric characters");
                    valid = false;

                } else {
                    mLoginPassword.setError(null);

                }
            }


        });
    }



    @Override
    //onStart method for login
    protected void onStart() {
        super.onStart();
        checkUserExist();

    }


    private void checkUserExist() {

        if (mAuth.getCurrentUser() != null){
            final String user_id = mAuth.getCurrentUser().getUid();

            mUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //not working then change with !
                    if (dataSnapshot.hasChild(user_id)) {

                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);
                        finish();


                    } else {
                        Toast.makeText(LoginActivity.this, " User not found pls register", Toast.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    private void calAction() {

        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        loginForm.setVisibility(View.GONE);
                        showForget();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                loginForm.startAnimation(fadeOut);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        forgotForm.setVisibility(View.GONE);
                        showLogin();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                forgotForm.startAnimation(fadeOut);
            }
        });

        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void showForget() {
        fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(500);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                clearForms();
                forgotForm.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        forgotForm.setAnimation(fadeIn);
    }

    public void showLogin() {
        fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(500);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                clearForms();
                loginForm.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        loginForm.setAnimation(fadeIn);
    }

    public void clearForms() {
        mLoginEmail.setText("");
        mLoginPassword.setText("");
        recoverEmail.setText("");
    }

    //To be removed


}














