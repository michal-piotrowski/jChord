package com.piotrowski.userInputLogic;

import com.piotrowski.graphics.GraphicsScheisse;
import javafx.util.Pair;
import jdk.internal.util.xml.impl.Input;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;

/**
 * Created by Michał Piotrowski on 2018-08-22.
 */

/**
 * TODO: LADOWAC DOSTEPNE OPERACJE Z PLIKU, ZEBY PRZY ICH DRUKOWANIU W help() BYL DOSTEPNY DESCRIPTION OPERACJI
 */
public class CommandsImpl implements Commands {

    public enum argCorrectness {
        chordName("[abcdefgABCDEFG][a-zA-Z0-9#/]*"),
        tab("(([xX0-9]|[0-9][0-9])\\.){5}([xX0-9]){1,2}");

        private String regexPattern;

        argCorrectness(String pattern) {
            regexPattern = pattern;
        }

        public void isCorrect(String input) throws InputMismatchException {
            if (this.equals(chordName) && input.matches("[hH].*")) {
                System.out.println();
                throw new InputMismatchException("We use B instead of H and Bb instead of B");
            }
            if (!input.matches(regexPattern))
                throw new InputMismatchException(this.name() + " should match pattern: " + regexPattern);
        }
    }

    //TODO before listing a method, check if it's implemented. If not - print "not implemented yet" in console when listing
    public void printMenu() {
        System.out.println("Perform an operation: ");
        for (String fName : functions.keySet()) {
            if (fName.matches("^-"))
                continue;
            for (String[] entry : functions.get(fName)) {
                if (Arrays.stream(this.getClass().getMethods()).anyMatch(implementedMethod ->
                        implementedMethod.getName().equals(fName) &&
                                implementedMethod.getParameterCount() == entry.length
                ))
                    System.out.println("\t" + fName + Arrays.toString(entry));
                else
                    System.out.println("\t" + fName + Arrays.toString(entry) + " *** NOT IMPLEMENTED YET");
            }
        }
    }

    /**
     * Load already created chords
     */
    public CommandsImpl() {
        functions.put("addNew", new String[]{"chordName", "tab"});
        functions.put("-n", new String[]{"chordName", "tab"});
        functions.put("remove", new String[]{"chordName"});
        functions.put("-r", new String[]{"chordName"});
        functions.put("help", new String[]{});
        functions.put("-h", new String[]{});
        functions.put("exit", new String[]{});
        functions.put("addNew", new String[]{});
    }

    /**
     * Used when user runs the program without cmd line parameters
     *
     * @return
     */
    public String[] gatherUserInput() {
        String[] userInput = null;
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        try {
            userInput = br.readLine().split(" ");
            System.out.print("\033[H\033[2J");
            System.out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userInput;
    }

    public STATE handle(String[] userCommandWithArgs) {
        if (userCommandWithArgs.length > 0) {
            try {
                verifyCorrectness(userCommandWithArgs);
                return takeAction(userCommandWithArgs);
            } catch (OperationNotFoundException | IllegalArgumentException e) {
                e.printStackTrace();
                return STATE.CONTINUE;
            } catch (InputMismatchException e) {
                return STATE.CONTINUE;
            }
        }
        return STATE.CONTINUE;
    }

    @Override
    public STATE takeAction(String[] commandWithArgs) {
        String functionName = commandWithArgs[0];
        if (functionName.matches("^-")) {
            functionName = getFunctionNameForOption(functionName);
        }
        try {
            return callAppropriateMethod(functionName, commandWithArgs);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getFunctionNameForOption(String shortName) {
        switch (shortName) {
            case "-n":
                return "addNew";
            case "-r":
                return "remove";
            case "-h":
                return "help";
        }
        return null;
    }

    private STATE callAppropriateMethod(String functionName, String[] commandWithArgs) throws IOException {
        switch (functionName) {
            case "addNew": {
                if (commandWithArgs.length > 1)
                    return addNew(commandWithArgs[1], commandWithArgs[2]);
                else
                    return addNew();
            }
//            case "remove":
//                return remove(commandWithArgs[1]);
            case "help":
                return help();
            case "exit":
                return STATE.EXIT;
        }
        return STATE.CONTINUE;
    }

    public STATE help() {
        System.out.println(functions.toString());
        return STATE.CONTINUE;
    }

    /**
     * case lines corresponds to functions added to map in CommandsImpl constructor
     *
     * @param commandWithArgs contains functionName at 0 position and its arguments at remaining indexes
     * @throws OperationNotFoundException thrown when user didn't enter existing operation or invalid arguments
     */
    private void verifyCorrectness(String[] commandWithArgs) throws OperationNotFoundException, InputMismatchException {
        String functionName = commandWithArgs[0];
        try {
            if (functions.containsKey(functionName)) {
                switch (functionName) {
                    case "addNew": {
                        if (commandWithArgs.length == 1)
                            break;
                        if (commandWithArgs.length != 3)
                            throw new OperationNotFoundException(commandWithArgs);
                        argCorrectness.chordName.isCorrect(commandWithArgs[1]);
                        argCorrectness.tab.isCorrect(commandWithArgs[2]);
                        break;
                    }
                    case "remove": {
                        if (commandWithArgs.length != 2)
                            throw new OperationNotFoundException(commandWithArgs);
                        argCorrectness.chordName.isCorrect(commandWithArgs[1]);
                    }

                }
            } else {
                throw new OperationNotFoundException(commandWithArgs);
            }
        } catch (InputMismatchException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    public STATE addNew(String chordName, String tab) throws IOException {
        String[] frets = tab.split("\\.");
        List<Pair<String, String>> stringFretPairs = new ArrayList<>();
        stringFretPairs.add(new Pair<>("E", frets[0]));
        stringFretPairs.add(new Pair<>("A", frets[1]));
        stringFretPairs.add(new Pair<>("D", frets[2]));
        stringFretPairs.add(new Pair<>("G", frets[3]));
        stringFretPairs.add(new Pair<>("B", frets[4]));
        stringFretPairs.add(new Pair<>("E", frets[5]));
        new GraphicsScheisse(chordName, stringFretPairs);
        System.out.println("Chord successfully created");
        return STATE.CONTINUE;
    }

    public STATE addNew() {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        try {
            System.out.println("Name your chord: ");
            String chordName = br.readLine();
            if (chordName.matches(".*[!~$%^& *].*")) {
                System.out.println("What the fuck?");
                return STATE.CONTINUE;
            }
            System.out.println("Write it's tab from thickest string (e.g. Em is 0.2.2.0.0.0)");
            String chordTab = br.readLine();
            try {
                verifyCorrectness(new String[]{"addNew", chordName, chordTab});
            } catch (OperationNotFoundException e) {
                e.printStackTrace();
                return STATE.CONTINUE;
            }
            return addNew(chordName, chordTab);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

//    public STATE remove(String chordName) {
//        System.out.println("removing " + chordName + " chord");
//        return STATE.CONTINUE;
//    }

    protected STATE exit() {
        return STATE.EXIT;
    }

}
