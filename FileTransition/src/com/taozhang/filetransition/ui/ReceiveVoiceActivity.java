package com.taozhang.filetransition.ui;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.libra.sinvoice.LogHelper;
import com.libra.sinvoice.SinVoiceRecognition;
import com.taozhang.filetransition.R;
import com.taozhang.filetransition.accesspoint.AccessPointManager;
import com.taozhang.filetransition.accesspoint.WifiConnectManager;
import com.taozhang.filetransition.base.App;
import com.taozhang.filetransition.thread.ClientThread;
import com.taozhang.filetransition.ui.customComponent.RippleOutLayout;
import com.taozhang.filetransition.ui.customComponent.WaterDropLoadingView;
import com.taozhang.filetransition.util.Connect;
import com.taozhang.filetransition.util.Constant;
import com.taozhang.filetransition.util.MessageUtil;

/**
 * ����������activity ���ǿͻ��ˣ�������Ҫ���͵��˵��� �����Ҫ���Ͱ�ť��ѡ���ļ���Ȼ������������ܷ�����������ip��Ȼ�����ӷ����ļ�
 * 
 * @author Administrator
 * 
 */
public class ReceiveVoiceActivity extends Activity implements
		SinVoiceRecognition.Listener {
	private final static int[] TOKENS = { 32, 32, 32, 32, 32, 32 };
	private final static int TOKEN_LEN = TOKENS.length;
	private boolean mIsReadFromFile;

	private final static String TAG = "MainActivityxx";

	private final static int MSG_SET_RECG_TEXT = 1;
	private final static int MSG_RECG_START = 2;
	private final static int MSG_RECG_END = 3;
	private final static int MSG_PLAY_TEXT = 4;

	private final static String BAKCUP_LOG_PATH = "/sinvoice_backup";

	private Handler mHanlder;

	private SinVoiceRecognition mRecognition;

	public String mSdcardPath;
	private PowerManager.WakeLock mWakeLock;
	// private TextView mRegState;
	private char mRecgs[] = new char[100];
	private int mRecgCount;
	private RippleOutLayout relayout;// ����
	private Button title;
	private WifiConnectManager wifiConnect;
	private WifiManager mWifiManager;
	private AccessPointManager accessPoint;

	// frameLayout component
	private static FrameLayout frame;
	private static WaterDropLoadingView water;
	private static TextView tv_fileName;

	static {
		System.loadLibrary("sinvoice");
		LogHelper.d(TAG, "sinvoice jnicall loadlibrary");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewpager_second);
		mHanlder = new RegHandler(this);
		mIsReadFromFile = false;
		accessPoint = new AccessPointManager(this);
		// ����Ѿ������ȵ㣬�رգ�����Ҫ���ӷ��ͷ����ȵ㣬�����¹��ȵ㣬��wifi
		accessPoint.stopWifiAp();
		// ap
		wifiConnect = new WifiConnectManager(App.context);
		wifiConnect.openWifi();
		mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, TAG);

		mSdcardPath = Environment.getExternalStorageDirectory().getPath();
		mRecognition = new SinVoiceRecognition();
		mRecognition.init(this);
		mRecognition.setListener(this);

		title = (Button) findViewById(R.id.receiveVoiceTitle);
		relayout = (RippleOutLayout) findViewById(R.id.voiceAnimation);
		relayout.startRippleAnimation();

		getComponent();
	}

	private void getComponent() {
		frame = (FrameLayout) findViewById(R.id.receiveVoice_frame);
		water = (WaterDropLoadingView) findViewById(R.id.receiveVoice_waterDrop);
		tv_fileName = (TextView) findViewById(R.id.tv_receiveVoice_fileName);
	}

	@Override
	public void onResume() {
		super.onResume();

		mWakeLock.acquire();
		// ����
		receiveVoice();
	}

	/**
	 * �������ݵķ���
	 * 
	 * @return
	 */
	private String receiveVoice() {
		mRecognition.start(TOKEN_LEN, mIsReadFromFile);
		return null;
	}

	/**
	 * ֹͣ�������ݵķ���
	 */
	private void stop() {
		mRecognition.stop();
	}

	private static class RegHandler extends Handler {
		private WeakReference<ReceiveVoiceActivity> mAct;
		ReceiveVoiceActivity activity;

		public RegHandler(ReceiveVoiceActivity act) {
			mAct = new WeakReference<ReceiveVoiceActivity>(act);
			activity = mAct.get();
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SET_RECG_TEXT:
				char ch = (char) msg.arg1;
				// mTextBuilder.append(ch);
				activity.mRecgs[activity.mRecgCount++] = ch;
				break;

			case MSG_RECG_START:
				// mTextBuilder.delete(0, mTextBuilder.length());
				activity.mRecgCount = 0;
				break;

			/**
			 * ��ȡ��Ϣ���� ��Ȼ�����ӷ����������ļ����͹�ȥ
			 */
			case MSG_RECG_END:
				LogHelper.d(TAG, "recognition end gIsError:" + msg.arg1);
				if (activity.mRecgCount > 0) {
					byte[] strs = new byte[activity.mRecgCount];
					for (int i = 0; i < activity.mRecgCount; ++i) {
						strs[i] = (byte) activity.mRecgs[i];
					}
					try {
						String strReg = new String(strs, "UTF8");
//						Toast.makeText(App.context, strReg, Toast.LENGTH_LONG)
//								.show();
						if (null != mAct) {
							// �õ�ip�����ӶԷ��ķ�����
							Log.e("���յ���IP�� ", strReg);
							// У��
							String[] code = strReg.split(":"); // �ļ�ͷ��Ϣ
							String ssid = null;
							String ip = null;

							if (code.length > 1) {
								ssid = code[0]; // ssid
								ip = code[1]; // ����
							}
							// ����
							activity.wifiConnect.connectToAccessPoint(ssid,
									null);

							int count = 0;
							while (!activity.mWifiManager.getConnectionInfo()
									.getSSID().contains(ssid)) {
								Thread.sleep(50);
								count ++;
								if(count > 200){
									break;
								}
								// �ȴ����Ӻ�ap
							}
							activity.mHanlder.sendEmptyMessage(200);
							// ����asyncTask
							// ap�ĵ�ַ��д����,�������������ַ
							new ClientThread(frame).execute("192.168.43.1");
						}

					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				break;

			case MSG_PLAY_TEXT:
				// mAct.mPlayTextView.setText(mAct.mPlayText);
				break;

			case 200:
				activity.relayout.stopRippleAnimation();
				 activity.title.setText("���ӳɹ�,���ڴ���");
//				activity.title.setVisibility(View.GONE);
				break;
			case 400:
				// ��ʱ��������
				Toast.makeText(App.context, "�������", Toast.LENGTH_SHORT).show();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Intent intent = new Intent(activity, MainActivity.class);
				activity.startActivity(intent);

			}

			super.handleMessage(msg);
		}
	}

	// private void receiveFiles(final String ssid, final InetSocketAddress
	// address) {
	// new Thread() {
	// @Override
	// public void run() {
	// super.run();
	//
	// try {
	//
	// // Connect con = Connect.getInstance();
	// //// con.disConnect();
	// // int times = 0;
	// // while (!con.connectServer(address, Constant.PORT)) {
	// // Thread.sleep(100);
	// // times++;
	// // if (times > 100) {
	// // mHanlder.post(new Runnable() {
	// // @Override
	// // public void run() {
	// // Toast.makeText(App.context, "�޷����ӷ�����",
	// // Toast.LENGTH_SHORT).show();
	// // Log.e("______===________", "�޷����ӷ�����");
	// // }
	// // });
	// // return;
	// // }
	// //
	// // }
	// // mHanlder.sendEmptyMessage(200);
	// // String recieve;
	// // recieve = con.getMsg();
	// // Log.e("���ܵ���Ϣ", recieve);
	// // if (recieve.equals("200")) {
	// // Log.e("asd", "�лظ���");
	// // }
	// // con.sendMsg("�뷢����Ϣ");
	// // con.sendMsg(Constant.REQUESTFILES);// ����һ�٣���������������ļ�����
	// // if (con != null) {
	// // // �������ļ�����
	// // int count = Integer.parseInt(MessageUtil
	// // .getMsg(con.dIps));
	// // while (count > 0) {
	// // MessageUtil.getFile(con.dIps);
	// // Log.e("�����ļ��ɹ���������", "--------------------");
	// // count--;
	// // }
	// // // mAct.mHanlder.sendEmptyMessage(400);
	// // }
	// // } catch (IOException e) {
	// // e.printStackTrace();
	// // } catch (InterruptedException e) {
	// // e.printStackTrace();
	// // }
	// //
	// // }
	// // }.start();
	//
	// }

	@Override
	public void onPause() {
		super.onPause();
		if (mWakeLock.isHeld()) {
			mWakeLock.release();
		}

		mRecognition.stop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		accessPoint.destroy(this);
		mRecognition.uninit();
		this.finish();

	}

	private void clearBackup() {
		delete(new File(mSdcardPath + BAKCUP_LOG_PATH));

		Toast.makeText(this, "clear backup log info successful",
				Toast.LENGTH_SHORT).show();
	}

	private static void delete(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}

		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}

			for (int i = 0; i < childFiles.length; i++) {
				delete(childFiles[i]);
			}
			file.delete();
		}
	}

	private void backup() {
		mRecognition.stop();

		String timestamp = getTimestamp();
		String destPath = mSdcardPath + BAKCUP_LOG_PATH + "/back_" + timestamp;
		try {
			copyDirectiory(destPath, mSdcardPath + "/sinvoice");
			copyDirectiory(destPath, mSdcardPath + "/sinvoice_log");

			FileOutputStream fout = new FileOutputStream(destPath + "/text.txt");
			// TODO Ŀ���ǰ����ݱ����ڱ���
			// String str = mPlayTextView.getText().toString();
			// byte[] bytes = str.getBytes();
			// fout.write(bytes);

			// str = mRecognisedTextView.getText().toString();
			// bytes = str.getBytes();
			// fout.write(bytes);

			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Toast.makeText(this, "backup log info successful", Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void onSinVoiceRecognitionStart() {
		mHanlder.sendEmptyMessage(MSG_RECG_START);
	}

	@Override
	public void onSinVoiceRecognition(char ch) {
		mHanlder.sendMessage(mHanlder.obtainMessage(MSG_SET_RECG_TEXT, ch, 0));
	}

	@Override
	public void onSinVoiceRecognitionEnd(int result) {
		mHanlder.sendMessage(mHanlder.obtainMessage(MSG_RECG_END, result, 0));
	}

	private static String getTimestamp() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
		return sdf.format(new Date());
	}

	private static void copyFile(File targetFile, File sourceFile)
			throws IOException {
		FileInputStream input = new FileInputStream(sourceFile);
		BufferedInputStream inBuff = new BufferedInputStream(input);

		FileOutputStream output = new FileOutputStream(targetFile);
		BufferedOutputStream outBuff = new BufferedOutputStream(output);

		byte[] b = new byte[1024 * 5];
		int len;
		while ((len = inBuff.read(b)) != -1) {
			outBuff.write(b, 0, len);
		}
		outBuff.flush();

		inBuff.close();
		outBuff.close();
		output.close();
		input.close();
	}

	private static void copyDirectiory(String targetDir, String sourceDir)
			throws IOException {
		(new File(targetDir)).mkdirs();
		File[] file = (new File(sourceDir)).listFiles();
		if (null != file) {
			for (int i = 0; i < file.length; i++) {
				if (file[i].isFile()) {
					File sourceFile = file[i];
					File targetFile = new File(
							new File(targetDir).getAbsolutePath()
									+ File.separator + file[i].getName());
					copyFile(targetFile, sourceFile);
				}
				if (file[i].isDirectory()) {
					String srcPath = sourceDir + "/" + file[i].getName();
					String targetPath = targetDir + "/" + file[i].getName();
					copyDirectiory(targetPath, srcPath);
				}
			}
		}
	}

}
