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


public class DBUploader extends Task {



	public String getFile2upload() {
		return file2upload;
	}

	public void setFile2upload(String file2upload) {
		this.file2upload = file2upload;
	}

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

	//the file to be uploaded (full qualified file path)
	private String file2upload;

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

		if( (getFile2upload()==null||getFile2upload().length()==0) || (getAppidentifier()==null||getAppidentifier().length()==0) ){
			log("the variables need to be defined for this task to work");
			return;
		}

		try {
			doAllTheWork(
					getFile2upload(),
					getAppidentifier()
					);
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }

	public static void main(String[] args) {

		DBUploader ms=new DBUploader();
		ms.setAppidentifier("flourishApp");
		ms.setAppkey("p9xxFAKExxx");
		ms.setFile2upload("C:\\Temp\\random picture.png");
		try {
			ms.doAllTheWork(
					ms.getFile2upload(),
					ms.getAppidentifier()
					);
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
	private void createDirectoryIfNeeded(String directoryName)
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

	private void doAllTheWork(String file2Upload, String appidentifier) throws DbxException, IOException {

		String accessToken = (getAccesstoken()!=null)?getAccesstoken():"";

        DbxRequestConfig config = new DbxRequestConfig(
        		appidentifier,
	            Locale.getDefault().toString()
	            );


        DbxClient client = new DbxClient(config, accessToken);

        boolean error=true;
        File inputFile = new File(file2Upload);
        while (error){ //if something went wrong, re-try, TODO: limit number of retries and introduce xponential backoff
	        FileInputStream inputStream = new FileInputStream(inputFile);
	        try {
	        	boolean skip=false;
	            //check if the file actually already exists

			    	   if(client.getMetadataWithChildren("/"+inputFile.getName())!=null)skip=true;

	        	if(!skip){
	        		DbxEntry.File uploadedFile = client.uploadFile("/"+inputFile.getName(),
	                DbxWriteMode.add(), inputFile.length(), inputStream);
	        		log("Uploaded: " + uploadedFile.toString());
	        	}
	        	else log("Upload skipped for file "+file2Upload+" as it appears to exist.");

	        error=false;
	        } catch (DbxException e) {
								e.printStackTrace();
							} finally {
	            inputStream.close();

	        }
        }


	}

}
