package com.danielmeinicke.lson;

public interface JsonNumber extends JsonPrimitive, Comparable<Number> {

    int getAsInteger();
    byte getAsByte();
    float getAsFloat();
    double getAsDouble();
    short getAsShort();
    long getAsLong();

}
