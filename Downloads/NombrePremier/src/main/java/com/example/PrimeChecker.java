package com.example;

public class PrimeChecker {
    /**
     * Checks if a number is prime.
     * @param n the number to check
     * @return true if prime, false otherwise
     */
    public boolean isPrime(int n) {
        int cpt = 0;
        for (int i = 1; i <= n; i++) {
            if (n % i == 0) {
                cpt++;
            }
        }
        return cpt == 2;
    }
}
