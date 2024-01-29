package com.example.project1;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CryptoFetcher extends AsyncTask<Void, Void, List<Crypto>> {

    private final OnCryptoFetchListener listener;

    // Constructor to set the listener during instantiation
    public CryptoFetcher(OnCryptoFetchListener listener) {
        this.listener = listener;
    }

    // Background task to fetch cryptocurrency data from the API
    @Override
    protected List<Crypto> doInBackground(Void... voids) {
        List<Crypto> cryptoList = new ArrayList<>();

        // Define default values for error scenario
        String namex = "error";
        String symbolx = "symbol";
        double pricex = 0;
        double priceChangePercentagex = 0;
        String imageUrlx = "image";
        double low24hx = 0;
        double high24hx = 0;
        double priceChange24hx = 0;
        double caprankx = 0;
        Crypto errorcrypto = new Crypto(namex, symbolx, pricex, priceChangePercentagex, imageUrlx, low24hx, high24hx, priceChange24hx, caprankx);

        try {
            // Establish connection to the CoinGecko API
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

                // Create a Crypto object and add it to the list
                Crypto crypto = new Crypto(name, symbol, price, priceChangePercentage, imageUrl, low24h, high24h, priceChange24h, caprank);
                cryptoList.add(crypto);
            }
        } catch (IOException | JSONException e) {
            // Handle exceptions, add the error crypto to the list, and return
            e.printStackTrace();
            cryptoList.add(errorcrypto);
            return cryptoList;
        }

        return cryptoList;
    }

    // Execute post-fetch operations on the UI thread
    @Override
    protected void onPostExecute(List<Crypto> cryptoList) {
        if (listener != null) {
            listener.onCryptoFetched(cryptoList);
        }
    }

    // Interface for the callback when cryptocurrency data is fetched
    public interface OnCryptoFetchListener {
        void onCryptoFetched(List<Crypto> cryptoList);
    }
}
