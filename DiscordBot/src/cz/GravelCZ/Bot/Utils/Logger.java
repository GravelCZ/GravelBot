package cz.GravelCZ.Bot.Utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

	private static boolean debug = true;
	private static boolean r34out = false;

	private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSSS");
	private static SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss");
	
	public static void log(String string) {
		System.out.println(getDate() + " - [INFO] - " + string);
	}

	public static void log(Object o) {
		System.out.println(getDate() + " - [INFO] - " + o);
	}
	
	public static void debug(String string) {
		if (debug) {
			System.out.println(getDate() + " - [DEBUG] - " + string);	
		}
	}

	public static void error(String string) {
		System.out.println(getDate() + " - [ERROR] - " + string);
	}

	public static void rule34log(String string) {
		if (r34out) {
			System.out.println(getDate() + " - [RULE34] - " + string);
		}
	}

	public static String getDate() {
		return sdf.format(new Date(System.currentTimeMillis()));
	}
	
	public static void hook() throws FileNotFoundException {
		File logs = new File("./BotDataFolder/logs/");
		logs.mkdirs();
		
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("./BotDataFolder/logs/log-" + getLogDate() + ".log"));
		
		PrintStream newOut = new LoggerPrintStream(bos, System.out);
		PrintStream newErr = new LoggerPrintStream(bos, System.err);
		
		System.setOut(newOut);
		System.setErr(newErr);
	}

	private static String getLogDate() {
		return sdf2.format(new Date(System.currentTimeMillis()));
	}

	public static class LoggerPrintStream extends PrintStream {

		private PrintStream oldOutput;
		
		public LoggerPrintStream(BufferedOutputStream bufferedOutputStream, PrintStream oldOut) {
			super(bufferedOutputStream, true);
			this.oldOutput = oldOut;
		}
		
		@Override
		public void println(String x) {
			oldOutput.println(x);
			super.println(x);
		}
		
		@Override
		public void println(Object x) {
			oldOutput.println(x);
			super.println(x);
		}
	}
	
}
