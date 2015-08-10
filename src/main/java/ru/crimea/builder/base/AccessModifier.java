package ru.crimea.builder.base;

/**
 * Created by vitaliy.orlov@gmail.com on 21.07.2015.
 */
public enum AccessModifier {
    PRIVATE("private"), PUBLIC("public"), PROTECTED("protected"), PUBLIC_STATIC("public static");

    private String printValue;
    private AccessModifier(String printValue){
        this.printValue = printValue;
    }

    public String getPrintValue(){
        return printValue;
    }

}
