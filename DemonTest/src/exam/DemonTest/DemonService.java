package exam.DemonTest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class DemonService extends Service{

	boolean Quit;
	int counting = 0;
	
	public void onCreate(){ 
		super.onCreate();
	}
	
	public void onDestroy(){  
		super.onDestroy();
		Quit = true;
	}
	
	public int onStartCommand(Intent intent, int flags, int startId){
		super.onStartCommand(intent, flags, startId);
		Quit = false;
		DemonThread thread = new DemonThread(); 
		thread.start();
		return START_STICKY;
	}
	
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	class DemonThread extends Thread{ 
		public void run(){
			for(int i = 0 ; Quit == false ; i++){
				counting++;
				try{Thread.sleep(5000);}catch(Exception e){}
			}
		}
	}
	
}
