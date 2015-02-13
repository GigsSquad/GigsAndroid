package pl.javaparty.imageloader;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import android.content.Context;
import android.util.Log;

public class FileExplorer
{
	private File fileDir;
	private final String IMAGES_DIR = new String("/bandImages");
	
	public FileExplorer(Context context)
	{
//		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))//jesli jest karta SD
//            fileDir=new File(android.os.Environment.getExternalStorageDirectory(),IMAGES_DIR);
//		else
			fileDir= new File(context.getFilesDir(), IMAGES_DIR);
        Log.i("DIR", fileDir.getPath());
		if(!fileDir.exists())
			fileDir.mkdirs();
	}
	
	public File getFile(String fileName)//hashedfilename
	{
		return new File(fileDir, hashName(fileName));
	}
	
	private String hashName(String bandName)
	{
		bandName = bandName.toUpperCase(Locale.ENGLISH);
		try {
	        MessageDigest digest = MessageDigest.getInstance("MD5");
	        byte[] hashedBytes = digest.digest(bandName.getBytes("UTF-8"));
	 
	        return convertByteArrayToHexString(hashedBytes);
		}
		catch (NoSuchAlgorithmException | UnsupportedEncodingException ex)
		{
			ex.printStackTrace();
			return bandName;
		}
	}
		
	//skopiowane bezczelnie :D
	private String convertByteArrayToHexString(byte[] arrayBytes)
	{
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < arrayBytes.length; i++)
		{
			stringBuffer.append(Integer.toString(
					(arrayBytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		return stringBuffer.toString();
	}
	
	public void clear()
	{
		File[] allFiles = fileDir.listFiles();
		if (allFiles != null)
		{
			for (File f : allFiles)
			{
				f.delete();
			}
		}
	}
}
