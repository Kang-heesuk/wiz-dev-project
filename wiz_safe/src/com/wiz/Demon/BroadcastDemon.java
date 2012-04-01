
package com.wiz.Demon;

import com.wiz.util.WizSafeUtil;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;


//�ش� Ŭ������ �ý��� ����۽ÿ� ����� ������ �� ���� �ϴ� Ŭ�����̴�.
public class BroadcastDemon extends BroadcastReceiver {
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
			if(WizSafeUtil.isAuthOkUser(context)){
				//������ �Ϸ�� �ܸ��̹Ƿ� SERVICE �� �����Ѵ�. == ���� ������ ������ ���� �� ����
				//��׶��忡 DemonService��� Sevice�� �����ϴ��� ������.
				ComponentName cn = new ComponentName(context.getPackageName(), WizSafeService.class.getName());
				//���� ����(������� ���Ͽ� ���ִµ��� ����, ������ ������쵵 �������� ������)
				boolean stopsvc = context.stopService(new Intent().setComponent(cn));
				//���� ����(������ ���� ��Ų ������ ���۽�Ŵ)
				ComponentName startsvc = context.startService(new Intent().setComponent(cn));
			}
		}
	}
}
