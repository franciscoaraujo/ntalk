package grupocriar.ntalk.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by francisco on 07/02/2018.
 */

public class NtalkBroadcastService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        intent = new Intent(context, NtalkService.class);
        context.startService(intent);

    }
}
