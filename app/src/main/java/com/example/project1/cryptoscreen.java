package com.example.project1;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class cryptoscreen extends AppCompatActivity {

    ImageView cryptoimageview;
    TextView cryptonametextview, cryptosymboltextview, cryptopricetextview, cryptopricechangetextview, cryptohighlowtextview;
    Button buybutton, sellbutton, hourbutton, daybutton, weekbutton, monthbutton;
    Crypto crypto;
    WebView chartWebView;
    LoadingDialog loadingDialog;
    String url;

    FirebaseAuth auth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cryptoscreen);

        crypto = CryptoHolder.getCrypto();
        setview();
        setcrypto();
    }

    @SuppressLint({"SetTextI18n", "SetJavaScriptEnabled"})
    public void setcrypto(){
        Glide.with(this).load(crypto.getImageUrl()).into(cryptoimageview);
        //1H D W M
        chartWebView.getSettings().setJavaScriptEnabled(true);
        url = "https://s.tradingview.com/widgetembed/?frameElementId=tradingview_76d87&symbol=" + crypto.getSymbol()
                + "USD&interval=" + "D" + "&hidesidetoolbar=1&hidetoptoolbar=1&symboledit=1&saveimage=1&toolbarbg=F1F3F6&studies=[]&hideideas=1&theme=Dark&style=1&timezone=Etc%2FUTC&studies_overrides={}&overrides={}&enabled_features=[]&disabled_features=[]&locale=en&utm_source=coinmarketcap.com&utm_medium=widget&utm_campaign=chart&utm_term=BTCUSDT";
        chartWebView.setWebViewClient(new MyWebViewClient());
        hourbutton.setTextColor(ContextCompat.getColor(this, R.color.white));
        daybutton.setTextColor(ContextCompat.getColor(this, R.color.orange));
        weekbutton.setTextColor(ContextCompat.getColor(this, R.color.white));
        monthbutton.setTextColor(ContextCompat.getColor(this, R.color.white));
        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        chartWebView.loadUrl(url);
        cryptonametextview.setText(crypto.getName());
        cryptosymboltextview.setText(crypto.getSymbol());
        cryptopricetextview.setText(crypto.getPrice() + "$");
        if(crypto.getPriceChangePercentage() > 0){
            cryptopricechangetextview.setText("Price Change (24h): " + crypto.getPriceChange24h() + " / " + "+%" + crypto.getPriceChangePercentage());
            cryptopricechangetextview.setTextColor(ContextCompat.getColor(this, R.color.green));
        }
        else  if(crypto.getPriceChangePercentage() < 0){
            cryptopricechangetextview.setText("Price Change (24h): " + crypto.getPriceChange24h() + " / " + "-%" + crypto.getPriceChangePercentage());
            cryptopricechangetextview.setTextColor(ContextCompat.getColor(this, R.color.red));
        }
        else{
            cryptopricechangetextview.setText("Price Change (24h): " + crypto.getPriceChange24h() + " / " + "%" + crypto.getPriceChangePercentage());
            cryptopricechangetextview.setTextColor(ContextCompat.getColor(this, R.color.white));
        }
        cryptohighlowtextview.setText("High: " + crypto.getHigh24h() + " | Low: " + crypto.getLow24h()) ;
        hourbutton.setOnClickListener(view -> {
            hourbutton.setTextColor(ContextCompat.getColor(this, R.color.orange));
            daybutton.setTextColor(ContextCompat.getColor(this, R.color.white));
            weekbutton.setTextColor(ContextCompat.getColor(this, R.color.white));
            monthbutton.setTextColor(ContextCompat.getColor(this, R.color.white));
            loadingDialog.show();
             url = "https://s.tradingview.com/widgetembed/?frameElementId=tradingview_76d87&symbol=" + crypto.getSymbol()
                    + "USD&interval=" + "1H" + "&hidesidetoolbar=1&hidetoptoolbar=1&symboledit=1&saveimage=1&toolbarbg=F1F3F6&studies=[]&hideideas=1&theme=Dark&style=1&timezone=Etc%2FUTC&studies_overrides={}&overrides={}&enabled_features=[]&disabled_features=[]&locale=en&utm_source=coinmarketcap.com&utm_medium=widget&utm_campaign=chart&utm_term=BTCUSDT";
             chartWebView.loadUrl(url);
        });
        daybutton.setOnClickListener(view -> {
            hourbutton.setTextColor(ContextCompat.getColor(this, R.color.white));
            daybutton.setTextColor(ContextCompat.getColor(this, R.color.orange));
            weekbutton.setTextColor(ContextCompat.getColor(this, R.color.white));
            monthbutton.setTextColor(ContextCompat.getColor(this, R.color.white));
            loadingDialog.show();
            url = "https://s.tradingview.com/widgetembed/?frameElementId=tradingview_76d87&symbol=" + crypto.getSymbol()
                    + "USD&interval=" + "D" + "&hidesidetoolbar=1&hidetoptoolbar=1&symboledit=1&saveimage=1&toolbarbg=F1F3F6&studies=[]&hideideas=1&theme=Dark&style=1&timezone=Etc%2FUTC&studies_overrides={}&overrides={}&enabled_features=[]&disabled_features=[]&locale=en&utm_source=coinmarketcap.com&utm_medium=widget&utm_campaign=chart&utm_term=BTCUSDT";
            chartWebView.loadUrl(url);
        });
        weekbutton.setOnClickListener(view -> {
            hourbutton.setTextColor(ContextCompat.getColor(this, R.color.white));
            daybutton.setTextColor(ContextCompat.getColor(this, R.color.white));
            weekbutton.setTextColor(ContextCompat.getColor(this, R.color.orange));
            monthbutton.setTextColor(ContextCompat.getColor(this, R.color.white));
            loadingDialog.show();
            url = "https://s.tradingview.com/widgetembed/?frameElementId=tradingview_76d87&symbol=" + crypto.getSymbol()
                    + "USD&interval=" + "W" + "&hidesidetoolbar=1&hidetoptoolbar=1&symboledit=1&saveimage=1&toolbarbg=F1F3F6&studies=[]&hideideas=1&theme=Dark&style=1&timezone=Etc%2FUTC&studies_overrides={}&overrides={}&enabled_features=[]&disabled_features=[]&locale=en&utm_source=coinmarketcap.com&utm_medium=widget&utm_campaign=chart&utm_term=BTCUSDT";
            chartWebView.loadUrl(url);

        });
        monthbutton.setOnClickListener(view -> {
            hourbutton.setTextColor(ContextCompat.getColor(this, R.color.white));
            daybutton.setTextColor(ContextCompat.getColor(this, R.color.white));
            weekbutton.setTextColor(ContextCompat.getColor(this, R.color.white));
            monthbutton.setTextColor(ContextCompat.getColor(this, R.color.orange));
            loadingDialog.show();
            url = "https://s.tradingview.com/widgetembed/?frameElementId=tradingview_76d87&symbol=" + crypto.getSymbol()
                    + "USD&interval=" + "M" + "&hidesidetoolbar=1&hidetoptoolbar=1&symboledit=1&saveimage=1&toolbarbg=F1F3F6&studies=[]&hideideas=1&theme=Dark&style=1&timezone=Etc%2FUTC&studies_overrides={}&overrides={}&enabled_features=[]&disabled_features=[]&locale=en&utm_source=coinmarketcap.com&utm_medium=widget&utm_campaign=chart&utm_term=BTCUSDT";
            chartWebView.loadUrl(url);
        });
        buybutton.setOnClickListener(view -> showBuyOptionsDialog());
        sellbutton.setOnClickListener(view -> showSellOptionsDialog());


    }


    @SuppressLint("InflateParams")
    private void showBuyOptionsDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        final AlertDialog dialogView = new AlertDialog.Builder(this).create();
        final EditText amountEditText;
        final RadioButton quantityRadioButton;
        final RadioButton moneyRadioButton;

        dialogView.setView(inflater.inflate(R.layout.dialog_buy_options, null));
        dialogView.show();

        Button buyButton = dialogView.findViewById(R.id.buyButton);
        amountEditText = dialogView.findViewById(R.id.amountEditText);
        quantityRadioButton = dialogView.findViewById(R.id.quantityRadioButton);
        moneyRadioButton = dialogView.findViewById(R.id.moneyRadioButton);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String emailuser = user.getEmail().toString();
        String emailuser2 = emailuser.replace(".", "!");
        //databaseReference = FirebaseDatabase.getInstance("YOUR DATABASE LINK").getReference();
        DatabaseReference userref = databaseReference.child("Users").child(emailuser2).child("Wallet").child("USD");

