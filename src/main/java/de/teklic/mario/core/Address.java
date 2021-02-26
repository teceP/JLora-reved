package de.teklic.mario.core;
/*
 *
 * @author Mario Teklic
 */

public class Address {

    private static Address address;
    private String addr;

    private Address(){}

    public static Address getInstance(){
        if(address == null){
            address = new Address();
        }
        return address;
    }

    public String getAddr(){
        return addr;
    }

    public void setAddr(String addr){
        this.addr = addr;
    }
}
