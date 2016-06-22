package cn.zju.id21532035;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Gmf on 6/22.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, UpdateService.class));
        Log.d("BootReceiver", "onReceived");
    }
}
