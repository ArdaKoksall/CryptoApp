package com.example.project1;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    EditText passtext, emailtext;
    Button loginbutton;
    TextView createaccount, forgotpass;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(checkconnect()) {
            auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            if(user != null){
                Intent intent2= new Intent(this, crypto_mainpage.class);
                startActivity(intent2);
                finish();
            }
            createaccount = findViewById(R.id.createAccountTextView);
            createaccount.setOnClickListener(view -> {
                Intent intent = new Intent(this, createaccountactivity.class);
                startActivity(intent);
            });

            forgotpass = findViewById(R.id.forgotPasswordTextView);
            forgotpass.setOnClickListener(view -> {
                emailtext = findViewById(R.id.emailEditText);
                String emailToCheck = emailtext.getText().toString().trim();
                if (emailToCheck.isEmpty()) {
                    Toast.makeText(this, "Please enter your mail.", Toast.LENGTH_SHORT).show();
                } else if (isEmailValid(emailToCheck)) {
                    auth = FirebaseAuth.getInstance();
                    auth.sendPasswordResetEmail(emailToCheck);
                    Toast.makeText(this, "E-mail was sent.", Toast.LENGTH_SHORT).show();
                }
            });

            loginbutton = findViewById(R.id.loginButton);
            loginbutton.setOnClickListener(view -> loginaction());
        }

    }

    public void loginaction(){
        emailtext = findViewById(R.id.emailEditText);
        passtext = findViewById(R.id.passwordEditText);
        auth = FirebaseAuth.getInstance();
        String passtocheck = passtext.getText().toString();
        String emailToCheck = emailtext.getText().toString().trim();
        if(emailToCheck.isEmpty()){
            Toast.makeText( this, "Please enter your mail.", Toast.LENGTH_SHORT).show();
        }
        else if(passtocheck.isEmpty()){
            Toast.makeText(this, "Please enter your password.", Toast.LENGTH_SHORT).show();
        }
         else{
             if(isEmailValid(emailToCheck)){
                 auth.signInWithEmailAndPassword(emailToCheck, passtocheck)
                         .addOnCompleteListener(this, task2 -> {
                             if (task2.isSuccessful()) {
                                 Intent intent2= new Intent(this, crypto_mainpage.class);
                                 startActivity(intent2);
                                 finish();
                             } else {
                                 Toast.makeText( this, "Incorrect mail/password.", Toast.LENGTH_SHORT).show();
                             }
                         });
             }
             else{
                 Toast.makeText(this, "Invalid mail", Toast.LENGTH_SHORT).show();
             }
         }
    }

    public boolean isEmailValid(String email) {
        // Define a regular expression pattern for a basic email validation
        String emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}";

        // Compile the pattern
        Pattern pattern = Pattern.compile(emailPattern);

        // Create a Matcher object
        Matcher matcher = pattern.matcher(email);

        // Check if the provided text matches the pattern
        return matcher.matches();
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
        intent.putExtra("key", "MainActivity");
        startActivity(intent);
        finish();
    }


}