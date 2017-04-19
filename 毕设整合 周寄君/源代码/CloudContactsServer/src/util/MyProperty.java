package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class MyProperty {
private static MyProperty instance = new MyProperty();

	public String getProperty(String key) {
		if ((key == null) || ("".equals(key))) {
			return "";
		}
		InputStream inputStream = getClass().getClassLoader()
				.getResourceAsStream("config.properties");
		Properties p = new Properties();
		try {
			p.load(inputStream);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return p.getProperty(key);
	}

	public static MyProperty getInstance() {
		return instance;
	}
}
