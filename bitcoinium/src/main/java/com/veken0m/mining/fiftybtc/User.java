
package com.veken0m.mining.fiftybtc;

import com.fasterxml.jackson.annotation.JsonProperty;


public class User {
    private final int active_workers;
    private final float confirmed_rewards;
    private final float hash_rate;
    private final float payouts;

    public User(@JsonProperty("active_workers")
                int active_workers,
                @JsonProperty("confirmed_rewards")
                float confirmed_rewards,
                @JsonProperty("hash_rate")
                float hash_rate,
                @JsonProperty("payouts")
                float payouts) {
        this.active_workers = active_workers;
        this.confirmed_rewards = confirmed_rewards;
        this.hash_rate = hash_rate;
        this.payouts = payouts;
    }

    public int getActive_workers() {
        return this.active_workers;
    }

    public float getConfirmed_rewards() {
        return this.confirmed_rewards;
    }

    public float getHash_rate() {
        return this.hash_rate;
    }

    public float getPayouts() {
        return this.payouts;
    }
}
