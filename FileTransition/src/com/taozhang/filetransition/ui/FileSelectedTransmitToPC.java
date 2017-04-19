package com.taozhang.filetransition.ui;

import android.view.View;
import android.widget.Toast;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.adapter.AdapterOfGridView_operationComputer;
import com.taozhang.filetransition.base.App;
import com.taozhang.filetransition.bean.FileInfo;
import com.taozhang.filetransition.impl.FileSelecedImpl;
import com.taozhang.filetransition.util.Connect;
import com.taozhang.filetransition.util.Constant;
import com.taozhang.filetransition.util.MessageUtil;

public class FileSelectedTransmitToPC extends SelectedFileActivity {


	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.selected_file_finish:
			sendFiles();
			Toast.makeText(App.context, "�_ʼ�l��", Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}

	}

	private void sendFiles() {
		new Thread() {
			public void run() {
				Connect connect = Connect.getInstance();
				if (!connect.isConnected()) {
					connect.connectServer(FileListActivity.address,
							Constant.PCPORT);
				}
				// ��������
				connect.sendMsg(Constant.REQUEST_SEND_FEILS_TO_PC);
				// ��������
				int count = AdapterOfGridView_operationComputer
						.getCheckedFilesSize();
				connect.sendMsg(count + "");
				// �����ļ�
				for (FileInfo info : AdapterOfGridView_operationComputer
						.getCheckedFiles()) {
					MessageUtil.sendFile(info.filePath, connect.dOps);
				}

			};
		}.start();
	}

}
