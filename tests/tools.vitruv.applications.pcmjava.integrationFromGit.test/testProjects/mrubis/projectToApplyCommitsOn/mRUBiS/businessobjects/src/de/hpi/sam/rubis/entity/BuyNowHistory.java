package de.hpi.sam.rubis.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BuyNowHistory {

    public static final BuyNowHistory EMPTY = new BuyNowHistory(new ArrayList<BuyNow>(0));

    private List<BuyNow> buyNows;

    public BuyNowHistory(List<BuyNow> buyNows) {
        this.buyNows = buyNows;
    }

    public List<BuyNow> getBuyNows() {
        return Collections.unmodifiableList(buyNows);
    }

}
