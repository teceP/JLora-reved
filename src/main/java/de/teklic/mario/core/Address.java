package de.teklic.mario.core;
/*
 *
 * @author Mario Teklic
 */

/**
 * Address-Singleton.
 * Represents the nodes address.
 * Must begin with a zero "0" and must only consists out of numbers.
 * Example: "0137"
 */
public class Address {

    /**
     * Singleton Instance
     */
    private static Address address;

    /**
     * The nodes address
     */
    private String addr;

    private Address(){}

    /**
     * @return the instance of the address-object.
     */
    public static Address getInstance(){
        if(address == null){
            address = new Address();
        }
        return address;
    }

    /**
     * @return the address.
     */
    public String getAddr(){
        return addr;
    }

    /**
     * Sets the address
     * @param addr Address as String, must match following:
     *             1. consist out of numbers
     *             2. begins with a zero
     */
    public void setAddr(String addr){
        this.addr = addr;
    }
}
