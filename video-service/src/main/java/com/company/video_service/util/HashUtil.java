package com.company.video_service.util; // Package for utility classes

import java.security.MessageDigest; // Import MessageDigest for hashing

public class HashUtil { // Utility class for hashing operations

    // Computes the SHA-256 hash of a byte array and returns it as a hexadecimal
    // string
    public static String sha256Hex(byte[] data) {
        try {
            // Get SHA-256 digest instance
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // Compute hash
            byte[] hash = digest.digest(data);

            // Convert byte array to hex string
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b)); // Append hex representation of each byte
            }
            return sb.toString(); // Return final hex string

        } catch (Exception e) {
            // Throw runtime exception if hashing fails (unlikely with standard algorithms)
            throw new RuntimeException("SHA256_ERROR");
        }
    }
}
