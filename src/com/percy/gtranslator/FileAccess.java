package com.percy.gtranslator;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.CharBuffer;

import android.R.integer;
import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;

public class FileAccess
{
	public static String	fileName	= "";

	public static String readFile(Context context, String file)
	{
		fileName = file;
		String data = "";

		FileInputStream fi = null;
		DataInputStream dis = null;
		InputStreamReader isr = null;

		// if don't exist,just create the file.
		FileOutputStream fos;
		try
		{

			// fos = context.openFileOutput(fileName,
			// context.MODE_WORLD_WRITEABLE);

			// fos.flush();
			// fos.close();
			fi = context.openFileInput(fileName);
			isr = new InputStreamReader(fi);

			dis = new DataInputStream(fi);

			char[] buffer = new char[1024];

			String line;

			while ((line = dis.readUTF()) != null)
			{
				data += line;
			}

			isr.close();
			dis.close();
			fi.close();
		} catch (Exception e)
		{
			// TODO: handle exception
		}

		return data;

	}

	public static void writeFile(Context context, String toWriteString)
	{
		try
		{
			FileOutputStream fos = context.openFileOutput(fileName,
					context.MODE_APPEND);
			//OutputStreamWriter dos = new OutputStreamWriter(fos);
			// dos.writeChars(toWriteString);
			DataOutputStream dos = new DataOutputStream(fos);
			dos.writeUTF(toWriteString+'\n');
			

			dos.flush();
			fos.flush();
			dos.close();
			fos.close();

		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			Toast.makeText(context, "File not found", 4000).show();
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
