package ParaTracer;

import java.lang.instrument.Instrumentation;

import Logging.Logger;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;


/**
 * See: <a href="https://docs.oracle.com/javase/7/docs/api/java/lang/instrument/package-summary.html">Instrument Package Summary</a>
 * <p>An agent is deployed as a JAR file. An attribute in the JAR file manifest
 * specifies the agent class which will be loaded to start the agent.
 * On implementations with a command-line interface, an agent is started by
 * adding this option to the command-line:</p>
 * 
 * <code>-javaagent:jarpath[=options]</code>
 * 
 * <p><i>jarpath</i> is the path to the agent JAR file. <i>options</i> is the agent options.
 * This switch may be used multiple times on the same command-line, thus
 * creating multiple agents. More than one agent may use the same jarpath.</p>
 * 
 * <p>The manifest of the agent JAR file must contain the attribute Premain-Class.
 * The value of this attribute is the name of the agent class.</p>
 * 
 * Note that according to this 
 * <a href="http://stackoverflow.com/questions/29451704/using-javassist-to-log-method-calls-and-argument-values-how-to-make-a-logger-cl">StackOverflow answer</a>,
 * we need to make the {@code Logging.Logger} class available to the
 * bootstrap classloader. That is why we move the {@code Logger} class
 * into a separate JAR file, and list that file in the {@code Boot-Class-Path}
 * attribute of the {@code ParaTracer} agent JAR MANIFEST.MF file:
 * <p>{@code Boot-Class-Path: ParaTracerLogger.jar  libs/javassist/javassist.jar}</p>
 * This way, the {@code Logger} class is visible to the bootstrap loader
 * and can be seen by all the instrumented classes.
 * 
 * <p> Note that we also add this line to the {@code ParaTracer} agent
 * JAR MANIFEST.MF file:</p>
 * <p>{@code Premain-Class: ParaTracer.ParaTracer}</p>
 * 
 ***************************************************************************************
 * <p><b>Running DaCapo Benchmarks:</b></p>
 * <p>{@code cd E:\Dropbox\SIMM\MiningSpecs\ParaTracer\DaCapo Benchmark Suite}</p>
 * <p>{@code java -javaagent:E:\Dropbox\SIMM\MiningSpecs\ParaTracer\ParaTracerAgent.jar=OnlyListClasses,OnlyStdClasses,ClassListXalan      -jar dacapo-9.12-bach.jar xalan}</p>
 * <p>{@code java -javaagent:E:\Dropbox\SIMM\MiningSpecs\ParaTracer\ParaTracerAgent.jar=OnlyListClasses,OnlyStdClasses,ClassListAvrora     -jar dacapo-9.12-bach.jar avrora}</p>
 * <p>{@code java -javaagent:E:\Dropbox\SIMM\MiningSpecs\ParaTracer\ParaTracerAgent.jar=OnlyListClasses,OnlyStdClasses,ClassListBatik      -jar dacapo-9.12-bach.jar batik}</p>
 * <p>{@code java -javaagent:E:\Dropbox\SIMM\MiningSpecs\ParaTracer\ParaTracerAgent.jar=OnlyListClasses,OnlyStdClasses,ClassListEclipse    -jar dacapo-9.12-bach.jar eclipse}</p>
 * <p>{@code java -javaagent:E:\Dropbox\SIMM\MiningSpecs\ParaTracer\ParaTracerAgent.jar=OnlyListClasses,OnlyStdClasses,ClassListFop        -jar dacapo-9.12-bach.jar fop}</p>
 * <p>{@code java -javaagent:E:\Dropbox\SIMM\MiningSpecs\ParaTracer\ParaTracerAgent.jar=OnlyListClasses,OnlyStdClasses,ClassListH2         -jar dacapo-9.12-bach.jar h2}</p>
 * <p>{@code java -javaagent:E:\Dropbox\SIMM\MiningSpecs\ParaTracer\ParaTracerAgent.jar=OnlyListClasses,OnlyStdClasses,ClassListJython     -jar dacapo-9.12-bach.jar jython}</p>
 * <p>{@code java -javaagent:E:\Dropbox\SIMM\MiningSpecs\ParaTracer\ParaTracerAgent.jar=OnlyListClasses,OnlyStdClasses,ClassListLuIndex    -jar dacapo-9.12-bach.jar luindex}</p>
 * <p>{@code java -javaagent:E:\Dropbox\SIMM\MiningSpecs\ParaTracer\ParaTracerAgent.jar=OnlyListClasses,OnlyStdClasses,ClassListLuSearch   -jar dacapo-9.12-bach.jar lusearch}</p>
 * <p>{@code java -javaagent:E:\Dropbox\SIMM\MiningSpecs\ParaTracer\ParaTracerAgent.jar=OnlyListClasses,OnlyStdClasses,ClassListPmd        -jar dacapo-9.12-bach.jar pmd}</p>
 * <p>{@code java -javaagent:E:\Dropbox\SIMM\MiningSpecs\ParaTracer\ParaTracerAgent.jar=OnlyListClasses,OnlyStdClasses,ClassListSunflow    -jar dacapo-9.12-bach.jar sunflow}</p>
 * <p>{@code java -javaagent:E:\Dropbox\SIMM\MiningSpecs\ParaTracer\ParaTracerAgent.jar=OnlyListClasses,OnlyStdClasses,ClassListTomcat     -jar dacapo-9.12-bach.jar tomcat}</p>
 * <p>{@code java -javaagent:E:\Dropbox\SIMM\MiningSpecs\ParaTracer\ParaTracerAgent.jar=OnlyListClasses,OnlyStdClasses,ClassListTradeBeans -jar dacapo-9.12-bach.jar tradebeans}</p>
 * <p>{@code java -javaagent:E:\Dropbox\SIMM\MiningSpecs\ParaTracer\ParaTracerAgent.jar=OnlyListClasses,OnlyStdClasses,ClassListTradeSoap  -jar dacapo-9.12-bach.jar tradesoap}</p>
 ****************************************************************************************
 * <p>{@code java -javaagent:E:\Dropbox\SIMM\MiningSpecs\ParaTracer\ParaTracerAgent.jar=EventTraceAllClassesXalan      -jar dacapo-9.12-bach.jar xalan}</p>
 * <p>{@code java -javaagent:E:\Dropbox\SIMM\MiningSpecs\ParaTracer\ParaTracerAgent.jar=EventTraceAllClassesAvrora     -jar dacapo-9.12-bach.jar avrora}</p>
 * <p>{@code java -javaagent:E:\Dropbox\SIMM\MiningSpecs\ParaTracer\ParaTracerAgent.jar=EventTraceAllClassesBatik      -jar dacapo-9.12-bach.jar batik}</p>
 * <p>{@code java -javaagent:E:\Dropbox\SIMM\MiningSpecs\ParaTracer\ParaTracerAgent.jar=EventTraceAllClassesEclipse    -jar dacapo-9.12-bach.jar eclipse}</p>
 * <p>{@code java -javaagent:E:\Dropbox\SIMM\MiningSpecs\ParaTracer\ParaTracerAgent.jar=EventTraceAllClassesFop        -jar dacapo-9.12-bach.jar fop}</p>
 * <p>{@code java -javaagent:E:\Dropbox\SIMM\MiningSpecs\ParaTracer\ParaTracerAgent.jar=EventTraceAllClassesH2         -jar dacapo-9.12-bach.jar h2}</p>
 * <p>{@code java -javaagent:E:\Dropbox\SIMM\MiningSpecs\ParaTracer\ParaTracerAgent.jar=EventTraceAllClassesJython     -jar dacapo-9.12-bach.jar jython}</p>
 * <p>{@code java -javaagent:E:\Dropbox\SIMM\MiningSpecs\ParaTracer\ParaTracerAgent.jar=EventTraceAllClassesLuIndex    -jar dacapo-9.12-bach.jar luindex}</p>
 * <p>{@code java -javaagent:E:\Dropbox\SIMM\MiningSpecs\ParaTracer\ParaTracerAgent.jar=EventTraceAllClassesLuSearch   -jar dacapo-9.12-bach.jar lusearch}</p>
 * <p>{@code java -javaagent:E:\Dropbox\SIMM\MiningSpecs\ParaTracer\ParaTracerAgent.jar=EventTraceAllClassesPmd        -jar dacapo-9.12-bach.jar pmd}</p>
 * <p>{@code java -javaagent:E:\Dropbox\SIMM\MiningSpecs\ParaTracer\ParaTracerAgent.jar=EventTraceAllClassesSunflow    -jar dacapo-9.12-bach.jar sunflow}</p>
 * <p>{@code java -javaagent:E:\Dropbox\SIMM\MiningSpecs\ParaTracer\ParaTracerAgent.jar=EventTraceAllClassesTomcat     -jar dacapo-9.12-bach.jar tomcat}</p>
 * <p>{@code java -javaagent:E:\Dropbox\SIMM\MiningSpecs\ParaTracer\ParaTracerAgent.jar=EventTraceAllClassesTradeBeans -jar dacapo-9.12-bach.jar tradebeans}</p>
 * <p>{@code java -javaagent:E:\Dropbox\SIMM\MiningSpecs\ParaTracer\ParaTracerAgent.jar=EventTraceAllClassesTradeSoap  -jar dacapo-9.12-bach.jar tradesoap}</p>
 * 
 ****************************************************************************************
 * <p>{@code java -javaagent:E:\Dropbox\SIMM\MiningSpecs\ParaTracer\ParaTracerAgent.jar=EventTraceXalan,java.util.HashMap,java.util.LinkedHashMap,java.util.IdentityHashMap -jar dacapo-9.12-bach.jar xalan}</p>
 * <p>{@code java -javaagent:E:\Dropbox\SIMM\MiningSpecs\ParaTracer\ParaTracerAgent.jar=EventTraceTradeSoap,java.net  -jar dacapo-9.12-bach.jar tradesoap}</p>
 ****************************************************************************************
 * 
 * @author Ahmed Nassar
 *
 */


