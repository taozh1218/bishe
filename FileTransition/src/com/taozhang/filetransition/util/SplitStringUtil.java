package com.taozhang.filetransition.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 
 * @Description:
 * 
 * @author taozhang
 * @created 2016年5月29日 下午8:21:18
 *
 */
public class SplitStringUtil {

	// private static String stringDemo = "I love tt";
	private static String stringDemo = "Android驱动开发与移植实战详解.pdf.pdf";

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
		StringTokenizer token = new StringTokenizer(string, regular);// 按照空格和逗号进行截取
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
		int indexOf = temp.indexOf(regular);// 获取第一个匹配的index
		if (indexOf == -1) {
			System.out.println("未找到匹配的！");
			return null;
		}
		// 第一次从0开始截取，之后从该位置+1开始截取
		String substring = temp.substring(0, indexOf);// 截取从下表0到index
		while (indexOf != -1) {// -1为匹配结束
			System.err.println("temp:" + temp);
			System.err.println("substring:" + substring);// 打印截取的
			System.err.println("indexOf：" + indexOf + "");
			list.add(substring);
			temp = temp.substring(indexOf + 1);// 去除temp的第一段
			indexOf = temp.indexOf(regular);// 再次获取第一个匹配的index
			substring = temp.substring(0);//1.必须+1才能正常截取，2.可以避免是-1
			System.out.println(substring);
		}
		System.err.println("temp:" + temp + ",subString:" + substring
				+ ",index:" + indexOf);
		list.add(temp);
		return list;
	}

}
