package com.example.project1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CryptoAdapter extends RecyclerView.Adapter<CryptoAdapter.ViewHolder> {
    private List<Crypto> cryptoList;
    private Context context;

    // Constructor to initialize the adapter with context and data
    public CryptoAdapter(Context context, List<Crypto> cryptoList) {
        this.context = context;
        this.cryptoList = cryptoList;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for a single item and return the ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.crypto_layout, parent, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data model based on position
        Crypto crypto = cryptoList.get(position);

        // Bind data to the ViewHolder
        holder.symbolTextView.setText(crypto.getSymbol().toUpperCase());
        holder.priceTextView.setText(String.valueOf(crypto.getPrice()) + "$"); // Convert double to String

        // Set text color based on price change percentage
        if (crypto.getPriceChangePercentage() > 0) {
            holder.priceChange24hTextView.setTextColor(ContextCompat.getColor(context, R.color.green));
            holder.priceChange24hTextView.setText("+" + String.valueOf(crypto.getPriceChangePercentage()) + "%");
        } else if (crypto.getPriceChangePercentage() < 0) {
            holder.priceChange24hTextView.setTextColor(ContextCompat.getColor(context, R.color.red));
            holder.priceChange24hTextView.setText("-" + String.valueOf(crypto.getPriceChangePercentage()) + "%");
        } else {
            holder.priceChange24hTextView.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.priceChange24hTextView.setText(String.valueOf(crypto.getPriceChangePercentage()) + "%");
        }

        // Load image using Glide
        Glide.with(context).load(crypto.getImageUrl()).into(holder.cryptoImageView);

        // Set item click listener to open detailed crypto screen
        holder.itemView.setOnClickListener(view -> {
            CryptoHolder.setCrypto(crypto);
            Intent intent = new Intent(context, cryptoscreen.class);
            context.startActivity(intent);
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return cryptoList.size();
    }

    // ViewHolder class to hold references to the views for each item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView symbolTextView;
        TextView priceTextView;
        TextView priceChange24hTextView;
        ImageView cryptoImageView;

        // Constructor to initialize the ViewHolder with the view
        public ViewHolder(View itemView) {
            super(itemView);
            symbolTextView = itemView.findViewById(R.id.symbolTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            priceChange24hTextView = itemView.findViewById(R.id.priceChangeTextView);
            cryptoImageView = itemView.findViewById(R.id.cryptoImageView);
        }
    }
}