public class ParaTracer {

	private static volatile Instrumentation instr;
	private static boolean  onlyListClasses = false;
	private static boolean  onlyStdClasses  = false;
	private static String[] packages        = null;
	private static String   fileSuffix      = null;

	/**
	 * After the Java Virtual Machine (JVM) has initialized, each premain
	* method will be called in the order the agents were specified, then
	* the real application main method will be called. Each premain method
	* must return in order for the startup sequence to proceed.
	* There are no modeling restrictions on what the agent premain method
	* may do. Anything application main can do, including creating threads,
	* is legal from premain.
	*/
	public static void premain(String agentArgs, Instrumentation inst) {
		instr = inst;
		String[] args = agentArgs.split( "," );
		for( String arg : args ) {
			if ( "OnlyListClasses".equalsIgnoreCase( arg ) ) {
				onlyListClasses = true;
			} else if ( "OnlyStdClasses".equalsIgnoreCase( arg ) ) {
				onlyStdClasses = true;
			} else if ( arg.startsWith( "java." ) ) {
				addPackage( arg );
			} else if ( arg.startsWith( "javax." ) ) {
				addPackage( arg );
			} else if ( fileSuffix == null ) {
				fileSuffix = arg;
				Logger.setFileSuffix( fileSuffix );
			} else {
				System.err.println( "*** An unrecognized ParaTracer option: " + arg );
				System.err.println( "\tfileSuffix is " + ( ( fileSuffix == null )? "null" :  fileSuffix ) );
				System.exit( 1 );
			}
		}
		if ( packages != null ) {
			Logger.setPackages( packages );
		}
//		try {
//			createLoggerClass();
//		} catch( CannotCompileException ex ) {
//			ex.printStackTrace( System.err );
//		}

		// Now, all future class definitions will be seen by the transformer,
		// except definitions of classes upon which any registered transformer
		// is dependent. The transformer is called when classes are loaded, when
		// they are redefined and optionally, when they are retransformed
		// (if the transformer was added to the instrumentation instance with
		// the boolean canRetransform set to true).
		SimpleClassTransformer transformer =
				new SimpleClassTransformer( onlyListClasses, onlyStdClasses, packages );
		inst.addTransformer( transformer, false );
	}

