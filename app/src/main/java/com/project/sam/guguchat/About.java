package com.project.sam.guguchat;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class About extends AppCompatActivity {
    private  Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Toolbar
        // mToolbar = (Toolbar) findViewById(R.id.about_app_bar);
        //    setSupportActionBar(mToolbar);
        //    getSupportActionBar().setTitle("About");
        //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        // About libray starts here
        simulateDayNight(/* DAY */ 0);
        Element adsElement = new Element();

        adsElement.setTitle("Ad here");
        View about = new AboutPage(this)

                .isRTL(false)
                .setImage(R.drawable.chat_ic)
                .setDescription("About")

                .addItem(new Element().setTitle("Version 1.0"))

                .addItem(adsElement)

                .addGroup("Connect with me")

                .addEmail("edrilyckxs@gmail.com")

                .addWebsite("add website ")

                .addFacebook("my facebook")

                .addTwitter("d3yday")

                .addYoutube("youtube ")

                .addPlayStore("My PlayStore")

                .addInstagram("My Instagram")

                .addGitHub("my Github")

                .addItem(createCopyright())

                .create();
        setContentView(R.layout.activity_about);
        LinearLayout mActivityRoot = ((LinearLayout) findViewById(R.id.about_view)); // our host view
        mActivityRoot.addView(about,0/2); // Add about-page view to position 1 (since 0 is already taken by the toolbar)


    }



    private Element createCopyright() {

        final Element copyright = new Element();

        final String copyrightString = String.format("Copyright %d by Sam", Calendar.getInstance().get(Calendar.YEAR));

        copyright.setTitle(copyrightString);
        copyright.setIconDrawable(R.drawable.ic_stat_name);
        copyright.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color);
        copyright.setIconNightTint(android.R.color.white);
        copyright.setGravity(Gravity.CENTER);



        copyright.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {

                Toast.makeText(About.this,copyrightString, Toast.LENGTH_SHORT).show();

            }

        });

        return copyright;

    }
    //Night mode
    void simulateDayNight(int currentSetting) {
        final int DAY = 0;
        final int NIGHT = 1;
        final int FOLLOW_SYSTEM = 3;

        int currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        if (currentSetting == DAY && currentNightMode != Configuration.UI_MODE_NIGHT_NO) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);
        } else if (currentSetting == NIGHT && currentNightMode != Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);
        } else if (currentSetting == FOLLOW_SYSTEM) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }


}
