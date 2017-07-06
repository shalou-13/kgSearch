package com.kgSearch.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;


public class JsonHandler {
	
	private static Logger logger = Logger.getLogger(JsonHandler.class);

	public static String readJsonFromRequest(HttpServletRequest req) {
		StringBuffer jsonBuf = new StringBuffer();
		char[] buf = new char[1024];
		int len = -1;
		try {
			BufferedReader reader = req.getReader();
			while ((len = reader.read(buf)) != -1) {
				jsonBuf.append(new String(buf, 0, len));
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		return jsonBuf.toString();
	}

	

	public static Map<String, Object> writeJsontoResponse(int statecode,
			Object obj) {
		Map<String, Object> map = new HashMap<String, Object>();
		JsonHandler jsonHandler = new JsonHandler();
		map.put("des", jsonHandler.read(String.valueOf(statecode)));
		map.put("state", statecode);
		map.put("result", obj);
		return map;
	}

	public static void writeJsonStreamFromResponse(HttpServletResponse response, String result) {
		response.setCharacterEncoding("utf-8");
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter pw;
		try {
			pw = response.getWriter();
			pw.append(result);
			pw.flush();
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		
	}
	
	public static void writeXMLStreamFromResponse(HttpServletResponse response, String result) {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/xml; charset=UTF-8");
		PrintWriter pw;
		try {
			pw = response.getWriter();
			pw.append(result);
			pw.flush();
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		
	}
	
	
	public String read(String name) {
		try {
			InputStream in = null;
			in = this.getClass().getClassLoader().getResourceAsStream("statecode.properties");
			Properties p = new Properties();
			p.load(in);
			String ret = p.getProperty(name, "");
			in.close();
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
			return "";
		}
	}
}
