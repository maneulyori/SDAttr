package com.maneulyori.sdattr;

import java.io.*;
import java.lang.*;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import android.util.Log;
import android.content.res.*;
import com.maneulyori.sdattr.utils.*;

public class SDAttrActivity extends Activity {
    /** Called when the activity is first created. */
	
	private Resources resources;
	private InputStream fatattrstream;
	private Boolean firstrun = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		resources = getResources();
		
		try{
			InputStream fileTest = new FileInputStream("/data/data/com.maneulyori.sdattr/fatattr");
			fileTest.close();
		}
		catch (IOException e)
		{
			try{
			fatattrstream = resources.getAssets().open("fatattr");
			byte[] buffer = new byte[fatattrstream.available()];
			fatattrstream.read(buffer);
			
			OutputStream fdattrFile = new FileOutputStream("/data/data/com.maneulyori.sdattr/fatattr");
			
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			
			outStream.write(buffer);
			Log.i("SDAttr", "Installing fatattr binary...");
			outStream.writeTo(fdattrFile);
			outStream.close();
			fdattrFile.close();
			
			Toast toast = Toast.makeText(this, "fatattr binary is successfully installed!", Toast.LENGTH_LONG);
			toast.show();
			firstrun = true;
		}
		catch (IOException f)
		{
			Log.e("SDAttr", "Cannot write fatattr binary!");
		}
		}
		
		if(firstrun)
		{
			Log.i("SDAttr", "Executing chmod 755 on fatattr");
			ShellInterface.runCommand("sync; chmod 755 /data/data/com.maneulyori.sdattr/fatattr");
		}
		
		if(ShellInterface.isSuAvailable())
		{
		Runnable fileFixer = new FileFixer();
		
		Toast toast = Toast.makeText(this, "Fixing file attribute...", Toast.LENGTH_SHORT);
		toast.show();
		
		Thread fixerThread = new Thread(fileFixer);

		fixerThread.setDaemon(true);
		fixerThread.setName("FixerThread");
		fixerThread.start();

			toast = Toast.makeText(this, "Finished. Now you can close this app.", Toast.LENGTH_LONG);
			toast.show();
		}
		else
		{
			Toast toast = Toast.makeText(this, "This app require ROOT permission to run!", Toast.LENGTH_LONG);
			toast.show();
		}
    }
}