	private static void addPackage( String pkg ) {
		if ( packages == null ) {
			packages = new String[4];
		}
		// Look for an empty slot.
		for(int i=0; i < packages.length; ++i) {
			if ( packages[i] != null ) continue;
			packages[i] = pkg;
			return;
		}
		// Re-allocate a new array.
		int N  = packages.length;
		int NN = 2 * N;
		String[] npackages = new String[NN];
		for(int i=0; i < N; ++i) {
			npackages[i] = packages[i];
		}
		npackages[N] = pkg;
		packages = npackages;
	}

	private static String loggerFilePath      = "\"E:\\Dropbox\\SIMM\\MiningSpecs\\ParaTracer\\paratracer.out\"";
	private static String loggerInstance      = "loggerInstance";
	private static String loggerLockerObject  = "private static final Object LOCK = new Object();";
	private static String loggerInstanceField = "private static Logging.Logger " + loggerInstance + ";";
	private static String loggerFileWriter    = "private FileWriter   fw;";
	private static String loggerPrintWriter   = "private PrintWriter  out;";
//	private static String loggerCallStacks    = "private HashMap< Long, Stack<String> > callStacks;";
	private static String loggerCallStacks    = "private HashMap                        callStacks;";
	private static String loggerGet =
	"public static Logging.Logger get() {" +
		"synchronized(LOCK) {" +
		"	if ( " + loggerInstance + " == null ) {" +
		"		loggerInstance = new Logging.Logger();" +
		"	}" +
		"}" +
		"return loggerInstance;" +
	"}";


