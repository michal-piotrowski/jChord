package com.piotrowski;

import com.piotrowski.userInputLogic.Commands;
import com.piotrowski.userInputLogic.CommandsImpl;

/**
 * Created by Michał Piotrowski on 2018-08-22.
 * //0. read all existing chords in folder
 * //1. collect input parameters - if none, show menu and available chords (pictures)
 * //1.a take fs action if input params specified
 * //1.b notify of result
 * //2. show menu
 * //  - exit
 * //  - add new chord
 * //  - change existing chord
 * //collect input to shut down
 */
public class JChord {
    public static void main(String[] args) {

        Commands commands = new CommandsImpl();
        Commands.STATE result = commands.handle(args);
        if (args.length == 0)
            while (result != Commands.STATE.EXIT) {
                commands.printMenu();
                result = commands.handle(commands.gatherUserInput());
            }

    }
}
