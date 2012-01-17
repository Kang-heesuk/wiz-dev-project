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
		//��������(false == ������ �ش� ��׶��� ������ ���������.)
		boolean stopsvc = context.stopService(new Intent().setComponent(cn));
		Log.i("JYJ test","Stop Action == " + stopsvc);
		//�������
		ComponentName startsvc = context.startService(new Intent().setComponent(cn));
		if(startsvc != null){
			Log.i("JYJ test","Start Action == " + startsvc);
		}else{
			Log.i("JYJ test","Start Action == fail");
		}
		
	}

}
