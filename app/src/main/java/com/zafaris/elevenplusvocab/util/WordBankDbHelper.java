package com.zafaris.elevenplusvocab.util;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class WordBankDbHelper extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "WordBank.db";
    private static final int DATABASE_VERSION = 1;

    public WordBankDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}
