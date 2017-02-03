package Logging;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Stack;

public class Logger {

	private static String loggerDir = "E:\\Dropbox\\SIMM\\MiningSpecs\\ParaTracer";
	private static FileWriter   fw;
	private static PrintWriter  out;
	private static HashMap< Long, Stack<String> > callStacks;
	private static String   loggerFileSuffix = "";
	private static String[] packages = null;

	public static synchronized void pushArgs( long tid, String args ) {
		init();
		if ( ! callStacks.containsKey( tid ) ) {
			callStacks.put( tid, new Stack<String>() );
		}
		callStacks.get( tid ).push( args );
	}

	public static synchronized String popArgs( long tid ) {
		assert( callStacks.containsKey( tid ) );
		assert(!callStacks.get( tid ).empty() );
		return( callStacks.get( tid ).pop  () );
	}

	public static synchronized void shutdown() {
		if ( fw == null ) return;
		try {
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static synchronized void print( String str ) {
		init();
		out.print( str );
	}

	public static synchronized void println( String str ) {
		init();
		out.println( str );
	}

	public static void setPackages( String[] pkgs ) {
		packages = pkgs;
	}

	public static void setFileSuffix( String fileSuffix ) {
		loggerFileSuffix = fileSuffix;
	}

	private static void init() {
		if ( fw != null ) return;
		String loggerFilePath = loggerDir + "\\paratracer" + loggerFileSuffix + ".out";
		try {
			fw  = new FileWriter( loggerFilePath );
			out = new PrintWriter( fw );
			if ( packages != null ) {
				out.println( "=======================================================" );
				out.println( "       M O N I T O R E D      P A C K A G E S          " );
				out.println( "=======================================================" );
				for( String pkg : packages ) {
					if (pkg==null) continue;
					out.println( pkg );
				}
				out.println( "=======================================================" );
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		callStacks = new HashMap< Long, Stack<String> >();
//		final Thread mainThread = Thread.currentThread();
		Runtime.getRuntime().addShutdownHook( new Thread() {
		    public void run() {
		    	shutdown();
//		        mainThread.join();
		    }
		});
	}

}
