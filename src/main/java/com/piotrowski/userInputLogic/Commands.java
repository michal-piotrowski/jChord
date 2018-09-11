package com.piotrowski.userInputLogic;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Michał Piotrowski on 2018-08-22.
 */
public interface Commands {
    void printMenu();



    Multimap<String, String[]> functions = HashMultimap.create();

    String[] gatherUserInput();

    enum STATE {
        EXIT, CONTINUE, 
    }
    
    STATE handle(String[] input);
    
    STATE takeAction(String[] commandWithArgs);
}
