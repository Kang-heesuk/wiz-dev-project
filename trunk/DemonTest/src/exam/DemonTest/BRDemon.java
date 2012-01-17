package exam.DemonTest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BRDemon extends BroadcastReceiver {
	
	public void onReceive(Context context, Intent intent) {
		
		Log.i("JYJ test","Action == " + intent.getAction());
		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
			Log.i("JYJ test","Boot Completed");
		}else if(intent.getAction().equals(Intent.ACTION_TIME_TICK)){
			Log.i("JYJ test","Time Tick");
		}
	}

}
