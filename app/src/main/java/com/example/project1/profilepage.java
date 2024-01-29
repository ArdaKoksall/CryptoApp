package com.example.project1;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class profilepage extends AppCompatActivity {

    FirebaseAuth auth;
    DatabaseReference databaseReference;

    TextView email;


    EditText transferid, transferamount;

    Button logout, transfer;
    ImageButton mainpagebutton, walletbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilepage);

        email = findViewById(R.id.useremailtextview);
        transferid = findViewById(R.id.userIdEditText);
        transferamount = findViewById(R.id.amountEditText);
        logout = findViewById(R.id.logoutButton);
        transfer = findViewById(R.id.transferButton);
        mainpagebutton = findViewById(R.id.mainPageButton);
        walletbutton = findViewById(R.id.walletButton);

        if(checkconnect()){

            auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            String emailuser = user.getEmail().toString();
            email.setText(emailuser);
            String emailuser2 = emailuser.replace(".", "!");
            //databaseReference = FirebaseDatabase.getInstance("YOUR DATABASE LINK").getReference();
            DatabaseReference senderWalletRef = databaseReference.child("Users").child(emailuser2).child("Wallet").child("USD");
            transfer.setOnClickListener(view -> senderWalletRef.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot senderDataSnapshot) {
                    Double senderCurrentUSDValue = senderDataSnapshot.getValue(Double.class);

                    if (senderCurrentUSDValue != null) {
                        String receiverEmail = transferid.getText().toString();

                        if (!receiverEmail.isEmpty()) {
                            String receiverEmail2 = receiverEmail.replace(".", "!");

                            if (!transferamount.getText().toString().isEmpty()) {
                                Double sendAmount = Double.parseDouble(transferamount.getText().toString());

                                DatabaseReference receiverWalletRef = databaseReference.child("Users").child(receiverEmail2).child("Wallet").child("USD");

                                receiverWalletRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot receiverDataSnapshot) {
                                        Double receiverCurrentUSDValue = receiverDataSnapshot.getValue(Double.class);

                                        if (receiverDataSnapshot.exists() && receiverCurrentUSDValue != null) {
                                            if (sendAmount <= senderCurrentUSDValue) {
                                                // Update sender's wallet
                                                senderWalletRef.setValue(senderCurrentUSDValue - sendAmount);

                                                // Update receiver's wallet
                                                receiverWalletRef.setValue(receiverCurrentUSDValue + sendAmount);

                                                Toast.makeText(profilepage.this, "Money sent successfully", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(profilepage.this, "Insufficient funds", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(profilepage.this, "Unknown receiver email", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Handle database error
                                        Toast.makeText(profilepage.this, "Unknown receiver email Error", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(profilepage.this, "Unknown amount", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(profilepage.this, "Unknown receiver email", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database error
                    Toast.makeText(profilepage.this, "Failed to read sender value." + error.toException(), Toast.LENGTH_SHORT).show();
                }
            }));

            logout.setOnClickListener(view -> {
                auth.signOut();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            });

            mainpagebutton.setOnClickListener(view -> {
                Intent intent = new Intent(this, crypto_mainpage.class);
                startActivity(intent);
                finish();
            });

            walletbutton.setOnClickListener(view -> {
                Intent intent = new Intent(this, walletpage.class);
                startActivity(intent);
                finish();
            });


        }


    }

    boolean checkconnect(){
        ConnectivityManager connectivityManager = (ConnectivityManager)  getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null){
            return networkInfo.isConnected();
        }
        else{
            nointernet();
            return false;
        }
    }

    public void nointernet(){
        Intent intent = new Intent(this, nowifiscreen.class);
        intent.putExtra("key", "ProfilePage");
        startActivity(intent);
        finish();
    }
}