package com.example.application.polyflow.datatypes.nexmark;

import com.example.application.polyflow.datatypes.Tuple;
import jakarta.persistence.Entity;

@Entity
public class InputBid extends Tuple {

    private long auction;
    private long bidder;
    private long price;
    private String channel;
    private long timestamp;
    private String recordId;
    private String operatorId;
    private String intervalId;
    private String cursor;
    private String attributeForComputation;

    public InputBid copy(){
        InputBid copy = new InputBid();
        copy.setTimestamp(timestamp);
        copy.setRecordId(recordId);
        copy.setChannel(channel);
        copy.setPrice(price);
        copy.setBidder(bidder);
        copy.setAuction(auction);
        copy.setOperatorId(this.operatorId);
        copy.setIntervalId(this.intervalId);
        copy.setCursor(cursor);
        copy.setAttributeForComputation(attributeForComputation);
        return copy;
    }

    public long getAuction() {
        return auction;
    }

    public void setAuction(long auction) {
        this.auction = auction;
    }

    public long getBidder() {
        return bidder;
    }

    public void setBidder(long bidder) {
        this.bidder = bidder;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }


    @Override
    public String getRecordId() {
        return recordId;
    }

    @Override
    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    @Override
    public String getOperatorId() {
        return operatorId;
    }

    @Override
    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    @Override
    public String getIntervalId() {
        return intervalId;
    }

    @Override
    public void setIntervalId(String intervalId) {
        this.intervalId = intervalId;
    }

    @Override
    public String getCursor() {
        return cursor;
    }

    @Override
    public void setCursor(String cursor) {
        this.cursor = cursor;
    }


    @Override
    public void setAttributeForComputation(String attributeForComputation) {
        this.attributeForComputation = attributeForComputation;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
