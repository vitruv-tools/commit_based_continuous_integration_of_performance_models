package de.hpi.sam.rubis;

import java.io.Serializable;

import javax.ejb.Remote;

@Remote
public interface DummyService {

    public static String NAME = "DummyService";

    public CalculationResult calculate(int bytesToCalculate, byte[] input, boolean returnResults);

    public static class CalculationResult implements Serializable {
        
        private static final long serialVersionUID = 5865238362569720114L;

        private byte[] payload;

        private long processingTimeNano;

        public CalculationResult(byte[] payload, long processingTimeNano) {
            this.payload = payload;
            this.processingTimeNano = processingTimeNano;
        }

        public byte[] getPayload() {
            return payload;
        }

        public long getProcessingTimeNano() {
            return processingTimeNano;
        }

    }

}
