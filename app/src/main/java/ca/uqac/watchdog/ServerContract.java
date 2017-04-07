package ca.uqac.watchdog;

import android.provider.BaseColumns;

/**
 * Created by Sam on 2017-04-07.
 * Server "Contract", as suggested here:
 * https://developer.android.com/training/basics/data-storage/databases.html
 */

public final class ServerContract {
    // Prevent class froim being instanciated
    private ServerContract() {}

    /* Inner class that defines the table contents */
    public static class ServerEntry implements BaseColumns {
        public static final String TABLE_NAME = "servers";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_URL = "url";
    }
}
