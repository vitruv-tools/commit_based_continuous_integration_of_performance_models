package de.hpi.sam.rubis.bidandbuy.impl;

import java.util.Random;

import javax.ejb.Stateless;

import de.hpi.sam.rubis.DummyService;

@Stateless(name = DummyService.NAME)
public class DummyServiceBean implements DummyService {

    private Random random = new Random();

    public CalculationResult calculate(int bytesToCalculate, byte[] input, boolean returnResults) {
        long start = System.nanoTime();

        // actual processing
        byte[] payload = new byte[bytesToCalculate];
        random.nextBytes(payload);

        long end = System.nanoTime();

        CalculationResult result;
        if (returnResults) {
            result = new CalculationResult(payload, end - start);
        } else {
            byte[] emptyPayload = new byte[0];
            result = new CalculationResult(emptyPayload, end - start);
        }

        return result;
    }

}
