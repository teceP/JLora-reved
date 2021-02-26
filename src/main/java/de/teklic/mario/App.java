package de.teklic.mario;

import de.teklic.mario.core.JLora;

public class App {
    public static void main(String[] args){
        if(args.length != 1){
            System.out.println("Arguments example: '0140'.");
            System.exit(0);
        }

        JLora jLora = new JLora();
        jLora.start(args[0]);
    }
}
