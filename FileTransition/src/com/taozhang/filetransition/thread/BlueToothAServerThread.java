package com.taozhang.filetransition.thread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.base.App;
import com.taozhang.filetransition.ui.BluetoothActivity;
import com.taozhang.filetransition.ui.customComponent.WaterDropLoadingView;
import com.taozhang.filetransition.util.MessageUtil;
import com.taozhang.filetransition.util.SplitStringUtil;

/**
 * �������õ�ģʽ�Ƿ����������ļ�
 * 
 * @author Administrator
 * 
 */
public class BlueToothAServerThread extends AsyncTask<Void, String, Void> {

	private static final String TAG = "TAG";
	private BluetoothServerSocket mServerSocket;
	private DataInputStream dis;
	private DataOutputStream dos;

	private WaterDropLoadingView water;
	private FrameLayout layout;
	private Button recieving;
	private TextView name;
	private BluetoothAdapter bluetoothAdapter;

	public BlueToothAServerThread(BluetoothAdapter bluetoothAdapter,
			FrameLayout layout) {
		super();
		this.bluetoothAdapter = bluetoothAdapter;
		this.layout = layout;
		water = (WaterDropLoadingView) layout
				.findViewById(R.id.bluetooth_receiveVoice_waterDrop);
		recieving = (Button) layout.findViewById(R.id.bluetooth_recieveTitle);
		name = (TextView) layout
				.findViewById(R.id.bluetooth_tv_receiveVoice_fileName);

	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	private boolean flag = true;

	@Override
	protected Void doInBackground(Void... arg0) {
		BluetoothSocket socket = null;
		// Keep listening until exception occurs or a socket is returned

		try {
			mServerSocket = bluetoothAdapter
					.listenUsingRfcommWithServiceRecord("�ļ�����",
							BluetoothActivity.MY_UUID);
			while (flag) {
				socket = mServerSocket.accept();// û������ʱ��������
				publishProgress("0", null, "200");
				
				BluetoothDevice remoteDevice = socket.getRemoteDevice();

				dis = new DataInputStream(socket.getInputStream());
				// �������ļ�����
				int count = Integer.parseInt(MessageUtil.getMsg(dis));
				// ��һ�����������ٷֱȣ��ڶ����ļ������������ź�
				publishProgress("0", null, "OK");
				Log.e("�������ļ�����", count + "-----------------");
				while (count > 0) {
					receiveFile();
					Log.e("�����ļ��ɹ���������", "--------------------");
					count--;
				}
				publishProgress("0", null, "Finish");
				
			}
			

		} catch (IOException e) {
			e.printStackTrace();
			flag = false;
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
		if (persent >= 99) {
			publishProgress("0", null, "Finish");
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
			} else if ("OK".equals(values[2])) {
				layout.setVisibility(View.VISIBLE);
			} else if ("Finish".equals(values[2])) {
				// ���
				recieving.setText("�������");
			} else if("200".equals(values[2])){
				Toast.makeText(App.context, "���������������ɹ�", Toast.LENGTH_SHORT).show();
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
	// public boolean sendFile(FileInfo detail) {
	// File file = new File(detail.filePath);
	// publishProgress("0", file.getName(),null);// ��һλ�ǽ��ȣ��ڶ�λ���ļ���,�����ź�
	// try {
	// FileInputStream fis = new FileInputStream(file);
	// String name = file.getName();
	// long size = file.length();
	// dos.writeUTF(name);// �ȷ��ļ���
	// dos.flush();
	// dos.writeLong(size);// �ļ���С
	// dos.flush();
	// // buffer
	// byte[] buffer = new byte[MessageUtil.BUFFERSIZE];
	// int read = 0;
	//
	// while ((read = fis.read(buffer, 0, buffer.length)) != -1) {
	// dos.write(buffer, 0, read);
	// // ����ÿ�ζ���
	// increate += read; // ��¼����
	// // �������ڰٷ�֮1
	// float value = increate * 100 / size;
	// if (value > 1) {
	// Log.e("���Ȱٷֱ�", value + "--------------");
	// // ÿ�ζ�Ҫ����λ ��ͳһ���ȣ���ȻԽ��
	// publishProgress(value + "", null,null);
	// // ��������
	// increate = 0;
	// }
	//
	// }
	// // �����Ӧ���ļ�
	// AdapterOfGridView_operationComputer.cleanCheckFile(detail);
	// // ��ʷ��¼���泤��������Ҫ������ļ�
	// AdapterOfGridView_operationComputer.fileInfos.clear();
	// fis.close();
	// } catch (Exception e) {
	// e.printStackTrace();
	// return false;
	// }
	// return true;
	// }

	public File receiveFile() {
		File file = null;
		try {
			String name = dis.readUTF();// �ļ���
			long size = dis.readLong();// size
			publishProgress("0", name, null);
			byte[] buffer = new byte[MessageUtil.BUFFERSIZE];

			String type = SplitStringUtil.getTypeBySplit(name);
			String path = SplitStringUtil.getFilePathByType(type);

			file = new File(path, name);
			FileOutputStream fos = new FileOutputStream(file);
			int read = 0;
			while ((read = dis.read(buffer, 0, buffer.length)) != -1) {
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