	private static String loggerConstructor =
	"private Logger() {" +
			"fw  = new FileWriter ( " + loggerFilePath + " );" +
			"out = new PrintWriter( fw );" +
//			"callStacks = new HashMap< Long, Stack<String> >();" +
			"callStacks = new HashMap();" + // Javassist higher-level API does not support generics.
	"}";

	private static String loggerPushArgs =
	"public synchronized void pushArgs( long tid, String args ) {" +
			"if ( ! callStacks.containsKey( new Long(tid) ) ) {" +
//			"	callStacks.put( tid, new Stack<String>() );" +
			"	callStacks.put( new Long(tid), new Stack        () );" +
			"}" +
			"((Stack)callStacks.get( new Long(tid) )).push( args );" +
	"}";

	private static String loggerPopArgs =
	"public synchronized String popArgs( long tid ) {" +
//			"assert( callStacks.containsKey( new Long(tid) ) );" +
//			"assert( ! callStacks.get( new Long(tid) ).empty() );" +
			"return (String)((Stack)callStacks.get( new Long(tid) )).pop();" +
	"}";

	private static String loggerDestructor =
//	"@Override " +
	"public void finalize() {" +
		"fw.close();" +
	"}";

	private static String loggerPrint =
	"public synchronized void print( String str ) {" +
		    "out.print( str );" +
	"}";

	@SuppressWarnings("unused")
	private static void createLoggerClass()
			throws CannotCompileException {
		ClassPool cp = ClassPool.getDefault();

		cp.importPackage( "java.io.FileWriter"  );
		cp.importPackage( "java.io.IOException" );
		cp.importPackage( "java.io.PrintWriter" );
		cp.importPackage( "java.util.HashMap" );
		cp.importPackage( "java.util.Stack" );

		CtClass cc = cp.makeClass("Logging.Logger");

		/* System.err.println( loggerLockerObject  ); */ cc.addField      ( CtField         .make( loggerLockerObject,  cc ) );
		/* System.err.println( loggerFileWriter    ); */ cc.addField      ( CtField         .make( loggerFileWriter,    cc ) );
		/* System.err.println( loggerPrintWriter   ); */ cc.addField      ( CtField         .make( loggerPrintWriter,   cc ) );
		/* System.err.println( loggerInstanceField ); */ cc.addField      ( CtField         .make( loggerInstanceField, cc ) );
		/* System.err.println( loggerCallStacks    ); */ cc.addField      ( CtField         .make( loggerCallStacks,    cc ) );
		/* System.err.println( loggerConstructor   ); */ cc.addConstructor( CtNewConstructor.make( loggerConstructor,   cc ) );
		/* System.err.println( loggerDestructor    ); */ cc.addMethod     ( CtNewMethod     .make( loggerDestructor,    cc ) );
		/* System.err.println( loggerGet           ); */ cc.addMethod     ( CtNewMethod     .make( loggerGet,           cc ) );
		/* System.err.println( loggerPrint         ); */ cc.addMethod     ( CtNewMethod     .make( loggerPrint,         cc ) );
		/* System.err.println( loggerPushArgs      ); */ cc.addMethod     ( CtNewMethod     .make( loggerPushArgs,      cc ) );
		/* System.err.println( loggerPopArgs       ); */ cc.addMethod     ( CtNewMethod     .make( loggerPopArgs,       cc ) );

        // Request the context class loader for the current thread to load
		// the class represented by the CtClass.
		Class<?> c = cc.toClass();
	}

	public static long sizeInMB( Object obj ) {
		if( instr != null ) {
			return( (long)( instr.getObjectSize( obj ) / (1024*1024) ) );
		} else {
			return -1;
		}
	}
	

}



