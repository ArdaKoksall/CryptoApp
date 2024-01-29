package com.example.project1;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class nowifiscreen extends AppCompatActivity {
    Button tryagain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nowifiscreen);
        tryagain = findViewById(R.id.tryagain);

        tryagain.setOnClickListener(view -> {
            if(checkconnect()){
                Intent intent = getIntent();
                String value = intent.getStringExtra("key");
                if(Objects.equals(value, "MainActivity")){
                    Intent intent2 = new Intent(this, MainActivity.class);
                    startActivity(intent2);
                    finish();
                }
            }
            else{
                Toast.makeText(this, "NO INTERNET CONNECTION.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    boolean checkconnect(){
        ConnectivityManager connectivityManager = (ConnectivityManager)  getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null){
            return networkInfo.isConnected();
        }
        else{
            return false;
        }
    }
}