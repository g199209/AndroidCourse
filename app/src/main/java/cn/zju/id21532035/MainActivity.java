package cn.zju.id21532035;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.marakana.android.yamba.clientlib.SubmitProgram;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity {
    CoordinatorLayout SnackbarContainer;
    TextView tvPkgName;
    SQLiteDatabase db;
    Cursor cursor;
    DBHelper dbhlp;
    SimpleCursorAdapter adapter;
    ListView listStatus;
    boolean serviceRunning = false;

    private static final String[] FROM = {StatusConstract.Column.USER,
        StatusConstract.Column.MESSAGE, StatusConstract.Column.CREATED_AT};

    private static final int[] TO = {R.id.textUser, R.id.textMsg, R.id.textTime};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SnackbarContainer = (CoordinatorLayout)findViewById(R.id.SnackbarContainer);
        listStatus = (ListView)findViewById(R.id.listStatus);
        tvPkgName = (TextView)findViewById(R.id.textView);

        tvPkgName.setText(this.getString(R.string.PkgName) + this.getPackageName());

        dbhlp = new DBHelper(this);
        db = dbhlp.getReadableDatabase();
        cursor = db.query(StatusConstract.TABLE, null, null, null, null, null,
                StatusConstract.DEFAULT_SORT);
        startManagingCursor(cursor);

        adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, FROM, TO);
        adapter.setViewBinder(new TimeLineViewBinder());
        listStatus.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, UpdateService.class));
        db.close();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if(menu == null)
            return true;

        MenuItem toggleItem = menu.findItem(R.id.action_start_service);
        if (serviceRunning) {
            toggleItem.setTitle(R.string.StopService);
            toggleItem.setIcon(android.R.drawable.ic_media_pause);
        }
        else {
            toggleItem.setTitle(R.string.StartService);
            toggleItem.setIcon(android.R.drawable.ic_media_play);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_submit:
                new SubmitProgram().doSubmit(this);
                Snackbar.make(SnackbarContainer, "Submit Program!", Snackbar.LENGTH_LONG).show();
                return true;

            case R.id.action_publish:
                startActivity(new Intent("cn.zju.id21532035.StatusActivity"));
                return true;

            case R.id.action_setting:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            case R.id.action_start_service:
                if (serviceRunning) {
                    stopService(new Intent(this, UpdateService.class));
                    Snackbar.make(SnackbarContainer, "Stop Service!", Snackbar.LENGTH_LONG).show();
                    serviceRunning = false;
                }
                else {
                    startService(new Intent(this, UpdateService.class));
                    Snackbar.make(SnackbarContainer, "Start Service!", Snackbar.LENGTH_LONG).show();
                    serviceRunning = true;
                }
                return true;


            case R.id.action_cleardb:
                SQLiteDatabase dbw= dbhlp.getWritableDatabase();
                dbw.delete(StatusConstract.TABLE, null, null);
                cursor.requery();
                adapter.notifyDataSetChanged();
                Snackbar.make(SnackbarContainer, "All data is cleared", Snackbar.LENGTH_LONG).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class TimeLineViewBinder implements SimpleCursorAdapter.ViewBinder {
        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            if (view.getId() != R.id.textTime)
                return false;

            long timestamp = cursor.getLong(columnIndex);

            CharSequence relativeTime =
                    DateUtils.getRelativeTimeSpanString(timestamp);
            ((TextView)view).setText(relativeTime);
            return true;
        }
    }

//    private void fileTestWrite(String dir){
//        String fn = dir + "/hello.txt";
//        tv1.setText(fn);
//        try {
//            PrintWriter o = new PrintWriter(new BufferedWriter(new FileWriter(fn)));
//            o.println("Hello!");
//            o.close();
//        }
//        catch(Exception e){
//            e.printStackTrace();
//        }
//    }

}
