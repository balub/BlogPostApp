package com.example.balu.blogpostapp;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

   // private android.support.v7.widget.Toolbar maintoolbar;
   private FirebaseAuth mAuth;
   private FloatingActionButton addPostBtn;
   private BottomNavigationView bottomNavigationView;
   private android.support.v4.app.Fragment homeFragment;
   private NotificationFragment notificationFragment;
   private android.support.v4.app.Fragment accountFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // maintoolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.main_toolbar);
        //       // setSupportActionBar(maintoolbar);
        bottomNavigationView=(BottomNavigationView)findViewById(R.id.bottomNavigationView);
        mAuth = FirebaseAuth.getInstance();
        getSupportActionBar().setTitle("Blog APP");
        addPostBtn = (FloatingActionButton)findViewById(R.id.add_post_btn);
        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AddPostActivity.class);
                startActivity(intent);
            }
        });

        homeFragment= new HomeFragment();
        notificationFragment = new NotificationFragment();
        accountFragment = new AccountFragment();
        replaceFragment(homeFragment);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.bottom_nav_account:
                        replaceFragment(accountFragment);
                        return true;
                    case R.id.bottom_nav_home:
                        replaceFragment(homeFragment);
                        return true;
                    case R.id.bottom_nav_notifi:
                        replaceFragment(notificationFragment);
                        return true;
                        default:
                        return false;
                }


            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            //Toast.makeText(MainActivity.this,"Login Error..",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_logout_btn:
                LogOut();
                return true;
            case R.id.action_settings_btn:
                 UserSettings();
                return true;


                default:
                    return false;
        }
    }

    private void UserSettings() {
        Intent intent = new Intent(MainActivity.this, SetupActivity.class);
        startActivity(intent);
    }

    private void LogOut() {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private void replaceFragment(android.support.v4.app.Fragment fragment){
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container,fragment);
        fragmentTransaction.commit();

    }
}
