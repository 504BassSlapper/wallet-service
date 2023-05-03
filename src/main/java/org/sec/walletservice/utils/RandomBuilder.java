package org.sec.walletservice.utils;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Component
public class RandomBuilder {
    public static final int TEN = 10;

    public double buildRandomBalance(Random random) {
        int rangeMax = 7;

        double randomBalance = buildListOfTenPower(rangeMax).stream()
                .mapToDouble(tenPower -> tenPower * random.nextInt(10)).sum();
        return randomBalance;
    }

    public double randomTenPower(int range) {
        return Math.pow(TEN, range);
    }

    public List<Double> buildListOfTenPower(int rangeMax) {
        List<Double> listeOfRandomTenPower = new ArrayList<>();
        IntStream.range(-2, rangeMax).forEach(range -> {
            listeOfRandomTenPower.add(randomTenPower(range));

        });
        return listeOfRandomTenPower;
    }

}
