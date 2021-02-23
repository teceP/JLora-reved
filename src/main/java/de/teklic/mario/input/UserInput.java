package de.teklic.mario.input;

import de.teklic.mario.core.JLora;

import java.io.InputStream;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

public class UserInput extends Observable implements Runnable {

    private static UserInput userInput;
    private static Scanner scanner;

    private UserInput(){
        this.scanner = new Scanner(System.in);
    }

    public static UserInput getInstance(){
        if(userInput == null || scanner == null){
            JLora.logger.info("UserInput or Scanner object was null. Create new instance.");
            userInput = new UserInput();
        }
        return userInput;
    }

    @Override
    public void run() {

    }
}
