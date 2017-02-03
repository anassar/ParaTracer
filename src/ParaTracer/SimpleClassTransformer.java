package ParaTracer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import Logging.Logger;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;


/**
 * Once a transformer has been registered with <code>addTransformer</code>,
 * the transformer will be called for every new class definition and every
 * class redefinition.
 * The transformer is called during the processing of the request, before
 * the class file bytes have been verified or applied.
 * When there are multiple transformers, transformations are composed by
 * chaining the transform calls. That is, the byte array returned by one
 * call to transform becomes the input (via the <code>classfileBuffer</code> parameter)
 * to the next call.
 * Transformers are called in the order registered.
 * 
 * @author Ahmed Nassar
 *
 */



public class SimpleClassTransformer implements ClassFileTransformer {

	private final boolean  onlyListClasses;
	private final boolean  onlyStdClasses;
	private final String[] packages;

	public SimpleClassTransformer( boolean onlyListClasses,
			boolean onlyStdClasses, String[] packages ) {
		this.onlyListClasses = onlyListClasses;
		this.onlyStdClasses  = onlyStdClasses;
		this.packages        = packages;
	}

	/**
	 * <p>If the implementing method determines that no transformations are needed,
	 * it should return <code>null</code>. Otherwise, it should create a new
	 * <code>byte[]</code> array, copy the input <code>classfileBuffer</code>
	 * into it, along with all desired transformations, and return the new array.
	 * The input <code>classfileBuffer</code> must not be modified.
	 * Throwing an exception has the same effect as returning <code>null</code>.</p>
	 * 
	 * By the time when the agent is invoked, the class is not loaded. It is only
	 * during the class loading time, the agent is invoked. Hence reflection related
	 * APIs cannot be used at this point. For querying the method information for
	 * a particular class, the implementation makes use of ASM (bytecode analysis library).
	 */
	@Override
	public byte[] transform(
			ClassLoader       loader,              // The defining loader of the class to be transformed, may be null if the bootstrap loader
			String            className,           // The name of the class in the internal form of fully qualified class and interface names. For example, "java/util/List".
			Class<?>          classBeingRedefined, // If this is a class load, null.
			ProtectionDomain  protectionDomain,
			byte[]            classfileBuffer)     // A sequence of bytes in class file format
					throws IllegalClassFormatException {

		String normalizedClassName = className.replaceAll("/", ".");

		boolean stdClass =
				normalizedClassName.startsWith( "java."  ) ||
				normalizedClassName.startsWith( "javax." );
		if ( onlyListClasses && ( stdClass || !onlyStdClasses ) ) {
			/**
			// Use standard out because System.err is the stream used to log method calls.
			System.out.println( "---- Instrumenting: " + className );
			Logger.println( "---- Instrumenting: " + className );
			System.out.println( "\tNormalized: " + normalizedClassName );
			 */
			Logger.println( normalizedClassName );
			return null; // No modifications.
		}

		// Do not instrument unlisted packages.
		if ( packages != null ) {
			boolean found = false;
			for( String pkg : packages ) {
				if ( pkg != null ) {
					if ( normalizedClassName.startsWith( pkg ) ) {
						found = true;
						break;
					}
				}
			}
			if ( !found ) return null;
		}
		// To manipulate raw byte code, rely on dedicated tools
		// such as ASM or Javassist.
		byte[] byteCode = classfileBuffer;

		ClassMonitorSet classMonitorSet =
				ClassMonitorSet.isMonitoredClass( normalizedClassName );
		if ( classMonitorSet != null ) {
//			System.out.println( "----- Monitoring: " +
//				normalizedClassName );
//			Logger.println( "----- Monitoring: " +
//				normalizedClassName );
			try {
				// The ClassPool object reads a class file on demand for
				// constructing a CtClass object and records the
				// constructed object for responding to later accesses.
				// The ClassPool object returned by getDefault() searches
				// the default system search path.
				ClassPool cp = ClassPool.getDefault();
//         		cp.insertClassPath("/usr/local/javalib");
//				cp.importPackage( "Logging.Logger");

				// Compile-time class - an abstract representation of
				// a class file.
				CtClass cc = cp.get( normalizedClassName );

//				CtMethod[]  methods  = cc.getDeclaredMethods(); // Inherited methods are not included.
				CtMethod[]  methods  = cc.getMethods();
				for( CtMethod method : methods ) {
//					Logger.println( "\t~~~~ Checking Method: " + method.getLongName() );
					if ( ! classMonitorSet.isMonitoredMethod( method ) )
						continue;
//					System.out.println( "\t~~~~ Monitoring Method: " + method.getLongName() );
//					Logger.println( "\t~~~~ Monitoring Method: " + method.getLongName() );

					StringBuilder sbs = new StringBuilder();
					sbs.append( "long tid = Thread.currentThread().getId();" );
					sbs.append( "StringBuilder sbArgs = new StringBuilder();" );
					sbs.append( "sbArgs.append( System.identityHashCode( $0 ) );" ); // The "this" reference.
					CtClass[] pTypes = method.getParameterTypes();
					for( int i=0; i < pTypes.length; ++i ) {
						CtClass pType = pTypes[i];
						if ( pType.isPrimitive() ) {
							sbs.append( "sbArgs.append( \", \" + $args[" + i + "] );" );
						} else {
							sbs.append( "sbArgs.append( \", \" + System.identityHashCode( $args[" + i + "] ) );" );
						}
					}
					sbs.append( "Logging.Logger.pushArgs( tid, sbArgs.toString() );" );
					sbs.append( "StringBuilder sb = new StringBuilder();" );
					sbs.append( "sb.append( tid + \" : " + method.getLongName() + ".<START>(\" );" );
					sbs.append( "sb.append( sbArgs.toString() );" );
					sbs.append( "sb.append( \")\" );" );
					sbs.append( "Logging.Logger.println( sb.toString() );" );
//					sbs.append( "System.err.println( sb.toString() );" );
//					sbs.append( "String fPath = \"E:\\\\Dropbox\\\\SIMM\\\\MiningSpecs\\\\ParaTracer\\\\paratracer.out\";" );
//					sbs.append( "try {" );
//					sbs.append( "	java.io.FileWriter  fw  = new java.io.FileWriter( fPath, true );" );
//					sbs.append( "	java.io.PrintWriter out = new java.io.PrintWriter( fw, true );" );
//					sbs.append( "	out.println( sb.toString() );" );
//					sbs.append( "	fw.close();" );
//					sbs.append( "} catch (java.io.IOException e) {" );
//					sbs.append( "	e.printStackTrace();" );
//					sbs.append( "}" );

					// You need to catch CannotCompileExceptions here because
					// some methods might have no body which throws that exception.
					// If not caught here, the rest of the class will be bypassed
					// without instrumentation.
					try {
						method.insertBefore("{" + sbs.toString() + "}");
					} catch ( Exception ex ) {
						System.err.println( ex.toString() );
					}
					// The bytecode is inserted just before every return insturction.
					// It is not executed when an exception is thrown.
					StringBuilder sbe = new StringBuilder();
					sbe.append( "long tid = Thread.currentThread().getId();" );
					sbe.append( "String args = Logging.Logger.popArgs( tid );" );
					sbe.append( "StringBuilder sb = new StringBuilder();" );
//					sbe.append( "sb.append( tid + \" : " + method.getLongName() + ".< END >(*)\" );" );
					sbe.append( "sb.append( tid + \" : " + method.getLongName() + ".< END >(\" );" );
					sbe.append( "sb.append( args );" );
					sbe.append( "sb.append( \")\" );" );

					CtClass rType = method.getReturnType();
					if ( rType.equals( CtClass.voidType ) ) {
						sbe.append( "sb.append( \"=VOID\" );" );
					} if ( rType.isPrimitive() ) {
						sbe.append( "sb.append( \"=\" + $_ );" );
					} else {
						sbe.append( "sb.append( \"=\" + System.identityHashCode( $_ ) );" );
					}

					sbe.append( "Logging.Logger.println( sb.toString() );" );
//					sbe.append( "System.err.println( sb.toString() );" );
//					sbe.append( "String fPath = \"E:\\\\Dropbox\\\\SIMM\\\\MiningSpecs\\\\ParaTracer\\\\paratracer.out\";" );
//					sbe.append( "try {" );
//					sbe.append( "	java.io.FileWriter  fw  = new java.io.FileWriter( fPath, true );" );
//					sbe.append( "	java.io.PrintWriter out = new java.io.PrintWriter( fw, true );" );
//					sbe.append( "	out.println( sb.toString() );" );
//					sbe.append( "	fw.close();" );
//					sbe.append( "} catch (java.io.IOException e) {" );
//					sbe.append( "	e.printStackTrace();" );
//					sbe.append( "}" );

					/** You need to catch CannotCompileExceptions here
					 * because some methods might have no body which
					 * throws that exception. If not caught here, the
					 * rest of the class will be bypassed
					 * without instrumentation.
					 */
					try {
						/**
						 * Although the compiled code inserted by
						 * insertAfter() is executed just before the
						 * control normally returns from the method,
						 * it can be also executed when an exception
						 * is thrown from the method. To execute it
						 * when an exception is thrown, the second
						 * parameter asFinally to insertAfter() must
						 * be true.
						 * If an exception is thrown, the compiled code
						 * inserted by insertAfter() is executed as
						 * a finally clause. The value of $_ is 0 or null
						 * in the compiled code. After the execution of
						 * the compiled code terminates, the exception
						 * originally thrown is re-thrown to the caller.
						 * Note that the value of $_ is never thrown to
						 * the caller; it is rather discarded.
						 * 
						 * THIS IS IMPORTANT, since we store argument
						 * values on the stack. An exception unwinds the
						 * main program stack, but we need also to unwind
						 * the instrumentation stack.
						 */
						method.insertAfter(
								"{" + sbe.toString() + "}", true );
					} catch ( Exception ex ) {
						System.err.println( ex.toString() );
					}
					/**
					 * addCatch() inserts a code fragment into a method
					 * body so that the code fragment is executed when
					 * the method body throws an exception and the
					 * control returns to the caller. In the source
					 * text representing the inserted code fragment,
					 * the exception value is referred to with the
					 * special variable $e.
					 */
					CtClass eType =
							cp.get("java.lang.Exception");
					method.addCatch(
							"{ Logging.Logger.println("
							+ "\"*** Exception: \" + $e.getClass().getName());"
							+ " throw $e; }",
							eType );
					eType = cp.get("java.lang.Error");
					method.addCatch(
							"{ Logging.Logger.println("
							+ "\"*** Error: \" + $e.getClass().getName());"
							+ " throw $e; }",
							eType );
				}

				/**
				for( String method : mClassMonitorSets.get( normalizedClassName ) ) {
					CtMethod  m  = cc.getDeclaredMethod( method );

					StringBuilder sbs = new StringBuilder();
					sbs.append( "long tid = Thread.currentThread().getId();" );
					sbs.append( "StringBuilder sbArgs = new StringBuilder();" );
					sbs.append( "sbArgs.append( System.identityHashCode( $0 ) );" );
					CtClass[] pTypes = m.getParameterTypes();
					for( int i=0; i < pTypes.length; ++i ) {
						CtClass pType = pTypes[i];
						if ( pType.isPrimitive() ) {
							sbs.append( "sbArgs.append( \", \" + $args[" + i + "] );" );
						} else {
							sbs.append( "sbArgs.append( \", \" + System.identityHashCode( $args[" + i + "] ) );" );
						}
					}
					sbs.append( "Class logger = Class.forName( \"Logging.Logger\" );" );
					sbs.append( "logger.getMethod( \"pushArgs\", Long.class, String.class ).invoke( tid, sbArgs.toString() );" );
					sbs.append( "StringBuilder sb = new StringBuilder();" );
					sbs.append( "sb.append( tid + \" : " + m.getLongName() + ".<START>(\" );" );
					sbs.append( "sb.append( sbArgs.toString() );" );
					sbs.append( "sb.append( \")\" );" );
					sbs.append( "logger.getMethod( \"println\", String.class ).invoke( sb.toString() );" );

					m.insertBefore("{" + sbs.toString() + "}");
					// The bytecode is inserted just before every return insturction.
					// It is not executed when an exception is thrown.
					StringBuilder sbe = new StringBuilder();
					sbe.append( "long tid = Thread.currentThread().getId();" );
					sbs.append( "Class logger = Class.forName( \"Logging.Logger\" );" );
					sbe.append( "String args = (String)logger.getMethod( \"popArgs\", Long.class ).invoke( tid );" );
					sbe.append( "StringBuilder sb = new StringBuilder();" );
					sbe.append( "sb.append( tid + \" : " + m.getLongName() + ".<END>(\" );" );
					sbe.append( "sb.append( args );" );
					sbe.append( "sb.append( \")\" );" );
					sbe.append( "logger.getMethod( \"println\", String.class ).invoke( sb.toString() );" );

					m.insertAfter("{" + sbe.toString() + "}");
				}
				*/
				byteCode = cc.toBytecode(); // Obtain the modified bytecode
				cc.detach(); // CtClass object is removed from the ClassPool to save memory.
			} catch (Exception ex) {
//				ex.printStackTrace();
				System.err.println( ex.toString() );
			}
		}
        return byteCode;
	}

}
