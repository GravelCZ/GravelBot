package cz.GravelCZLP.Bot.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang3.tuple.Pair;

public class Utils {

	private static int questions = 0;

	public static int getLoadedQuestions() {
		return questions;
	}

	public static void setLoadedQuestions(int q) {
		questions = q;
	}

	public static Random getRandom() {
		return new Random();
	}

	public static String getRandomString(int len) {
		char[] chars = "qwertzuiopasdfghjklmnbvcxy1023456789".toCharArray();
		StringBuffer newString = new StringBuffer();
		for (int i = 0; i < len; i++) {
			newString.append(chars[getRandom().nextInt(chars.length)]);
		}
		return newString.toString();
	}
	
	public static String loadFile(File f) throws IOException {
		if (f == null || !f.exists()) {
			return "";
		}
		BufferedReader br = new BufferedReader(new FileReader(f));
		StringBuffer buffer = new StringBuffer();
		String line;
		while ((line = br.readLine()) != null) {
			buffer.append(line + "\n");
		}
		br.close();
		return buffer.toString();
	}

	public static String[] getLinesFromFile(File f) throws IOException {
		if (f == null || !f.exists()) {
			return null;
		}
		List<String> list = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(f));
		String line;
		while ((line = br.readLine()) != null) {
			list.add(line);
		}
		br.close();
		String[] array = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			array[i] = list.get(i);
		}
		return array;
	}

	public static void appendToFile(File file, String line) {
		try {
			FileWriter fw = new FileWriter(file, true);
			fw.write(line + "\n");
			fw.flush();
			fw.close();	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void writeToFile(File f, String file) {
		try {
			BufferedWriter b = new BufferedWriter(new FileWriter(f));
			b.write(file);
			b.flush();
			b.close();	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Pair<String, Integer> makeUrlPostRequest(URL url, HashMap<String, String> headers, String data) {
		try {
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

			conn.setRequestMethod("POST");
			if (data != null) {
				conn.setDoOutput(true);
			}

			headers.put("User-Agent", getUserAgent());

			for (Entry<String, String> e : headers.entrySet()) {
				conn.setRequestProperty(e.getKey(), e.getValue());
			}

			Logger.debug("Method: POST to url: " + url.toString());

			if (data != null) {
				conn.getOutputStream().write(data.getBytes());
			}

			if (conn.getResponseCode() == 404) {
				Logger.error("URL: " + url.toString() + " not found(404)");
				return Pair.of(null, 404);
			}

			InputStream is = null;
			
			try {
				is = conn.getInputStream();
			} catch (Exception e) {
				is = conn.getErrorStream();
			}
			
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer buffer = new StringBuffer();
			while ((line = br.readLine()) != null) {
				buffer.append(line);
			}

			return Pair.of(buffer.toString(), conn.getResponseCode());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static HttpsURLConnection rawConnection(String method, URL url, HashMap<String, String> headers) {
		try {
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setRequestMethod(method);

			headers.put("User-Agent", getUserAgent());

			for (Entry<String, String> e : headers.entrySet()) {
				conn.addRequestProperty(e.getKey(), e.getValue());
			}

			conn.connect();

			return conn;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Pair<String, Integer> makeUrlGetRequest(URL url, HashMap<String, String> headers, boolean appendNewLine) {
		try {
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setRequestMethod("GET");

			headers.put("User-Agent", getUserAgent());

			for (Entry<String, String> e : headers.entrySet()) {
				conn.addRequestProperty(e.getKey(), e.getValue());
			}

			conn.connect();

			if (conn.getResponseCode() == 404) {
				Logger.error("URL: " + url.toString() + " not found(404)");
				return null;
			}

			Logger.debug("Method: GET to url: " + url.toString());

			InputStream is = null;
			
			try {
				is = conn.getInputStream();
			} catch (Exception e) {
				is = conn.getErrorStream();
			}
			
			StringBuffer sb = new StringBuffer();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			String line;

			while ((line = br.readLine()) != null) {
				if (appendNewLine) {
					sb.append(line + "\n");
				} else {
					sb.append(line);
				}
			}

			return Pair.of(sb.toString(), conn.getResponseCode());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Pair<InputStream, Integer> getInputStreamFromURL(URL url, HashMap<String, String> headers) {
		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");

			headers.put("User-Agent", getUserAgent());

			for (Entry<String, String> e : headers.entrySet()) {
				conn.addRequestProperty(e.getKey(), e.getValue());
			}

			Logger.debug("Opening connection to: " + url.toString());

			conn.connect();

			if (conn.getResponseCode() == 404) {
				Logger.error("URL: " + url.toString() + " not found(404)");
				return Pair.of(null, 404);
			}

			return Pair.of(conn.getInputStream(), conn.getResponseCode());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int getResponseCode(URL url, HashMap<String, String> headers) {
		try {
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

			conn.setRequestMethod("GET");

			conn.setRequestProperty("User-Agent", getUserAgent());

			for (Entry<String, String> e : headers.entrySet()) {
				conn.addRequestProperty(e.getKey(), e.getValue());
			}

			conn.connect();

			int code = conn.getResponseCode();

			Logger.debug("GET to: " + url.toString() + "; Response code: " + code);

			return code;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}


	public static String readInputStream(InputStream inputStream, boolean newLine) {
		try {
			StringBuffer sb = new StringBuffer();
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

			String line;

			while ((line = br.readLine()) != null) {
				if (newLine) {
					sb.append(line + "\n");
				} else {
					sb.append(line);
				}
			}
			
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Pair<byte[], Integer> downloadFile(URL url, HashMap<String, String> headers) {
		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");

			conn.setRequestProperty("User-Agent", getUserAgent());

			for (Entry<String, String> e : headers.entrySet()) {
				conn.addRequestProperty(e.getKey(), e.getValue());
			}
			
			Logger.log("GET to: " + url.toString());
			
			if (conn.getResponseCode() == 404) {
				Logger.error("URL: " + url.toString() + " not found(404)");
				return Pair.of(new byte[0], 404);
			}
			
			InputStream is = new BufferedInputStream(conn.getInputStream());

			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			byte[] buf = new byte[1024];
			int n = 0;
			while ((n = is.read(buf)) != -1) {
				bos.write(buf, 0, n);
			}

			bos.close();
			is.close();

			return Pair.of(bos.toByteArray(), conn.getResponseCode());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String getUserAgent() {
		return "GravelBot (v" + getVersion() + "; Linux x86_64)";
	}

	public static byte[] sha256(byte[] data) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			return digest.digest(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * NON CRYPTO HASH !!!
	 * @param in - string in
	 * @return a number
	 */
	public static int gravelHash(String s) {
		int sum = 0;
		for (int i = 0; i < s.length(); i++) {
			sum += s.charAt(i) << (5 * i);
		}
		return Math.abs(sum);
	}
	
	public static String toB64(byte[] data) {
		return Base64.getUrlEncoder().encodeToString(data);
	}
	
	public static boolean isImage(String s) {
		if (s.endsWith(".png") || s.endsWith(".jpg") || s.endsWith(".jpeg")) {
			return true;
		}
		return false;
	}

	public static boolean isGif(String s) {
		return s.endsWith(".gif");
	}

	public static String getVersion() {
		return "1.2.3-BETA";
	}

}
