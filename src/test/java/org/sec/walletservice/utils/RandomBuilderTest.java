package org.sec.walletservice.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class RandomBuilderTest {

    @Test
    void should_build_a_random_positiv_double() {
        RandomBuilder randomBuilder = new RandomBuilder();
        double randomBalance = randomBuilder.buildRandomBalance(new Random());
        System.out.println(randomBalance);
        Assertions.assertTrue(randomBalance > 0);
    }
}
