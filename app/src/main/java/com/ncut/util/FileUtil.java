package com.ncut.util;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;


public class FileUtil {
	private String SDPATH=null;
	public String getSDPATH()
	{
		return SDPATH;
	}
	public FileUtil()
	{
		//获得当前外部存储设备SD卡的目录
		SDPATH=Environment.getExternalStorageDirectory()+"/";
	}
	//创建文件
	public File createFile(String fileName) throws IOException
	{
		File file=new File(SDPATH+fileName);
		file.getParentFile().mkdirs();
		file.createNewFile();
		return file;
	}
	//创建目录
	public File createDir(String fileName) throws IOException
	{
		File dir=new File(SDPATH+fileName);		
		dir.mkdirs();
		return dir;
	}
	//判断文件是否存在
	public boolean isExist(String fileName)
	{
		File file=new File(SDPATH+fileName);
		return file.exists();
	}
	public File writeToSDPATHFromInput(String path,String fileName,InputStream inputstream)
	{
		File file=null;
		OutputStream outputstream=null;
	    try
		{
	    	createDir(path);
	    	file=createFile(path+fileName);
	    	ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
	    	outputstream=new FileOutputStream(file,false);
	    	byte buffer[]=new byte[2024];
	    	int len = 0;
	    	//将输入流中的内容先输入到buffer中缓存，然后用输出流写到文件中
	    	 while ((len = inputstream.read(buffer)) != -1) {  
	             outStream.write(buffer, 0, len);  
	         } 
	    	 outputstream.write(outStream.toByteArray());
	    	 
		}
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	    finally
	    {
	    	try {
				outputstream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    return file;
	}
}
