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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.List;

public class CryptoAdapter2 extends RecyclerView.Adapter<CryptoAdapter2.ViewHolder> {
    private List<Crypto> cryptoList;
    private Context context;

    public CryptoAdapter2(Context context, List<Crypto> cryptoList) {
        this.context = context;
        this.cryptoList = cryptoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.crypto_layout, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Crypto crypto = cryptoList.get(position);
        DecimalFormat decimalFormat = new DecimalFormat("0.0000");

        // Firebase authentication and database references
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String emailuser = user.getEmail().toString();
        String emailuser2 = emailuser.replace(".", "!");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://coinmarket-28dff-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        DatabaseReference coinref = databaseReference.child("Users").child(emailuser2).child("Wallet").child("Cryptos").child(crypto.getSymbol());
        DatabaseReference coinprofit = databaseReference.child("Users").child(emailuser2).child("Wallet").child("Cryptos").child(crypto.getSymbol() + "_buyvalue");

        coinref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double coinquantity = snapshot.getValue(Double.class);

                coinprofit.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                        Double buyvalue = snapshot2.getValue(Double.class);
                        Double netprofit = crypto.getPrice() - buyvalue;
                        netprofit = netprofit * coinquantity;
                        Double uservalue = crypto.getPrice() * coinquantity;

                        // Display cryptocurrency details in the UI
                        holder.priceTextView.setText(String.valueOf(crypto.getPrice()) + "$" + " |You have: " + decimalFormat.format(uservalue) + "$");

                        if((netprofit > 0)){
                            holder.priceChange24hTextView.setTextColor(ContextCompat.getColor(context, R.color.green));
                            holder.priceChange24hTextView.setText("Quantity: " + decimalFormat.format(coinquantity) + "|  Profit: " + decimalFormat.format(netprofit) + "$");
                        }
                        else if((netprofit < 0)){
                            holder.priceChange24hTextView.setTextColor(ContextCompat.getColor(context, R.color.red));
                            holder.priceChange24hTextView.setText("Quantity: " + decimalFormat.format(coinquantity) + "|  Profit: " + decimalFormat.format(netprofit) + "$");
                        }
                        else{
                            holder.priceChange24hTextView.setTextColor(ContextCompat.getColor(context, R.color.white));
                            holder.priceChange24hTextView.setText("Quantity: " + decimalFormat.format(coinquantity) + "|  Profit: " + decimalFormat.format(netprofit) + "$");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Display basic cryptocurrency information
        holder.symbolTextView.setText(crypto.getSymbol().toUpperCase());

        // Load image using Glide
        Glide.with(context).load(crypto.getImageUrl()).into(holder.cryptoImageView);

        // Set item click listener to open detailed crypto screen
        holder.itemView.setOnClickListener(view -> {
            CryptoHolder.setCrypto(crypto);
            Intent intent = new Intent(context, cryptoscreen.class);
            context.startActivity(intent);
        });
    }

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
