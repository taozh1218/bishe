package com.taozhang.filetransition.ui;

import com.taozhang.filetransition.R;
import com.taozhang.filetransition.R.layout;
import com.taozhang.filetransition.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class HelpActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);

		getComponent();
	}

	private void getComponent() {
		TextView tv_transition = (TextView) findViewById(R.id.tv_help_first);
		TextView tv_computer = (TextView) findViewById(R.id.tv_help_second);
		TextView tv_history = (TextView) findViewById(R.id.tv_help_third);

		tv_transition
				.setText("�ֻ�����ҳ�棬�������ǳ�����עĿ�İ�ť��\"��Ҫ����\"��\"��Ҫ����\"��\n\n������Ҫ�����ļ�������ʱ��\n1.�������Ͱ�ť��Ȼ��ͻ���ת���ļ�ѡ���ҳ�棬����ѡ����Ҫ���͵��ļ���\n2.ѡ����ɺ��������Ͻǵ�\"���\"��ť��\n3.���������ᷢ��һ��������Ϊ�˷���ʶ������ֻ����������ߣ���������ֻ��ȴ��ļ��Ĵ�����ɡ�\n\n������Ҫ�����ļ�ʱ��\n1.������Ҫ���հ�ť");

		tv_computer.setText("");

		tv_history
				.setText("��ʷ�ļ�ҳ����ʾ�������յ����ļ������ҽ����˼򵥵ķ��ࡣ\n�����ҳ�棬��ֻ�賤��Ŀ���ļ������Զ���ʷ�ļ����м򵥵��ļ�������Ʃ�磬���ơ�ճ�������еȡ�\n���⣬��Ŀ���ļ������󻬣�����ʵ��ɾ����ʷ�ļ��Ĺ��ܣ�ע�⣬��ͬʱɾ�������ļ����������������");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.help, menu);
		return true;
	}

}
