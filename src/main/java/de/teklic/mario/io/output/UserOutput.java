package de.teklic.mario.io.output;
/*
 *
 * @author Mario Teklic
 */

import java.io.PrintStream;

public class UserOutput {
    private static UserOutput userOutput;
    private PrintStream printStream;

    private UserOutput(){
        printStream = System.out;
    }

    public static UserOutput getInstance(){
        if(userOutput == null){
            userOutput = new UserOutput();
        }

        return userOutput;
    }

    public void setPrintStream(PrintStream printStream){
        this.printStream = printStream;
    }

    public void write(String message){
        printStream.println(message);
    }
}
