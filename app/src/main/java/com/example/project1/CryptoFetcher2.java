package com.example.project1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("StaticFieldLeak")
public class CryptoFetcher2 extends AsyncTask<Void, Void, List<Crypto>> {

    private final OnCryptoFetchListener listener;
    private final Context context;

    private DatabaseReference coinref;

    FirebaseAuth auth;
    FirebaseUser user;
    String emailuser;
    String emailuser2;
    DatabaseReference databaseReference, walletref;

    List<Crypto> cryptoList;
    Double total;

    public CryptoFetcher2(Context context, OnCryptoFetchListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected List<Crypto> doInBackground(Void... voids) {
        total = 0.00;
        cryptoList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        emailuser = user.getEmail().toString();
        emailuser2 = emailuser.replace(".", "!");
        //databaseReference = FirebaseDatabase.getInstance("YOUR DATABASE LINK").getReference();
        walletref = databaseReference.child("Users").child(emailuser2).child("Wallet").child("Total");

        try {
            URL url = new URL("https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read and parse the response
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();


            // Parse JSON data
            JSONArray jsonArray = new JSONArray(response.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject cryptoObject = jsonArray.getJSONObject(i);
                String name = cryptoObject.getString("name");
                String symbol = cryptoObject.getString("symbol");
                double price = cryptoObject.getDouble("current_price");
                double priceChangePercentage = cryptoObject.getDouble("price_change_percentage_24h");
                String imageUrl = cryptoObject.getString("image");
                double low24h = cryptoObject.getDouble("low_24h");
                double high24h = cryptoObject.getDouble("high_24h");
                double priceChange24h = cryptoObject.getDouble("price_change_24h");
                double caprank = cryptoObject.getDouble("market_cap_rank");

                Crypto crypto = new Crypto(name, symbol, price, priceChangePercentage, imageUrl, low24h, high24h, priceChange24h, caprank);

                coinref = databaseReference.child("Users").child(emailuser2).child("Wallet").child("Cryptos").child(crypto.getSymbol());
                coinref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Double quantity = snapshot.getValue(Double.class);
                            if(!(quantity == 0)){
                                cryptoList.add(crypto);
                                calculatetotal(crypto, quantity);
                                notifyListener(cryptoList);
                            }
                            else {
                                calculatetotal(crypto, quantity);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        }
        catch (IOException | JSONException e) {
            e.printStackTrace();
            SharedPreferences preferences = context.getSharedPreferences("CryptoPrefs", Context.MODE_PRIVATE);
            String cachedData = preferences.getString("cachedData", "[]");
            Type listType = new TypeToken<List<Crypto>>() {}.getType();
            List<Crypto> userCryptoList = new Gson().fromJson(cachedData, listType);
            for (Crypto userCrypto : userCryptoList) {
                coinref = databaseReference.child("Users").child(emailuser2).child("Wallet").child("Cryptos").child(userCrypto.getSymbol());
                coinref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Double quantity = snapshot.getValue(Double.class);
                            if (!(quantity == 0)) {
                                cryptoList.add(userCrypto);
                                calculatetotal(userCrypto, quantity);
                                notifyListener(cryptoList);
                            }
                            else {
                                calculatetotal(userCrypto, quantity);
                            }

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
            return cryptoList;
        }
        return cryptoList;
    }

    private void calculatetotal(Crypto crypto, double cryptoquantity){
        walletref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                total = total + (crypto.getPrice() * cryptoquantity);
                walletref.setValue(total);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


    private void notifyListener(List<Crypto> cryptoList) {
        if (listener != null) {
            listener.onCryptoFetched(cryptoList);
        }
    }


    @Override
    protected void onPostExecute(List<Crypto> cryptoList) {
        if (listener != null) {
            listener.onCryptoFetched(cryptoList);
        }
    }

    public interface OnCryptoFetchListener {
        void onCryptoFetched(List<Crypto> cryptoList);
    }



}
