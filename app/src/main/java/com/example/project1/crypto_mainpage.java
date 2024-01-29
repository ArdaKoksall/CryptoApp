package com.example.project1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class crypto_mainpage extends AppCompatActivity {
    // RecyclerView and adapters
    private RecyclerView recyclerView;
    private CryptoAdapter adapter;

    // Lists to store cryptocurrency data
    private List<Crypto> cryptoList, cachedCryptoList2;

    // Application context
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crypto_mainpage);

        // Initialize UI elements
        // Image buttons for navigation
        ImageButton accbutton = findViewById(R.id.profileButton);
        ImageButton walletbutton = findViewById(R.id.walletButton);
        EditText searchEditText = findViewById(R.id.searchEditText);

        // Initialize lists and context
        cryptoList = new ArrayList<>();
        cachedCryptoList2 = new ArrayList<>();
        context = crypto_mainpage.this;

        // Initialize RecyclerView and its layout manager
        recyclerView = findViewById(R.id.cryptoListRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Fetch cryptocurrency data asynchronously
        CryptoFetcher cryptoFetcher = new CryptoFetcher(cryptoList2 -> {
            if(cryptoList2.size() == 1){
                // Use cached data if only one crypto is fetched
                cachedCryptoList2 = loadCachedData();
                cryptoList = cachedCryptoList2;
                adapter = new CryptoAdapter(context, cachedCryptoList2);
                recyclerView.setAdapter(adapter);
            }
            else{
                // Use fetched data and cache it
                adapter = new CryptoAdapter(context, cryptoList2);
                cryptoList = cryptoList2;
                recyclerView.setAdapter(adapter);
                cacheData(cryptoList2);
            }
        });
        cryptoFetcher.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        // Add text change listener for search functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Filter the cryptocurrency list based on user's search query
                filterCryptoList(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Set click listeners for navigation buttons
        accbutton.setOnClickListener(view -> {
            Intent intent = new Intent(this, profilepage.class);
            startActivity(intent);
            finish();
        });

        walletbutton.setOnClickListener(view -> {
            Intent intent = new Intent(this, walletpage.class);
            startActivity(intent);
            finish();
        });
    }

    // Method to filter cryptocurrency list based on user's search query
    private void filterCryptoList(String query) {
        List<Crypto> filteredCryptoList = new ArrayList<>();

        for (Crypto crypto : cryptoList) {
            if (crypto.getSymbol().toLowerCase().contains(query)) {
                filteredCryptoList.add(crypto);
            }
        }

        // Sort and update the RecyclerView's data source with the filtered list
        filteredCryptoList.sort(Comparator.comparingDouble(Crypto::getCaprank));
        // Declare your adapter as a member variable
        CryptoAdapter adapter2 = new CryptoAdapter(context, filteredCryptoList);
        recyclerView.setAdapter(adapter2);
    }

    // Method to cache cryptocurrency data
    private void cacheData(List<Crypto> cryptoList) {
        SharedPreferences preferences = context.getSharedPreferences("CryptoPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.putString("cachedData", new Gson().toJson(cryptoList));
        editor.apply();
    }

    // Method to load cached cryptocurrency data
    private List<Crypto> loadCachedData() {
        SharedPreferences preferences = context.getSharedPreferences("CryptoPrefs", Context.MODE_PRIVATE);
        String cachedData = preferences.getString("cachedData", "[]");

        Type listType = new TypeToken<List<Crypto>>() {}.getType();
        return new Gson().fromJson(cachedData, listType);
    }
}
