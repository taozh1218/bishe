package com.plus.cloudcontacts.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.os.Environment;

public class DirUtil {
	public static boolean isFileExist(String filename) {
		File cache = new File(Environment.getExternalStorageDirectory(),
				"cloudcontacts_cache");
		if (!cache.exists()) {
			cache.mkdirs();
		}
		File file = new File(cache, filename);
		return file.exists();
	}

	public static String getFilePath(String filename) {
		return Environment.getExternalStorageDirectory() + File.separator
				+ "cloudcontacts_cache" + File.separator + filename;
	}

	public static void Copy(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) {
				InputStream inStream = new FileInputStream(oldPath);
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
//				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread;
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("error  ");
			e.printStackTrace();
		}
	}
}
