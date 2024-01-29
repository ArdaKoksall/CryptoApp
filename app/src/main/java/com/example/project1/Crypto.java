package com.example.project1;

public class Crypto {
    private String name, symbol, imageUrl;
    private double price, priceChangePercentage, low24h, high24h, priceChange24h, caprank;

    public Crypto() {
        // Default constructor required for Firebase
    }

    public Crypto(String name,
                  String symbol,
                  double price,
                  double priceChangePercentage,
                  String imageUrl,
                  double low24h,
                  double high24h,
                  double priceChange24h,
                  double caprank){
        this.name = name;
        this.symbol = symbol;
        this.price = price;
        this.priceChangePercentage = priceChangePercentage;
        this.imageUrl = imageUrl;
        this.low24h = low24h;
        this.high24h = high24h;
        this.priceChange24h = priceChange24h;
        this.caprank = caprank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPriceChangePercentage() {
        return priceChangePercentage;
    }

    public void setPriceChangePercentage(double priceChangePercentage) {
        this.priceChangePercentage = priceChangePercentage;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getLow24h() {
        return low24h;
    }

    public void setLow24h(double low24h) {
        this.low24h = low24h;
    }

    public double getHigh24h() {
        return high24h;
    }

    public void setHigh24h(double high24h) {
        this.high24h = high24h;
    }

    public double getPriceChange24h() {
        return priceChange24h;
    }

    public void setPriceChange24h(double priceChange24h) {
        this.priceChange24h = priceChange24h;
    }

    public double getCaprank(){return caprank; }

}
