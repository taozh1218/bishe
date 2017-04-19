 package com.taozhang.filetransition.thread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.PublicKey;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.adapter.AdapterOfGridView_operationComputer;
import com.taozhang.filetransition.bean.FileInfo;
import com.taozhang.filetransition.ui.customComponent.WaterDropLoadingView;
import com.taozhang.filetransition.util.Constant;
import com.taozhang.filetransition.util.MessageUtil;
import com.taozhang.filetransition.util.SplitStringUtil;

/**
 * ����ͻ��˵��߳�
 * 
 * @author Administrator
 * 
 */
public class ServiceThread extends AsyncTask<Void, String, Void> {
	private Socket socket;
	private DataInputStream ips;
	private DataOutputStream ops;
	private WaterDropLoadingView water;
	private FrameLayout layout;
	private Button sending;
	private TextView name;
	public ServiceThread(Socket socket, FrameLayout layout) throws IOException {
		this.socket = socket;
		this.layout = layout;
		
		water = (WaterDropLoadingView) layout.findViewById(R.id.sendVoice_waterDrop);
		sending = (Button) layout.findViewById(R.id.button_sending);
		name = (TextView) layout.findViewById(R.id.tv_sendVoice_fileName);
	}

	
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	@Override
	protected Void doInBackground(Void... arg0) {

		try {
			ips = new DataInputStream(socket.getInputStream());
			ops = new DataOutputStream(socket.getOutputStream());
//			// �o�͑��˰l����Ϣ
//			MessageUtil.sendMsg("200", ops);
//			
//			String msg = MessageUtil.getMsg(ips);
//			Log.e("�ͻ��˷�������Ϣ", msg);
//			System.err.println("�Ѿ���ͻ��˷���������");
			
			// ��ȡ����
			String command = MessageUtil.getMsg(ips);
			if (command.equals(Constant.REQUESTFILES)) {
				// ��ͻ��˷����ļ�
				ArrayList<FileInfo> checkedFiles = AdapterOfGridView_operationComputer
						.getCheckedFiles();
				 ArrayList<FileInfo> fileInfos = AdapterOfGridView_operationComputer.fileInfos;
				 fileInfos.addAll(checkedFiles);
				// �ȷ��ļ�����
				MessageUtil.sendMsg(fileInfos.size() + "", ops);
				// �����ļ�
				for(FileInfo info : fileInfos){
					sendFile(info);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private float persent = 0;
	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
		// ���ݰٷְټ��������Ļ���
		float per = Float.parseFloat(values[0]);//�Ѿ����Ŵ���һ�ٱ�
		float du = per * WaterDropLoadingView.MAX / 100  ;
		persent += per ;
		sending.setText("���ڴ���..." + (int)persent + "%");
		if(du > 0){
			water.addSweepAngle(du);
			float value = water.getSweepAngle();
			Log.e("percent", value + "");
		}
		// ��ȡ���֣������ַ����ڶ�λ
		if(null != values[1] ){
			name.setText(values[1]);
		}
		
	}
	
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		// ֹͣ
		sending.setText("�������");
	}
	

	float increate = 0; //������
	/**
	 * ����һ���ļ�
	 */
	public boolean sendFile(FileInfo detail) {
		File file = new File(detail.filePath);
		publishProgress("0",file.getName());// ��һλ�ǽ��ȣ��ڶ�λ���ļ���
		try {
			FileInputStream fis = new FileInputStream(file);
			String name = file.getName();
			long size = file.length();
			ops.writeUTF(name);// �ȷ��ļ���
			ops.flush();
			ops.writeLong(size);// �ļ���С
			ops.flush();
			// buffer
			byte[] buffer = new byte[MessageUtil.BUFFERSIZE];
			int read = 0;
			
			while ((read = fis.read(buffer, 0, buffer.length)) != -1) {
				ops.write(buffer, 0, read);
				// ����ÿ�ζ���
				increate += read; //��¼����
				// �������ڰٷ�֮1
				float value = increate * 100 / size ;
				if( value > 1){
					Log.e("���Ȱٷֱ�", value + "--------------");
					// ÿ�ζ�Ҫ����λ ��ͳһ���ȣ���ȻԽ��
					publishProgress(value + "",null);
					// ��������
					increate = 0;
				}
				
			}
			// �����Ӧ���ļ�
			AdapterOfGridView_operationComputer.cleanCheckFile(detail);
			//��ʷ��¼���泤��������Ҫ������ļ�
			AdapterOfGridView_operationComputer.fileInfos.remove(detail);
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}



	public File receiveFile() {
		File file = null;
		try {
			String name = ips.readUTF();// �ļ���
			long size = ips.readLong();// size
			byte[] buffer = new byte[MessageUtil.BUFFERSIZE];
			int total = 0;

			String type = SplitStringUtil.getTypeBySplit(name);
			String path = SplitStringUtil.getFilePathByType(type);
			
			file = new File(path, name);
			FileOutputStream fos = new FileOutputStream(file);
			int read = 0;
			while ((read = ips.read(buffer, 0, buffer.length)) != -1) {
				// д�ļ�
				fos.write(buffer, 0, read);
				total += read;
			}
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return file;
	}

	

	
//	public   void receiveFiles() {
//		String mFilePath = Constant.BASEPATH_ELSE;
//	    File dirs = new File(mFilePath);
//	    if (!dirs.exists()) {
//	      dirs.mkdirs();
//	    }
//	    int fileNum = 0;
//	    long totalSize = 0;
//	    List<FileInfo>  fileInfos = null;
//	    try {
//	      fileNum = ips.readInt();
//	      fileInfos = new ArrayList<FileInfo>();
//	      
//	      for (int i = 0; i < fileNum; i++) {
//	        FileInfo file = new FileInfo();
//	        file.fileName = ips.readUTF();
//	        file.fileSize = ips.readLong();
//	        fileInfos.add(file);
//	      }
//	      totalSize = ips.readLong();
//	    } catch (IOException e) {
//	      e.printStackTrace();
//	      System.exit(0);
//	    }
////	    System.out.println(fileNum);
////	    System.out.println(totalSize);
////	    for (FileInfo fileinfo : fileInfos) {
////	      System.out.println(fileinfo.fileName);
////	      System.out.println(fileinfo.fileSize);
////	    }
//	    // // /////////////////////////////////////////////////////////////////
//	    int leftLen = 0; // д���ļ��󻺴�����ʣ����ֽڳ��ȡ�
//	    int bufferedLen = 0; // ��ǰ�������е��ֽ���
//	    int writeLen = 0; // ÿ�����ļ���д����ֽ���
//	    long writeLens = 0; // ��ǰ�Ѿ��򵥸��ļ���д����ֽ�����
//	    long totalWriteLens = 0; // д��������ֽ���
//	    byte buf[] = new byte[8192];
//	    for (int i = 0; i < fileNum; i++) {
//	      writeLens = 0;
//	      try {
//	        FileOutputStream fout = new FileOutputStream(mFilePath + "/"
//	            + fileInfos.get(i).fileName);
//	        while (true) {
//	          if (leftLen > 0) {
//	            bufferedLen = leftLen;
//	          } else {
//	            bufferedLen = ips.read(buf);
//	          }
//	          if (bufferedLen == -1)
//	            return;
//	          System.out.println("readlen" + bufferedLen);
//	          // �����д���ļ����ֽ������ϻ������е��ֽ����Ѵ����ļ��Ĵ�С��ֻд�뻺�����Ĳ������ݡ�
//	          if (writeLens + bufferedLen >= fileInfos.get(i).fileSize) {
//	            leftLen = (int) (writeLens + bufferedLen - fileInfos.get(i).fileSize);
//	            writeLen = bufferedLen - leftLen;
//	            fout.write(buf, 0, writeLen); // д�벿��
//	            totalWriteLens += writeLen;
////	            move(buf, writeLen, leftLen);
//	            break;
//	          } else {
//	            fout.write(buf, 0, bufferedLen); // ȫ��д��
//	            writeLens += bufferedLen;
//	            totalWriteLens += bufferedLen;
//	            if (totalWriteLens >= totalSize) {
//	              //mListener.report(GroupChatActivity.FAIL, null);
//	              return;
//	            }
//	            leftLen = 0;
//	          }
//	          //mListener.report(GroupChatActivity.PROGRESS,
//	              //(int) (totalWriteLens * 100 / totalSize));
//	        } // end while
//	        fout.close();
//
//	      } catch (Exception e) {
//	        e.printStackTrace();
//	      }
//	    } // end for
//	    //mListener.report(GroupChatActivity.FAIL, null);
//	  }
	
//	public  boolean  sendFiles(List<FileInfo> files) {  
//        long totalSize = 0;  
//        byte buf[] = new byte[8192];  
//        int len;  
//        try {  
//            ops.writeInt(files.size());  
//            for(FileInfo file : files){
//                ops.writeUTF(file.fileName);  
//                ops.flush();  
//                ops.writeLong(file.fileSize);  
//                ops.flush();  
//                totalSize += file.fileSize;  
//            }  
//            ops.writeLong(totalSize);  
//            BufferedInputStream din = null;
//            for(FileInfo file : files){
//            	din = new BufferedInputStream(  
//                        new FileInputStream(file.filePath));  
//                while ((len = din.read(buf)) != -1) {  
//                    ops.write(buf, 0, len);  
//                }  
//            }  
//            din.close();
//            System.out.println("�ļ��������");  
//  
//        } catch (Exception e) {  
//            e.printStackTrace();  
//            return false;
//        }  
//        return true;  
//    }  
}
