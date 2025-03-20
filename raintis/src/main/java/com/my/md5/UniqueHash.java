package com.my.md5;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UniqueHash {
    public static String createSHA256Hash(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(data.getBytes());
            byte[] digest = md.digest();
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not supported", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static void main(String[] args) {
        String data = "some unique data";
        String hash = createSHA256Hash(data);
        System.out.println("SHA-256 Hash: " + hash);
        System.out.println(hash.length());
    }
}
