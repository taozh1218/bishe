package com.taozhang.filetransition.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 
 * @Description:
 * 
 * @author taozhang
 * @created 2016��5��29�� ����8:21:18
 *
 */
public class SplitStringUtil {

	// private static String stringDemo = "I love tt";
	private static String stringDemo = "Android������������ֲʵս���.pdf.pdf";

	private static List<String> list;

	public static List<String> getArrayBySplit(String string, String regular) {
		list = new ArrayList<String>();
		String[] split = string.split(regular);
		for (String string2 : split) {
			System.out.println(string2);
		}
		return list;
	}

	public static List<String> getArrayByStringTokenizer(String string,
			String regular) {
		list = new ArrayList<String>();
		StringTokenizer token = new StringTokenizer(string, regular);// ���տո�Ͷ��Ž��н�ȡ
		while (token.hasMoreTokens()) {
			String nextToken = token.nextToken();
			list.add(nextToken);
			System.out.println(nextToken);
		}
		return list;
	}

	public static List<String> getArrayByIndexOf(String string, String regular) {
		list = new ArrayList<String>();

		String temp = string;
		int indexOf = temp.indexOf(regular);// ��ȡ��һ��ƥ���index
		if (indexOf == -1) {
			System.out.println("δ�ҵ�ƥ��ģ�");
			return null;
		}
		// ��һ�δ�0��ʼ��ȡ��֮��Ӹ�λ��+1��ʼ��ȡ
		String substring = temp.substring(0, indexOf);// ��ȡ���±�0��index
		while (indexOf != -1) {// -1Ϊƥ�����
			System.err.println("temp:" + temp);
			System.err.println("substring:" + substring);// ��ӡ��ȡ��
			System.err.println("indexOf��" + indexOf + "");
			list.add(substring);
			temp = temp.substring(indexOf + 1);// ȥ��temp�ĵ�һ��
			indexOf = temp.indexOf(regular);// �ٴλ�ȡ��һ��ƥ���index
			substring = temp.substring(0);//1.����+1����������ȡ��2.���Ա�����-1
			System.out.println(substring);
		}
		System.err.println("temp:" + temp + ",subString:" + substring
				+ ",index:" + indexOf);
		list.add(temp);
		return list;
	}

}
