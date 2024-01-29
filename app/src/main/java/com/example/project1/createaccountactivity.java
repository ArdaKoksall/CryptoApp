package com.example.project1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class createaccountactivity extends AppCompatActivity {
    // EditTexts for user input
    private EditText fullNameEditText, emailEditText, passwordEditText, confirmPasswordEditText;

    // TextViews for error messages and validation control
    private TextView fullNameControlTextView, emailControlTextView, passwordControlTextView, confirmPasswordControlTextView;

    // ImageViews for displaying ticks and crosses for validation control
    private ImageView fullNameTick, emailTick, passwordTick, confirmPasswordTick;
    private ImageView fullNameCross, emailCross, passwordCross, confirmPasswordCross;

    // Firebase Authentication instance
    private FirebaseAuth mAuth;

    // Booleans for input validation
    private boolean namebool;
    private boolean emailbool;
    private boolean passbool;
    private boolean confirmpassbool;

    // Firebase Database reference
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createaccountactivity);
        //databaseReference = FirebaseDatabase.getInstance("YOUR DATABASE LINK").getReference();
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI elements

        // Edit Texts
        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);

        // Text Views
        fullNameControlTextView = findViewById(R.id.fullNameControlTextView);
        emailControlTextView = findViewById(R.id.emailControlTextView);
        passwordControlTextView = findViewById(R.id.passwordControlTextView);
        confirmPasswordControlTextView = findViewById(R.id.confirmPasswordControlTextView);

        // Ticks
        fullNameTick = findViewById(R.id.fullNameTick);
        emailTick = findViewById(R.id.emailTick);
        passwordTick = findViewById(R.id.passwordTick);
        confirmPasswordTick = findViewById(R.id.confirmPasswordTick);

        // Crosses
        fullNameCross = findViewById(R.id.fullNameCross);
        emailCross = findViewById(R.id.emailCross);
        passwordCross = findViewById(R.id.passwordCross);
        confirmPasswordCross = findViewById(R.id.confirmPasswordCross);

        // User enter visibility settings
        fullNameControlTextView.setVisibility(View.INVISIBLE);
        emailControlTextView.setVisibility(View.INVISIBLE);
        passwordControlTextView.setVisibility(View.INVISIBLE);
        confirmPasswordControlTextView.setVisibility(View.INVISIBLE);

        // Sign-up button
        Button signUpButton = findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(view -> signUp());

        // Booleans initialization
        namebool = false;
        emailbool = false;
        passbool = false;
        confirmpassbool = false;

        // Set focus change listeners

        // Listener-1
        fullNameEditText.setOnFocusChangeListener((view, b) -> checkName(b));

        // Listener-2
        emailEditText.setOnFocusChangeListener((view, c) -> checkEmail(c));

        // Listener-3
        passwordEditText.setOnFocusChangeListener((view, d) -> checkPassword(d));

        // Listener-4
        confirmPasswordEditText.setOnFocusChangeListener((view, e) -> checkConfirmPassword(e));
    }

    //Checkers
    private void checkName(boolean b1){
        if(!b1){
            String fullName = fullNameEditText.getText().toString().trim();
            if(fullName.isEmpty()){
                namebool = false;
                showErrorName("Name can't be empty");
            }
            else{
                // Check name length
                if (fullName.length() < 3 || fullName.length() > 16) {
                    namebool = false;
                    showErrorName("Name should be 3-16 characters");
                }
                else{
                    namebool = true;
                    showTrueName();
                }
            }
        }
    }

    private void checkEmail(boolean c1){
        if(!c1){
            String email = emailEditText.getText().toString().trim();
            if(email.isEmpty()){
                emailbool = false;
                showErrorEmail("Email can't be empty");
            }
            else{
                if (isEmailValid(email)) {
                    FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    SignInMethodQueryResult result = task.getResult();
                                    List<String> signInMethods = result.getSignInMethods();
                                    if (signInMethods != null && !signInMethods.isEmpty()) {
                                        emailbool = false;
                                        showErrorEmail("Email already exists.");
                                    } else {
                                        emailbool = true;
                                        showTrueEmail();
                                    }
                                }
                            });

                }
                else{
                    emailbool = false;
                    showErrorEmail("Invalid Email");
                }
            }
        }
    }

    private void checkPassword(boolean d1){
        if(!d1){
            String password = passwordEditText.getText().toString().trim();
            if(password.isEmpty()){
                passbool = false;
                showErrorPassword("Password can't be empty");
            }
            else{
                if (password.length() < 6 || password.length() > 16) {
                    passbool = false;
                    showErrorPassword("Password should be 6-16 characters");
                }
                else{
                    passbool = true;
                    showTruePassword();
                }
            }
        }
    }

    private void checkConfirmPassword(boolean e1){
        if(!e1){
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();
            if(confirmPassword.isEmpty()){
                confirmpassbool = false;
                showErrorConfirmPassword("Password can't be empty");
            }
            else{
                if (confirmPassword.length() < 6 || confirmPassword.length() > 16) {
                    confirmpassbool = false;
                    showErrorConfirmPassword("Password should be 6-16 characters");
                }
                else{
                    String password = passwordEditText.getText().toString().trim();
                    if(password.isEmpty()){
                        showErrorPassword("Password can't be empty");
                        confirmpassbool = false;
                        passbool = false;
                    }
                    else if (password.equals(confirmPassword)) {
                        confirmpassbool = true;
                        showTrueConfirmPassword();
                    }
                    else{
                        showErrorConfirmPassword("Passwords don't match.");
                        confirmpassbool = false;
                    }
                }
            }
        }
    }


    //error messages
    private void showErrorName(String message){
        fullNameTick.setVisibility(View.GONE);
        fullNameControlTextView.setText(message);
        fullNameControlTextView.setVisibility(View.VISIBLE);
        fullNameCross.setVisibility(View.VISIBLE);
    }
    private void showErrorEmail(String message){
        emailTick.setVisibility(View.GONE);
        emailControlTextView.setText(message);
        emailControlTextView.setVisibility(View.VISIBLE);
        emailCross.setVisibility(View.VISIBLE);
    }
    private void showErrorPassword(String message){
        passwordTick.setVisibility(View.GONE);
        passwordControlTextView.setText(message);
        passwordControlTextView.setVisibility(View.VISIBLE);
        passwordCross.setVisibility(View.VISIBLE);
    }
    private void showErrorConfirmPassword(String message){
        confirmPasswordTick.setVisibility(View.GONE);
        confirmPasswordControlTextView.setText(message);
        confirmPasswordControlTextView.setVisibility(View.VISIBLE);
        confirmPasswordCross.setVisibility(View.VISIBLE);
    }

    //True messages
    private void showTrueName(){
        fullNameControlTextView.setVisibility(View.INVISIBLE);
        fullNameCross.setVisibility(View.GONE);
        fullNameTick.setVisibility(View.VISIBLE);
    }
    private void showTrueEmail(){
        emailControlTextView.setVisibility(View.INVISIBLE);
        emailCross.setVisibility(View.GONE);
        emailTick.setVisibility(View.VISIBLE);
    }
    private void showTruePassword(){
        passwordControlTextView.setVisibility(View.INVISIBLE);
        passwordCross.setVisibility(View.GONE);
        passwordTick.setVisibility(View.VISIBLE);
    }
    private void showTrueConfirmPassword(){
        confirmPasswordControlTextView.setVisibility(View.INVISIBLE);
        confirmPasswordCross.setVisibility(View.GONE);
        confirmPasswordTick.setVisibility(View.VISIBLE);
    }

    //Others

    public boolean isEmailValid(String email2) {
        // Define a regular expression pattern for a basic email validation
        String emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}";

        // Compile the pattern
        Pattern pattern = Pattern.compile(emailPattern);

        // Create a Matcher object
        Matcher matcher = pattern.matcher(email2);

        // Check if the provided text matches the pattern
        return matcher.matches();
    }
    public void signUp() {

        // Get user input
        boolean s = false;
        String fullName = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        //Last checking
        checkName(s);
        checkPassword(s);
        checkEmail(s);
        checkConfirmPassword(s);

        // Continue with Firebase Authentication here to create a user account
        if(namebool && emailbool && passbool && confirmpassbool){
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Create a User instance
                            String newemail = email.replace(".", "!");
                            writeUserData(newemail, fullName, 1000);
                            Toast.makeText(this, "Account created.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            // Sign up failed, handle the error
                            Toast.makeText(this, "Unknown error.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void writeUserData(String userEmail, String userName, int usdBalance) {
        // Create a JSON object for the wallet
        Map<String, Object> wallet = new HashMap<>();
        wallet.put("USD", usdBalance);
        // You can remove the entries for EUR and BTC if you don't want them

        // Create a new user object with name, email, and wallet
        Map<String, Object> user = new HashMap<>();
        user.put("Name", userName);
        user.put("Email", userEmail);
        user.put("Wallet", wallet);

        // Write the user data to the database
        databaseReference.child("Users").child(userEmail).setValue(user);
    }

}
