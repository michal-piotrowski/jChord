package com.piotrowski.userInputLogic;

/**
 * Created by Michał Piotrowski on 2018-08-22.
 */
public class OperationNotFoundException extends Exception {
    private String[] operationWithArgs;

    public OperationNotFoundException(String[] operationWithArgs) {
        this.operationWithArgs = operationWithArgs;
    }

    @Override
    public void printStackTrace() {
        System.out.print("Operation: ");
        for (String tok: operationWithArgs) {
            System.out.print(tok + " ");
        }
        System.out.println("has not been found");
        String fName = operationWithArgs[0];
        if (Commands.functions.containsKey(fName)) {
            System.out.println("candidates: ");
            for (String[] argList: Commands.functions.get(fName)){
                System.out.print(fName + " ");
                for(String arg: argList)
                    System.out.print(arg + " ");
                System.out.println();
            }
            System.out.println();
        }
    }
}
