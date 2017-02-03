package ParaTracer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class Test {

//	private static String loggerFilePath = "E:\\Dropbox\\SIMM\\MiningSpecs\\ParaTracer\\paratracer.out";

	public static void main(String[] args) {

		// Redirect standard error because this is the stream used to
		// log method calls.
////		PrintStream consoleOut = System.out;
//		PrintStream consoleErr = System.err;
//		File file = new File( loggerFilePath );
//		FileOutputStream fos;
//		PrintStream ps;
//		try {
//			fos = new FileOutputStream(file);
//			ps = new PrintStream(fos);
//		} catch (IOException e) {
//			e.printStackTrace();
//			return;
//		}
//
////		System.setOut( ps );
//		System.setErr( ps );

		HashMap<Long, Long> mMap = new HashMap<Long, Long>();
		Random rng = new Random();
		for( int i=0; i < 1000; ++i ) {
			mMap.put( rng.nextLong(), rng.nextLong() );
		}
		Iterator<Long> itr = mMap.keySet().iterator();
//		int i = 0;
		while ( itr.hasNext() ) {
			long num = itr.next();
			if ( num > 1000 ) {
				itr.remove();
			}
//			System.err.println( "i = " + i );
//			++i;
		}
////		System.setOut( consoleOut );
//		System.setErr( consoleErr );
//		try {
//			fos.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//			return;
//		}
	}

}




