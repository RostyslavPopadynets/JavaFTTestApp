package com.vakoms.utils;

import com.vakoms.objects.Lang;

import java.util.Locale;

public class LocaleManager {

    public static final Locale RU_LOCALE = new Locale("ru");
    public static final Locale EN_LOCALE = new Locale("en");
    public static final Locale UA_LOCALE = new Locale("ua");

    private static Lang currentLang = new Lang(2,"ua","Ukrainian",UA_LOCALE);

    public static Lang getCurrentLang() {
        return currentLang;
    }

    public static void setCurrentLang(Lang currentLang) {
        LocaleManager.currentLang = currentLang;
    }

}