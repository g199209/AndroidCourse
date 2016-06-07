package cn.zju.id21532035;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Gmf on 6/7.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = DBHelper.class.getSimpleName();

    public DBHelper(Context context) {
        super(context, StatusConstract.DB_NAME, null, StatusConstract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("drop table if exists " + StatusConstract.TABLE);
        String sql = String.format("create table %s (%s int primary key, %s text, %s text, %s int)",
                StatusConstract.TABLE,
                StatusConstract.Column.ID,
                StatusConstract.Column.USER,
                StatusConstract.Column.MESSAGE,
                StatusConstract.Column.CREATED_AT);

        Log.d(TAG, "Create DB: " + sql);
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + StatusConstract.TABLE);
        onCreate(sqLiteDatabase);
    }
}
