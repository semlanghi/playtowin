package com.example.application.polyflow.datatypes.nexmark;

import com.example.application.polyflow.datatypes.Tuple;
import jakarta.persistence.Entity;

@Entity
public class InputAuction extends Tuple {

    private long timestamp;
    private long auctionId;
    private String itemName;
    private String description;
    private long initialBid;
    private long reserve;
    private long dateTime;
    private long expires;
    private long seller;
    private long category;
    private String extra;
    private String recordId;
    private String operatorId;
    private String intervalId;
    private String cursor;
    private String attributeForComputation;


    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public long getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(long auctionId) {
        this.auctionId = auctionId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getInitialBid() {
        return initialBid;
    }

    public void setInitialBid(long initialBid) {
        this.initialBid = initialBid;
    }

    public long getReserve() {
        return reserve;
    }

    public void setReserve(long reserve) {
        this.reserve = reserve;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public long getExpires() {
        return expires;
    }

    public void setExpires(long expires) {
        this.expires = expires;
    }

    public long getSeller() {
        return seller;
    }

    public void setSeller(long seller) {
        this.seller = seller;
    }

    public long getCategory() {
        return category;
    }

    public void setCategory(long category) {
        this.category = category;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
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



}
