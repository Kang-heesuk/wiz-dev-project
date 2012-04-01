
package com.wiz.Demon;

import com.wiz.util.WizSafeUtil;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;


//해당 클래스는 시스템 재시작시에 사라진 데몬을 재 실행 하는 클래스이다.
public class BroadcastDemon extends BroadcastReceiver {
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
			if(WizSafeUtil.isAuthOkUser(context)){
				//인증이 완료된 단말이므로 SERVICE 를 실행한다. == 서비스 실행은 무조건 종료 후 실행
				//백그라운드에 DemonService라는 Sevice가 존재하는지 가져옴.
				ComponentName cn = new ComponentName(context.getPackageName(), WizSafeService.class.getName());
				//서비스 중지(재시작을 위하여 떠있는데몬 삭제, 데몬이 없을경우도 문제없이 삭제됨)
				boolean stopsvc = context.stopService(new Intent().setComponent(cn));
				//서비스 시작(위에서 중지 시킨 데몬을 시작시킴)
				ComponentName startsvc = context.startService(new Intent().setComponent(cn));
			}
		}
	}
}