// Retrieve the current USD value from the database
        userref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double currentUsdValue = dataSnapshot.getValue(Double.class);

                // Handle the buy button click
                buyButton.setOnClickListener(v -> {
                    String amount = amountEditText.getText().toString();
                    if (quantityRadioButton.isChecked()) {
                        String quantityStr = amountEditText.getText().toString();
                        if (!TextUtils.isEmpty(quantityStr)) {
                            Double quantityToBuy = Double.parseDouble(quantityStr);

                            if (quantityToBuy >= 1) { // Validate the quantity
                                Double cryptoPrice = crypto.getPrice();
                                // Calculate the total cost of the purchase
                                Double totalCost = quantityToBuy * cryptoPrice;

                                if (totalCost <= currentUsdValue) {
                                    // Deduct the total cost from the user's wallet
                                    Double newUsdValue = currentUsdValue - totalCost;

                                    // Update the user's USD value in the database
                                    userref.setValue(newUsdValue);

                                    // Retrieve the user's existing quantity of the crypto from the database
                                    DatabaseReference cryptoQuantityRef = databaseReference.child("Users").child(emailuser2).child("Wallet").child("Cryptos").child(crypto.getSymbol());

                                    cryptoQuantityRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Long currentCryptoQuantity = dataSnapshot.getValue(Long.class);

                                            // Update the quantity of the purchased crypto in the database
                                            cryptoQuantityRef.setValue(currentCryptoQuantity != null ? currentCryptoQuantity + quantityToBuy : quantityToBuy);

                                            DatabaseReference buytime = databaseReference.
                                                    child("Users").
                                                    child(emailuser2).child("Wallet")
                                                    .child("Cryptos").
                                                    child(crypto.getSymbol() + "_buyvalue");
                                            buytime.setValue(crypto.getPrice());

                                            // Transaction successful
                                            Toast.makeText(cryptoscreen.this, "Transaction successful", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            // Handle database error
                                            Toast.makeText(cryptoscreen.this, "Database error", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(cryptoscreen.this, "Insufficient funds", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(cryptoscreen.this, "Invalid quantity", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(cryptoscreen.this, "Please enter a valid quantity", Toast.LENGTH_SHORT).show();
                        }
                    }

                    else if (moneyRadioButton.isChecked()) {
                            String amountStr = amountEditText.getText().toString();
                            if (!TextUtils.isEmpty(amountStr)) {
                                Double amountToSpend = Double.parseDouble(amountStr);

                                if (amountToSpend > 0 && amountToSpend <= currentUsdValue) {
                                    // Calculate the quantity of cryptocurrency based on the amount to spend
                                    Double cryptoPrice = crypto.getPrice();
                                    Double quantityToBuy = amountToSpend / cryptoPrice;

                                    // Deduct the spent amount from the user's wallet
                                    Double newUsdValue = currentUsdValue - amountToSpend;
                                    userref.setValue(newUsdValue);

                                    // Retrieve the user's existing quantity of the crypto from the database
                                    DatabaseReference cryptoQuantityRef = databaseReference.child("Users").child(emailuser2).child("Wallet").child("Cryptos").child(crypto.getSymbol());

                                    cryptoQuantityRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Double currentCryptoQuantity = dataSnapshot.getValue(Double.class);

                                            // Update the quantity of the purchased crypto in the database
                                            cryptoQuantityRef.setValue(currentCryptoQuantity != null ? currentCryptoQuantity + quantityToBuy : quantityToBuy);

                                            DatabaseReference buytime = databaseReference.
                                                    child("Users").
                                                    child(emailuser2).child("Wallet")
                                                    .child("Cryptos").
                                                    child(crypto.getSymbol() + "_buyvalue");
                                            buytime.setValue(crypto.getPrice());

                                            // Transaction successful
                                            Toast.makeText(cryptoscreen.this, "Transaction successful", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            // Handle database error
                                            Toast.makeText(cryptoscreen.this, "Database error", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(cryptoscreen.this, "Invalid amount or insufficient funds", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(cryptoscreen.this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                            }

                            // Close the dialog if needed
                            dialogView.dismiss();
                    }

                    else {
                        Toast.makeText(cryptoscreen.this, "Please choose an option", Toast.LENGTH_SHORT).show();
                    }

                    // Close the dialog if needed
                    dialogView.dismiss();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(cryptoscreen.this, "Database error", Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void showSellOptionsDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        final AlertDialog dialogView = new AlertDialog.Builder(this).create();
        final EditText amountEditText;
        final RadioButton quantityRadioButtonSell;
        final RadioButton moneyRadioButtonSell;

        dialogView.setView(inflater.inflate(R.layout.dialog_sell_options, null));
        dialogView.show();

        Button sellButton = dialogView.findViewById(R.id.sellButton);
        Button sellAllButton = dialogView.findViewById(R.id.sellAllButton);
        amountEditText = dialogView.findViewById(R.id.amountEditTextSell);
        quantityRadioButtonSell = dialogView.findViewById(R.id.quantityRadioButtonSell);
        moneyRadioButtonSell = dialogView.findViewById(R.id.moneyRadioButtonSell);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String emailuser = user.getEmail().toString();
        String emailuser2 = emailuser.replace(".", "!");
        databaseReference = FirebaseDatabase.getInstance("https://coinmarket-28dff-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        DatabaseReference userref = databaseReference.child("Users").child(emailuser2).child("Wallet").child("USD");

        // Retrieve the current USD value from the database
        userref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double currentUsdValue = dataSnapshot.getValue(Double.class);
                // Handle the sell button click
                sellButton.setOnClickListener(v -> {
                    String amount = amountEditText.getText().toString();
                    if (quantityRadioButtonSell.isChecked()) {
                        String quantityStr = amountEditText.getText().toString();
                        if (!TextUtils.isEmpty(quantityStr)) {
                            Double quantityToSell = Double.parseDouble(quantityStr);

                            if (quantityToSell != 0) { // Validate the quantity
                                DatabaseReference cryptoQuantityRef = databaseReference.child("Users").child(emailuser2).child("Wallet").child("Cryptos").child(crypto.getSymbol());

                                cryptoQuantityRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Double currentCryptoQuantity = dataSnapshot.getValue(Double.class);

                                        if (currentCryptoQuantity != null && quantityToSell <= currentCryptoQuantity) {
                                            // Calculate the total USD value to receive from selling
                                            Double cryptoPrice = crypto.getPrice();
                                            Double totalSaleValue = quantityToSell * cryptoPrice;

                                            // Add the total sale value to the user's wallet
                                            Double newUsdValue = currentUsdValue + totalSaleValue;
                                            userref.setValue(newUsdValue);

                                            // Update the quantity of the sold crypto in the database
                                            cryptoQuantityRef.setValue(currentCryptoQuantity - quantityToSell);

                                            // Transaction successful
                                            Toast.makeText(cryptoscreen.this, "Transaction successful", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(cryptoscreen.this, "Insufficient cryptocurrency quantity to sell", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Handle database error
                                        Toast.makeText(cryptoscreen.this, "Database error", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(cryptoscreen.this, "Invalid quantity to sell", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(cryptoscreen.this, "Please enter a valid quantity to sell", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else if (moneyRadioButtonSell.isChecked()) {
                        // Handle selling by amount with 'amount'
                        String amountStr = amountEditText.getText().toString();
                        if (!TextUtils.isEmpty(amountStr)) {
                            Double amountToSell = Double.parseDouble(amountStr);

                            if (amountToSell > 0) {
                                DatabaseReference cryptoQuantityRef = databaseReference.
                                        child("Users").
                                        child(emailuser2).
                                        child("Wallet").
                                        child("Cryptos").
                                        child(crypto.getSymbol());

                                cryptoQuantityRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Double currentCryptoQuantity = dataSnapshot.getValue(Double.class);

                                        if (currentCryptoQuantity != null && currentCryptoQuantity > 0) {
                                            // Calculate the quantity of cryptocurrency to sell based on the amount
                                            Double cryptoPrice = crypto.getPrice();
                                            Double quantityToSell = amountToSell / cryptoPrice;

                                            if (quantityToSell <= currentCryptoQuantity) {
                                                // Calculate the total USD value to receive from selling
                                                Double totalSaleValue = amountToSell;

                                                // Add the total sale value to the user's wallet
                                                Double newUsdValue = currentUsdValue + totalSaleValue;
                                                userref.setValue(newUsdValue);

                                                // Update the quantity of the sold crypto in the database
                                                cryptoQuantityRef.setValue(currentCryptoQuantity - quantityToSell);

                                                // Transaction successful
                                                Toast.makeText(cryptoscreen.this, "Transaction successful", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(cryptoscreen.this, "Insufficient cryptocurrency quantity to sell", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(cryptoscreen.this, "You don't have any cryptocurrency to sell", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Handle database error
                                        Toast.makeText(cryptoscreen.this, "Database error", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else {
                                Toast.makeText(cryptoscreen.this, "Invalid amount to sell", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(cryptoscreen.this, "Please enter a valid amount to sell", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(cryptoscreen.this, "Please choose an option", Toast.LENGTH_SHORT).show();
                    }
                    // Close the dialog if needed
                    dialogView.dismiss();
                });


                // Sell All Button
                sellAllButton.setOnClickListener(v -> {
                    DatabaseReference cryptoQuantityRef = databaseReference
                            .child("Users")
                            .child(emailuser2)
                            .child("Wallet")
                            .child("Cryptos")
                            .child(crypto.getSymbol());

                    cryptoQuantityRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Double currentCryptoQuantity = dataSnapshot.getValue(Double.class);

                            if (currentCryptoQuantity != null && currentCryptoQuantity > 0) {
                                // Calculate the total USD value to receive from selling all cryptocurrency
                                Double cryptoPrice = crypto.getPrice();
                                Double totalSaleValue = currentCryptoQuantity * cryptoPrice;

                                // Add the total sale value to the user's wallet
                                Double newUsdValue = currentUsdValue + totalSaleValue;
                                userref.setValue(newUsdValue);

                                // Update the quantity of the sold crypto in the database to zero
                                cryptoQuantityRef.setValue(0.0);

                                // Transaction successful
                                Toast.makeText(cryptoscreen.this, "Transaction successful", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(cryptoscreen.this, "You don't have any cryptocurrency to sell", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle database error
                            Toast.makeText(cryptoscreen.this, "Database error", Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Close the dialog if needed
                    dialogView.dismiss();
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(cryptoscreen.this, "Database error", Toast.LENGTH_SHORT).show();
            }
        });
    }








    public void setview(){
        cryptoimageview = findViewById(R.id.cryptoImageView);
        chartWebView = findViewById(R.id.webchart);
        cryptonametextview = findViewById(R.id.cryptoNameTextView);
        cryptosymboltextview = findViewById(R.id.cryptoSymbolTextView);
        cryptopricetextview = findViewById(R.id.cryptoPriceTextView);
        cryptopricechangetextview = findViewById(R.id.cryptoPriceChangeTextView);
        cryptohighlowtextview = findViewById(R.id.cryptoHighLowTextView);

        buybutton = findViewById(R.id.buyButton);
        sellbutton = findViewById(R.id.sellButton);
        hourbutton = findViewById(R.id.hourButton);
        daybutton = findViewById(R.id.dayButton);
        weekbutton = findViewById(R.id.weekButton);
        monthbutton = findViewById(R.id.monthButton);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            loadingDialog.dismiss();
        }
    }




}