package com.moe.getjiggy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;

import org.apache.tools.ant.Task;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWriteMode;


public class DBCleanAppFolder extends Task {


	public String getAppidentifier() {
		return appidentifier;
	}

	public void setAppidentifier(String appidentifier) {
		this.appidentifier = appidentifier;
	}

	public String getAppkey() {
		return appkey;
	}

	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}

	//the registered app online on the dropbox developer console, without it nothing will work and the app name will determine where your files go within your DropBox account
	private String appidentifier;

	//the appkey is provided by dropbox
	private String appkey;

	//the access token is provided by dropbox after a successful pre-authentication
	private String accesstoken;

	public String getAccesstoken() {
		return accesstoken;
	}

	public void setAccesstoken(String accesstoken) {
		this.accesstoken = accesstoken;
	}

	/*
	 * The execute method is actually being kicked off by the ANT task to be run
	 *
	 * (non-Javadoc)
	 * @see org.apache.tools.ant.Task#execute()
	 */
	public void execute()  {

		if( ( (getAppidentifier()==null||getAppidentifier().length()==0) )){
			log("the variables need to be defined for this task to work");
			return;
		}

		try {
			doAllTheWork();
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }

	public static void main(String[] args) {

		DBCleanAppFolder ms=new DBCleanAppFolder();
		ms.setAppidentifier("flourishApp");
		ms.setAppkey("p9xxFAKExxx");

		try {
			ms.doAllTheWork();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	/**
	 *
	 * Clear files in the root target directory - will not clean sub-directories though
	 *
	 * @param directoryName
	 */
	private void clearParentDirIfExists(String directoryName){

		log("directory to be cleaned out: " + directoryName);
		File theDir = new File(directoryName);
		theDir = new File(theDir.getParent());

		for(File file: theDir.listFiles()) file.delete();

	}

	/**
	 * Generates the parent directory of the file in case it does not exist
	 * @param directoryName
	 */
	private  void createDirectoryIfNeeded(String directoryName)
	{
	  File theDir = new File(directoryName);
	  theDir = new File(theDir.getParent());


	  // if the directory does not exist, create it
	  if (!theDir.exists())
	  {
	    log("creating directory: " + theDir);
	    theDir.mkdirs();
	  }
	}

	private void doAllTheWork() throws DbxException, IOException {

		String accessToken = (getAccesstoken()!=null)?getAccesstoken():"";

        DbxRequestConfig config = new DbxRequestConfig(
        		getAppidentifier(),
	            Locale.getDefault().toString()
	            );


        DbxClient client = new DbxClient(config, accessToken);
        DbxEntry.WithChildren listing = null;

        try {
			listing=client.getMetadataWithChildren("/");
			for (DbxEntry child : listing.children) {
	          client.delete("/"+child.name);
	          log("Successfully deleted: "+child.name);
	          }


		} catch (DbxException e1) {
				log(e1.toString());
		}



	}






}
