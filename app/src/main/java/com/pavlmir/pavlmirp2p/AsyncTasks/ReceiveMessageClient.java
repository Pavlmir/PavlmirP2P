package com.pavlmir.pavlmirp2p.AsyncTasks;

import android.app.ActivityManager;
import android.content.Context;
import com.pavlmir.pavlmirp2p.ChatActivity;
import com.pavlmir.pavlmirp2p.Entities.Message;
import com.pavlmir.pavlmirp2p.MainActivity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ReceiveMessageClient extends AbstractReceiver {
	private static final int SERVER_PORT = 4446;
	private Context mContext;
	private ServerSocket socket;

	public ReceiveMessageClient(Context context){
		mContext = context;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		try {
			socket = new ServerSocket(SERVER_PORT);
			while(true){
				Socket destinationSocket = socket.accept();
				
				InputStream inputStream = destinationSocket.getInputStream();
				BufferedInputStream buffer = new BufferedInputStream(inputStream);
				ObjectInputStream objectIS = new ObjectInputStream(buffer);
				Message message = (Message) objectIS.readObject();
				
				destinationSocket.close();
				publishProgress(message);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
        
		return null;
	}

	@Override
	protected void onCancelled() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.onCancelled();
	}

	@Override
	protected void onProgressUpdate(Message... values) {
		super.onProgressUpdate(values);
		playNotification(mContext, values[0]);		
		
		//If the message contains a video or an audio, we saved this file to the external storage
		int type = values[0].getmType();

		if(isActivityRunning(MainActivity.class)){
			ChatActivity.refreshList(values[0], false);
		}			
	}
	
	@SuppressWarnings("rawtypes")
	public Boolean isActivityRunning(Class activityClass) {
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (activityClass.getCanonicalName().equalsIgnoreCase(task.baseActivity.getClassName()))
                return true;
        }

        return false;
	}
}
