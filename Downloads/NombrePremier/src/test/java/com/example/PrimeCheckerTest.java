package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PrimeCheckerTest {
    private final PrimeChecker checker = new PrimeChecker();

    @Test
    public void testPrimes() {
        assertTrue(checker.isPrime(2));
        assertTrue(checker.isPrime(3));
        assertTrue(checker.isPrime(5));
        assertTrue(checker.isPrime(7));
        assertTrue(checker.isPrime(11));
        assertTrue(checker.isPrime(13));
        assertTrue(checker.isPrime(17));
        assertTrue(checker.isPrime(19));
        assertTrue(checker.isPrime(23));
    }

    @Test
    public void testNonPrimes() {
        assertFalse(checker.isPrime(-1));
        assertFalse(checker.isPrime(0));
        assertFalse(checker.isPrime(1));
        assertFalse(checker.isPrime(4));
        assertFalse(checker.isPrime(6));
        assertFalse(checker.isPrime(8));
        assertFalse(checker.isPrime(9));
        assertFalse(checker.isPrime(10));
        assertFalse(checker.isPrime(15));
        assertFalse(checker.isPrime(21));
        assertFalse(checker.isPrime(25));
    }
}
