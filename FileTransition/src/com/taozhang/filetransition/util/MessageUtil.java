package com.taozhang.filetransition.util;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.util.Log;

import com.taozhang.filetransition.bean.FileDetail;
import com.taozhang.filetransition.bean.FileInfo;

public class MessageUtil {

	public static int BUFFERSIZE = 5 * 1024;
	/**
	 * ������Ϣ�ķ���
	 * 
	 * @param msg
	 */
	public static void sendMsg(String msg, DataOutputStream dOps) {
		try {
			dOps.writeUTF(msg);
			dOps.flush();// ǿ�����
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * �ӿͻ��˻�ȡ��Ϣ�ķ���
	 * @param ips
	 * @return client messages
	 * @throws IOException
	 */
	public static String getMsg(DataInputStream dIps) throws IOException {
		String readUTF = dIps.readUTF();
		return readUTF;
	}

	/**
	 * ת16����
	 * 
	 * @param src
	 * @return
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder();
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}
	
	/**
	 * ����һ���ļ�
	 */
	public static boolean sendFile(FileInfo detail,DataOutputStream dos){
		
		File file = new File(detail.filePath);
		try {
			FileInputStream fis = new FileInputStream(file);
			String name = file.getName();
			long size = file.length();
			int total = 0;
			dos.writeUTF(name);// �ȷ��ļ���
			
			dos.writeLong(size);// �ļ���С
			// buffer
			byte[] buffer = new byte[BUFFERSIZE];
			int read = 0;
			while((read = fis.read(buffer,0,BUFFERSIZE)) != -1 ){
				dos.write(buffer, 0, read);
				total += read;
			}
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * ����һ���ļ�
	 */
	public static boolean sendFile(String filePath,DataOutputStream dos){
		
		File file = new File(filePath);
		try {
			FileInputStream fis = new FileInputStream(file);
			String name = file.getName();
			long size = file.length();
			int total = 0;
			dos.writeUTF(name);// �ȷ��ļ���
			
			dos.writeLong(size);// �ļ���С
			// buffer
			byte[] buffer = new byte[BUFFERSIZE];
			int read = 0;
			while((read = fis.read(buffer,0,BUFFERSIZE)) != -1){
				dos.write(buffer, 0, read);
				total += read;
			}
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	

	
	
	public static boolean  sendFiles(DataOutputStream dout, List<FileInfo> files) {  
        long totalSize = 0;  
        byte buf[] = new byte[8192];  
        int len;  
        try {  
           
             
            dout.writeInt(files.size());  
            for(FileInfo file : files){
            		
                dout.writeUTF(file.fileName);  
                dout.flush();  
                dout.writeLong(file.fileSize);  
                dout.flush();  
                totalSize += file.fileSize;  
            }  
            dout.writeLong(totalSize);  
            BufferedInputStream din = null;
            for(FileInfo file : files){
            	din = new BufferedInputStream(  
                        new FileInputStream(file.filePath));  
                while ((len = din.read(buf)) != -1) {  
                    dout.write(buf, 0, len);  
                }  
            }  
            din.close();
            System.out.println("�ļ��������");  
  
        } catch (Exception e) {  
            e.printStackTrace();  
            return false;
        }  
        return true;  
    }  
	
	
	public static File getFile(DataInputStream dis){
		File file = null;
		try {
			String name = dis.readUTF();//�ļ���
			long size = dis.readLong();
			int total = 0;
			byte[] buffer = new byte[BUFFERSIZE];
			

			String type = SplitStringUtil.getTypeBySplit(name);
			String path = SplitStringUtil.getFilePathByType(type);
			
			file = new File(path,name);
			FileOutputStream fos = new FileOutputStream(file);
			int read = 0;
			while((read = dis.read(buffer,0,BUFFERSIZE)) != -1){
				// д�ļ�
				fos.write(buffer, 0, read);
				total += read;
				
				
			}
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return file;
	}
	
	public static  void receiveFiles(DataInputStream din) {
		String mFilePath = Constant.BASEPATH_ELSE;
	    File dirs = new File(mFilePath);
	    if (!dirs.exists()) {
	      dirs.mkdirs();
	    }
	    int fileNum = 0;
	    long totalSize = 0;
	    List<FileInfo>  fileInfos = null;
	    try {
	      fileNum = din.readInt();
	      fileInfos = new ArrayList<FileInfo>();
	      
	      for (int i = 0; i < fileNum; i++) {
	        FileInfo file = new FileInfo();
	        file.fileName = din.readUTF();
	        file.fileSize = din.readLong();
	        fileInfos.add(file);
	      }
	      totalSize = din.readLong();
	    } catch (IOException e) {
	      e.printStackTrace();
	      System.exit(0);
	    }
	    System.out.println(fileNum);
	    System.out.println(totalSize);
	    for (FileInfo fileinfo : fileInfos) {
	      System.out.println(fileinfo.fileName);
	      System.out.println(fileinfo.fileSize);
	    }
	    // // /////////////////////////////////////////////////////////////////
	    int leftLen = 0; // д���ļ��󻺴�����ʣ����ֽڳ��ȡ�
	    int bufferedLen = 0; // ��ǰ�������е��ֽ���
	    int writeLen = 0; // ÿ�����ļ���д����ֽ���
	    long writeLens = 0; // ��ǰ�Ѿ��򵥸��ļ���д����ֽ�����
	    long totalWriteLens = 0; // д��������ֽ���
	    byte buf[] = new byte[8192];
	    for (int i = 0; i < fileNum; i++) {
	      writeLens = 0;
	      try {
	        FileOutputStream fout = new FileOutputStream(mFilePath + "/"
	            + fileInfos.get(i).fileName);
	        while (true) {
	          if (leftLen > 0) {
	            bufferedLen = leftLen;
	          } else {
	            bufferedLen = din.read(buf);
	          }
	          if (bufferedLen == -1)
	            return;
	          System.out.println("readlen" + bufferedLen);
	          // �����д���ļ����ֽ������ϻ������е��ֽ����Ѵ����ļ��Ĵ�С��ֻд�뻺�����Ĳ������ݡ�
	          if (writeLens + bufferedLen >= fileInfos.get(i).fileSize) {
	            leftLen = (int) (writeLens + bufferedLen - fileInfos.get(i).fileSize);
	            writeLen = bufferedLen - leftLen;
	            fout.write(buf, 0, writeLen); // д�벿��
	            totalWriteLens += writeLen;
//	            move(buf, writeLen, leftLen);
	            break;
	          } else {
	            fout.write(buf, 0, bufferedLen); // ȫ��д��
	            writeLens += bufferedLen;
	            totalWriteLens += bufferedLen;
	            if (totalWriteLens >= totalSize) {
	              //mListener.report(GroupChatActivity.FAIL, null);
	              return;
	            }
	            leftLen = 0;
	          }
	          //mListener.report(GroupChatActivity.PROGRESS,
	              //(int) (totalWriteLens * 100 / totalSize));
	        } // end while
	        fout.close();

	      } catch (Exception e) {
	        e.printStackTrace();
	      }
	    } // end for
	    //mListener.report(GroupChatActivity.FAIL, null);
	  }
}
