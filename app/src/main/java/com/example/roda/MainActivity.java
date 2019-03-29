package com.example.roda;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
             try
             {
                sleep(5000);
             }
             catch (Exception e)
             {
                e.printStackTrace();
             }
             finally {
                 Intent welcomeIntent= new Intent(MainActivity.this,WelcomeActivity.class);
                 startActivity(welcomeIntent);
             }

            }
        };
        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        finish();
    }
}
