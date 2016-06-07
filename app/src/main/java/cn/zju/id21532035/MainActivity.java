package cn.zju.id21532035;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.marakana.android.yamba.clientlib.SubmitProgram;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity {
    CoordinatorLayout SnackbarContainer;
    Button btn1, btn2, btn3;
    TextView tv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SnackbarContainer = (CoordinatorLayout)findViewById(R.id.SnackbarContainer);

        tv1 = (TextView)findViewById(R.id.textView3);

        ContentResolver provider = getContentResolver();
        Cursor cursor = provider.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{"duration", "title", "artist"}, null, null, null);
        String s = "";
        while(cursor.moveToNext()) {
            s += String.format("作者： %s\n曲名：%s\n时间：%s (ms)\n\n",
                    cursor.getString(2),
                    cursor.getString(1),
                    cursor.getString(0));
        }
        tv1.setText(s);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
                Snackbar.make(SnackbarContainer, "Start Service!", Snackbar.LENGTH_LONG).show();
                startService(new Intent(this, UpdateService.class));
                return true;

            case R.id.action_stop_service:
                Snackbar.make(SnackbarContainer, "Stop Service!", Snackbar.LENGTH_LONG).show();
                stopService(new Intent(this, UpdateService.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fileTestWrite(String dir){
        String fn = dir + "/hello.txt";
        tv1.setText(fn);
        try {
            PrintWriter o = new PrintWriter(new BufferedWriter(new FileWriter(fn)));
            o.println("Hello!");
            o.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

}
