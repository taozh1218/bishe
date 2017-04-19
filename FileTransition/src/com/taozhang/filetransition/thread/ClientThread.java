package com.taozhang.filetransition.thread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.PublicKey;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.adapter.AdapterOfGridView_operationComputer;
import com.taozhang.filetransition.base.App;
import com.taozhang.filetransition.bean.FileInfo;
import com.taozhang.filetransition.ui.customComponent.WaterDropLoadingView;
import com.taozhang.filetransition.util.Connect;
import com.taozhang.filetransition.util.Constant;
import com.taozhang.filetransition.util.MessageUtil;
import com.taozhang.filetransition.util.SplitStringUtil;

/**
 * �ͻ����̣߳����ܷ������������ļ�
 * 
 * @author Administrator
 * 
 */
public class ClientThread extends AsyncTask<String, String, Void> {

	private DataInputStream ips;
	private DataOutputStream ops;
	private WaterDropLoadingView water;
	private FrameLayout layout;
	private Button recieving;
	private TextView name;

	public ClientThread(FrameLayout layout) throws IOException {
		this.layout = layout;
		water = (WaterDropLoadingView) layout
				.findViewById(R.id.receiveVoice_waterDrop);
		recieving = (Button) layout.findViewById(R.id.recieveTitle);
		name = (TextView) layout.findViewById(R.id.tv_receiveVoice_fileName);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(String... value) {
		try {

			InetSocketAddress address = new InetSocketAddress(value[0],
					Constant.PORT);
			Connect con = Connect.getInstance();
			
			// con.disConnect();
			int times = 0;
			while (!con.connectServer(address, Constant.PORT)) {
				Thread.sleep(100);
				times++;
				if (times > 100) {
					// Э�飺 ��һλ �ǽ��Ȱٷֱȣ��ڶ�λ���ļ���������λ���ź�
					publishProgress("0", null, "Error");
				}

			}
			ips = con.dIps;
			ops = con.dOps;
			publishProgress("0",null,"OK");
			// mHanlder.sendEmptyMessage(200);
//			String recieve;
//			recieve = con.getMsg();
//			Log.e("���ܵ���Ϣ", recieve);
//			if (recieve.equals("200")) {
//				Log.e("asd", "�лظ���");
//			}
//			con.sendMsg("�뷢����Ϣ");
			con.sendMsg(Constant.REQUESTFILES);// ����һ�٣���������������ļ�����
			// �������ļ�����
			int count = Integer.parseInt(con.getMsg());
			while (count > 0) {
				receiveFile();
				Log.e("�����ļ��ɹ���������", "--------------------");
				count--;
			}
			// mAct.mHanlder.sendEmptyMessage(400);
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return null;

	}

	private float persent = 0;

	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
		// ���ݰٷְټ��������Ļ���
		float per = Float.parseFloat(values[0]);// �Ѿ����Ŵ���һ�ٱ�
		float du = per * WaterDropLoadingView.MAX / 100;
		persent += per;
		if(persent >= 99){
			publishProgress("0",null,"Finish");
		}
		recieving.setText("���ڽ���..." + (int) persent + "%");
		if (du > 0) {
			water.addSweepAngle(du);
			float value = water.getSweepAngle();
			Log.e("percent", value + "");
		}
		// ��ȡ���֣������ַ����ڶ�λ
		if (null != values[1]) {
			name.setText(values[1]);
		}
		// �ź�
		if (null != values[2]) {
			if ("Error".equals(values[2])) {
				Toast.makeText(App.context, "���ӳ�ʱ", Toast.LENGTH_SHORT).show();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.exit(0);
			}else if("OK".equals(values[2])){
				layout.setVisibility(View.VISIBLE);
			}else if("Finish".equals(values[2])){
				// ���
				recieving.setText("�������");
			}
		}

	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		// ֹͣ
		recieving.setText("�������");
	}

	float increate = 0; // ������

	/**
	 * ����һ���ļ�
	 */
	public boolean sendFile(FileInfo detail) {
		File file = new File(detail.filePath);

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
			}
			// �����Ӧ���ļ�
			AdapterOfGridView_operationComputer.cleanCheckFile(detail);
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
			publishProgress("0", name, null);// ��һλ�ǽ��ȣ��ڶ�λ���ļ���,�������ź�
			long size = ips.readLong();// size
			byte[] buffer = new byte[MessageUtil.BUFFERSIZE];

			String type = SplitStringUtil.getTypeBySplit(name);
			String path = SplitStringUtil.getFilePathByType(type);

			file = new File(path, name);
			FileOutputStream fos = new FileOutputStream(file);
			int read = 0;
			while ((read = ips.read(buffer, 0, buffer.length)) != -1) {
				// д�ļ�
				fos.write(buffer, 0, read);
				// ����ÿ�ζ���
				increate += read; // ��¼����
				// �������ڰٷ�֮1
				float value = increate * 100 / size;
				if (value > 1) {
					Log.e("���Ȱٷֱ�", value + "--------------");
					// ÿ�ζ�Ҫ����λ ��ͳһ���ȣ���ȻԽ��
					publishProgress(value + "", null, null);
					// ��������
					increate = 0;
				}
			}
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return file;
	}

}
