package de.hpi.sam.rubis.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BidHistory {

    public static final BidHistory EMPTY = new BidHistory(new ArrayList<Bid>(0));

    private List<Bid> bids;

    public BidHistory(List<Bid> bids) {
        this.bids = bids;
    }

    public List<Bid> getBids() {
        return Collections.unmodifiableList(bids);
    }

}
