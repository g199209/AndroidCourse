package cn.zju.id21532035;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClient.*;
import com.marakana.android.yamba.clientlib.YambaClientException;

import java.util.List;

public class UpdateService extends Service {
    private static final String TAG = "UpdaterService";
    static long DELAY = 60000; // ms
    public static boolean runFlag = false;
    private Updater myUpdater;
    private String username, password;

    public UpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        DELAY = Long.parseLong(prefs.getString("interval", "60")) * 1000;
        username = prefs.getString("username", "username");
        password = prefs.getString("password", "password");

        prefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(UpdateService.this);
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                if (s.equals("username"))
                    username = prefs.getString("username", "username");
                if (s.equals("password"))
                    password = prefs.getString("password", "password");

                if (s.equals("interval"))
                    DELAY = Long.parseLong(prefs.getString("interval", "60")) * 1000;
            }
        });

        this.myUpdater = new Updater();
        Log.d(TAG, "OnCreated");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!runFlag) {
            this.runFlag = true;
            this.myUpdater.start();
        }
        Log.d(TAG, "OnStarted");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        this.runFlag = false;
        this.myUpdater.interrupt();
        this.myUpdater = null;

        Log.d(TAG, "OnDestoryed");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    private class Updater extends Thread {
        public Updater() {
            super("UpdaterService Thread");
        }

        @Override
        public void run() {
            DBHelper myDBHelper = new DBHelper(UpdateService.this);

            while(runFlag) {
                Log.d(TAG, "Running background thread" + DELAY / 1000);
                SQLiteDatabase db = myDBHelper.getWritableDatabase();

                try {
                    YambaClient cloud = new YambaClient(username, password);
                    List<Status> timeline = cloud.getTimeline(20);
                    ContentValues values = new ContentValues();
                    Log.i(TAG, "获取记录数： " + timeline.size());

                    int count = 0;
                    long rowID = 0;
                    for (Status status : timeline) {
                        //Log.d(TAG, String.format("%s: %s",
                        //        status.getUser(),	status.getMessage()));
                        String usr = status.getUser();
                        String msg = status.getMessage();
                        if (msg != null && msg.startsWith("<")) {
                            String[] msgA = msg.split("[<>]", 3);
                            if (msgA.length == 3) {
                                usr = msgA[1];
                                msg = msgA[2];
                            }
                        }
                        values.clear();
                        values.put(StatusConstract.Column.ID, status.getCreatedAt().getTime());
                        values.put(StatusConstract.Column.USER, usr);
                        values.put(StatusConstract.Column.MESSAGE, msg);
                        values.put(StatusConstract.Column.CREATED_AT, status.getCreatedAt().getTime());

                        rowID = db.insertWithOnConflict(StatusConstract.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                        if (rowID != -1) {
                            count ++;
                            Log.i(TAG, String.format("(%s)  %s",usr, msg));
                        }

                    }

                }
                catch (YambaClientException e) {
                    e.printStackTrace();
                    runFlag = false;
                }
                finally {
                    db.close();
                }

                try {
                    Thread.sleep(DELAY);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                    runFlag = false;
                }
            }
        }
    }

}
