package exam.DemonTest;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {
		Log.i("JYJ test","AlarmReceiver Action == " + intent.getAction());
		ComponentName cn = new ComponentName(context.getPackageName(), DemonService.class.getName());
		//데몬중지(false == 기존에 해당 백그라운드 데몬이 없을경우임.)
		boolean stopsvc = context.stopService(new Intent().setComponent(cn));
		Log.i("JYJ test","Stop Action == " + stopsvc);
		//데몬시작
		ComponentName startsvc = context.startService(new Intent().setComponent(cn));
		if(startsvc != null){
			Log.i("JYJ test","Start Action == " + startsvc);
		}else{
			Log.i("JYJ test","Start Action == fail");
		}
		
	}

}
