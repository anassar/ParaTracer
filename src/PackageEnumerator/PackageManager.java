package PackageEnumerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class PackageManager {

	private static class TrieNode {
		private final String name;
		private HashMap< String, TrieNode > children;

		public TrieNode( String name ) {
			this.name     = name;
			this.children = new HashMap< String, TrieNode >();
		}

		public void addClass( String classLongName ) {
			String[] names = classLongName.split( "\\." );
			assert( names[0].equals( name ) );
			classLongName = classLongName.replaceFirst( name, "" );

			if ( names.length < 2 ) return;
			assert( classLongName.startsWith( "." ) );
			classLongName = classLongName.replaceFirst( "\\.", "" );

			String childName = names[1];
			if ( ! children.containsKey( childName ) ) {
				children.put( childName, new TrieNode( childName ) );
			}
			children.get( childName ).addClass( classLongName );
		}

		public void println( PrintWriter out, String indentation ) {
			out.println( indentation + name );
			ArrayList<String> list = new ArrayList<String>( children.keySet() );
			Collections.sort(list);
			for( String childName : list ) {
				TrieNode child = children.get(childName);
				child.println( out, indentation + "\t" );
			}
		}
	}
	
	private static final String dir =
			"E:\\Dropbox\\SIMM\\MiningSpecs\\ParaTracer\\DaCapo Class Lists";
	private static final String outFile =
			"E:\\Dropbox\\SIMM\\MiningSpecs\\ParaTracer\\DaCapo Class Lists\\classList.out";

	public static void main(String[] args) {
		HashSet<String> classes = new HashSet<String>();

		Package[] packages = Package.getPackages();
		for( Package pkg : packages ) {
			System.err.println( pkg.toString() );
		}

		File[] files = new File( dir ).listFiles();
		for ( File file : files ) {
			if ( ! file.isFile() ) continue;
			BufferedReader br;
			try {
				br = new BufferedReader( new FileReader( file ) );
			    String line;
				while ( (line = br.readLine()) != null ) {
					classes.add( line );
			    }
			    br.close();
			} catch ( Exception ex ) {
				System.err.println( ex.toString() );
			}
		}
		TrieNode javaNode  = new TrieNode( "java"  );
		TrieNode javaxNode = new TrieNode( "javax" );
		for( String classLongName : classes ) {
			if ( classLongName.startsWith( "java." ) ) {
				javaNode.addClass( classLongName );
			} else if ( classLongName.startsWith( "javax." ) ) {
				javaxNode.addClass( classLongName );
			}
		}
		FileWriter   fw;
		PrintWriter  out;
		try {
			fw  = new FileWriter( outFile );
			out = new PrintWriter( fw );
			javaNode .println( out, "" );
			javaxNode.println( out, "" );
			fw.close();
		} catch (IOException e) {
			System.err.println( e.toString() );
		}
	}

}
