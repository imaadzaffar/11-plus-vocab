package com.zafaris.learnvocab.data.database;

import android.provider.BaseColumns;

public final class WordBankContract {

    private WordBankContract() { }

    public static class WordBank implements BaseColumns {
        public static final String TABLE_NAME = "words";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_SET = "[set]";
        public static final String COLUMN_NUMBER = "number";
        public static final String COLUMN_WORD = "word";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_DEFINITION = "definition";
        public static final String COLUMN_EXAMPLE = "example";
        public static final String COLUMN_SYNONYMS = "synonyms";
        public static final String COLUMN_ANTONYMS = "antonyms";
    }
}