
package com.veken0m.mining.bitminter;

import com.fasterxml.jackson.annotation.JsonProperty;


public class Balances {
    private final float BTC;
    private final float NMC;

    public Balances(@JsonProperty("BTC")
                    float BTC,
                    @JsonProperty("NMC")
                    float NMC) {

        this.BTC = BTC;
        this.NMC = NMC;
    }

    public float getBTC() {
        return BTC;
    }

    public float getNMC() {
        return NMC;
    }
}
