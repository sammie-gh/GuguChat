package com.project.sam.guguchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;


public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ViewPager mViewpager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private DatabaseReference mUserRef;

    private TabLayout mTabsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        //Toolbar
        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Gugu Chat");

        //add to all activities to make it work

        if (mAuth.getCurrentUser() != null) {


            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        }


//An array containing your icons from the drawable directory
        final int[] ICONS = new int[]{
                R.drawable.ic_new_releases_white_24dp,
                R.drawable.ic_chat_white_24dp,
                R.drawable.ic_people_white_24dp,


        };

        //Tabs
        mViewpager = (ViewPager) findViewById(R.id.main_tabpager);
        mSectionsPagerAdapter =  new SectionsPagerAdapter(getSupportFragmentManager());

        mViewpager.setAdapter(mSectionsPagerAdapter);

        mTabsLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabsLayout.setupWithViewPager(mViewpager);
//setting icons
        mTabsLayout.getTabAt(0).setIcon(ICONS[0]);
        mTabsLayout.getTabAt(1).setIcon(ICONS[1]);
        mTabsLayout.getTabAt(2).setIcon(ICONS[2]);

    }


    @Override
    public void onStart() {
        super.onStart();
        //checks if  user is signed in (non-null ) and update Ui according
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {

            sendTostart();

        }else {

            mUserRef.child("online").setValue(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {

            //can add onstop and start to each activity add later

            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

        }
    }

    private void sendTostart() {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu,menu);


        return true;
    }

    // Menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.main_logout_btn){
            FirebaseAuth.getInstance().signOut();
            sendTostart();

        }
        if(item.getItemId() == R.id.main_setting_btn){

            Intent settingIntent = new Intent(MainActivity.this,SettingsActivitity.class);
            startActivity(settingIntent);
        }

        if (item.getItemId()== R.id.main_all_btn){

            Intent settingIntent = new Intent(MainActivity.this,AllUsersActivity.class);
            startActivity(settingIntent);



        }

        if(item.getItemId()== R.id.about_menu){

            Intent aboutIntent = new Intent(MainActivity.this,About.class);
            startActivity(aboutIntent);
        }

        if(item.getItemId()== R.id.Hub){

        }


        return true;
    }
}
