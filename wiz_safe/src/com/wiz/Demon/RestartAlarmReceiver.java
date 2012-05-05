package com.wiz.Demon;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.wiz.util.WizSafeUtil;

public class RestartAlarmReceiver extends BroadcastReceiver {
	
	public void onReceive(Context context, Intent intent) {
		if(WizSafeUtil.isAuthOkUser(context)){
			//��׶��忡 DemonService��� Sevice�� �����ϴ��� ������.
			ComponentName cn = new ComponentName(context.getPackageName(), WizSafeService.class.getName());
			//���� ����(������� ���Ͽ� ���ִµ��� ����, ������ ������쵵 �������� ������)
			boolean stopsvc = context.stopService(new Intent().setComponent(cn));
			//���� ����(������ ���� ��Ų ������ ���۽�Ŵ)
			ComponentName startsvc = context.startService(new Intent().setComponent(cn));
		}
	}
}
