package com.example.application.polyflow.datatypes;

import com.example.application.data.AbstractEntity;
import jakarta.persistence.Entity;

import java.lang.reflect.Field;

@Entity
public class Tuple extends AbstractEntity {

    public Tuple copy(){return new Tuple();};
    public String getRecord_Id(){return "";};

    public void setRecord_Id(String record_Id){};
    public String getOperatorId(){return "";};
    public String getIntervalId(){return "";};
    public String getCursor(){return "";};
    public void setCursor(String cursor){};
    public void setOperatorId(String operatorId){};
    public void setIntervalId(String intervalId){};
    public long getTimestamp(){return 0;};
    public void setTimestamp(long dateOfBirth){};
    public void setAttributeForComputation(String attributeForComputation){};

    /*

    We use this method to dynamically get the Attribute that the user wants to compute over in one
    of the classes extending this one (e.g. InputLinearRoad, InputTaxi etc..)
    The attribute to compute over should be a double to simplify the logic here

     */
    public double getAttributeForComputation(String attributeForComputation) {
        try {
            Field field = this.getClass().getDeclaredField(attributeForComputation);
            field.setAccessible(true); //it's private
            return field.getDouble(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Invalid attribute name: " + attributeForComputation, e);
        }
    }}
