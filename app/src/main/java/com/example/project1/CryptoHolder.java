package com.example.project1;

public class CryptoHolder {
    private static Crypto cryptoInstance;

    public static void setCrypto(Crypto crypto) {
        cryptoInstance = crypto;
    }

    public static Crypto getCrypto() {
        return cryptoInstance;
    }
}
