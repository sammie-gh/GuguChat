package com.project.sam.guguchat;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;

import es.dmoral.toasty.Toasty;

public class StartActivity extends AppCompatActivity {

    RelativeLayout startlayout;
    AnimationDrawable animationDrawable;

    private Button mRegbtn;
    private Button mLogbtn;
    private  ImageView mimage2;
    private ImageView mImage;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mRegbtn = (Button) findViewById(R.id.start_reg_btn);
        mLogbtn  = (Button) findViewById(R.id.start_login_btn);
        //start backg. animation
        startlayout =(RelativeLayout) findViewById(R.id.start_text_wlc);

        animationDrawable= (AnimationDrawable)startlayout.getBackground();
        animationDrawable.setEnterFadeDuration(4500);
        animationDrawable.setExitFadeDuration(4500);
        animationDrawable.start();



        final ImageView mimage2 = (ImageView) findViewById(R.id.start_image2);
        final ImageView mImage = (ImageView) findViewById(R.id.start_image);
        final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(10000L);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                final float width = mImage.getWidth();
                final float translationX = width * progress;
                mImage.setTranslationX(translationX);
                mimage2.setTranslationX(translationX - width);
            }
        });
        animator.start();

        mRegbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg_intent = new Intent( StartActivity.this, RegisterActivity.class);
                startActivity(reg_intent);
            }
        });


        mLogbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent log_intent = new Intent( StartActivity.this, LoginActivity.class);
                startActivity(log_intent);
            }
        });

        //Ripple effects

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        // making notification bar transparent
        changeStatusBarColor();

    }


    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }



}
