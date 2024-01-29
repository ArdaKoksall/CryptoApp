package com.example.project1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

public class walletpage extends AppCompatActivity {
    RecyclerView recyclerView;
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    Context context;
    CryptoAdapter2 adapter;
    ImageButton profilepage, mainpage;
    TextView wallet;
    Double totalvalue, usdvalue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walletpage);

        totalvalue = 0.00;
        recyclerView = findViewById(R.id.walletRecyclerView);
        context = walletpage.this;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        profilepage = findViewById(R.id.profileButton);
        mainpage = findViewById(R.id.mainPageButton);
        wallet = findViewById(R.id.profileTitle);
        auth = FirebaseAuth.getInstance();
        DecimalFormat decimalFormat = new DecimalFormat("0.0000");
        FirebaseUser user = auth.getCurrentUser();
        String emailuser = user.getEmail().toString();
        String emailuser2 = emailuser.replace(".", "!");

        //databaseReference = FirebaseDatabase.getInstance("YOUR DATABASE LINK").getReference();
        
        DatabaseReference userref = databaseReference.child("Users").child(emailuser2).child("Wallet").child("USD");
        DatabaseReference coinref = databaseReference.child("Users").child(emailuser2).child("Wallet").child("Total");

        userref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalvalue = snapshot.getValue(Double.class);
                usdvalue = snapshot.getValue(Double.class);
                coinref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                        if(snapshot2.exists()){
                            totalvalue = totalvalue + snapshot2.getValue(Double.class);
                            wallet.setText("Wallet balance: | USD balance:  " + decimalFormat.format(totalvalue) + "$  | " + decimalFormat.format(usdvalue) + "$" );
                        }
                        else{
                            wallet.setText("Wallet balance: " + decimalFormat.format(totalvalue) + "$ | USD balance: " + decimalFormat.format(usdvalue));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        @SuppressLint("SetTextI18n")
        CryptoFetcher2 cryptoFetcher = new CryptoFetcher2(context, cryptoList2 -> {
            adapter = new CryptoAdapter2(context, cryptoList2);
            recyclerView.setAdapter(adapter);
        });
        cryptoFetcher.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        


        profilepage.setOnClickListener(view -> {
            Intent intent = new Intent(this, profilepage.class);
            startActivity(intent);
            finish();
        });

        mainpage.setOnClickListener(view -> {
            Intent intent2 = new Intent(this, crypto_mainpage.class);
            startActivity(intent2);
            finish();
        });

    }


}