package cn.zju.id21532035;

import android.provider.BaseColumns;

/**
 * Created by Gmf on 6/7.
 */

public class StatusConstract {
    public static final String DB_NAME = "timeline.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE = "status";
    public static final String DEFAULT_SORT = Column.CREATED_AT + " DESC";

    public static final String NEW_STATUSES = "cn.zju.id56781234.action.NEW_STATUSES";

    public class Column {
        public static final String ID = BaseColumns._ID;
        public static final String USER = "user";
        public static final String MESSAGE = "message";
        public static final String CREATED_AT = "created_at";
    }

}
