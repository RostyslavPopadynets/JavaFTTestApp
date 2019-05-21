package com.vakoms.objects;

import java.util.Locale;

public class Lang {

    private String code;
    private String name;
    private Locale locale;
    private int index;


    public Lang(int index, String code, String name, Locale locale) {
        this.code = code;
        this.name = name;
        this.locale = locale;
        this.index = index;
    }

    public Locale getLocale() {
        return locale;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return name;
    }

}