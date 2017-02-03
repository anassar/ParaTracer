package ParaTracer;

import javassist.CtMethod;
import javassist.Modifier;

public class ClassMonitorSet {
	public final String    className;
	public       String[]  monitorSet;
	public final boolean   monitorPublic;
	public final boolean   monitorAll;
	public final boolean   isPrefix;

	public static ClassMonitorSet[] mClassMonitorSets;

	public ClassMonitorSet( String className, int n ) {
		this( className, false, false, false );
		this.monitorSet = new String[n];
	}

	public ClassMonitorSet( String className, boolean isPrefix,
			boolean monitorPublic, boolean monitorAll ) {
		assert( monitorPublic || monitorAll );
		this.className     = className.trim(); // Remove leading/trailing whitespace.
		this.monitorSet    = null;
		this.monitorPublic = monitorPublic;
		this.monitorAll    = monitorAll;
		this.isPrefix      = isPrefix;
	}

	public static ClassMonitorSet isMonitoredClass( String className ) {
		for(int i=0; i < mClassMonitorSets.length; ++i) {
			ClassMonitorSet set = mClassMonitorSets[i];
			if ( set == null ) {
				return null;
			}
			if ( set.isPrefix ) {
				if ( className.startsWith( set.className ) ) {
//					System.err.println( "\tClass (" + set.className + ") found." );
					return set;
				}
			} else if ( className.equals( set.className ) ) {
//				System.err.println( "\tClass (" + set.className + ") found." );
				return set;
			}
		}
		return null;
	}

	public boolean isMonitoredMethod( CtMethod method ) {
		if ( monitorAll ) return true;
		int mods = method.getModifiers();
//		String methodLongName = method.getLongName();
		String methodName = method.getName();

//		boolean isSynch     = ( ( mods & Modifier.SYNCHRONIZED ) != 0 );
//		boolean isPrivate   = ( ( mods & Modifier.PROTECTED    ) != 0 );
//		boolean isProtected = ( ( mods & Modifier.PRIVATE      ) != 0 );
		boolean isPublic    = ( ( mods & Modifier.PUBLIC       ) != 0 );
		boolean isStatic    = ( ( mods & Modifier.STATIC       ) != 0 );
//		Logger.print( "\t\tMethod:");
//		if ( isSynch     ) { Logger.print( " SYNCHRONIZED" ); }
//		if ( isPrivate   ) { Logger.print( " PRIVATE"      ); }
//		if ( isProtected ) { Logger.print( " PROTECTED"    ); }
//		if ( isPublic    ) { Logger.print( " PUBLIC"       ); }
//		if ( isStatic    ) { Logger.print( " STATIC"       ); }
//		Logger.println( " " + methodName );

		if ( monitorPublic && isPublic && ! isStatic ) {
			return true;
		}
		if ( monitorSet == null ) return false;

		for( String name : monitorSet ) {
			if ( name.equals( methodName ) ) {
				return true;
			}
		}
		return false;
	}

	private static void addClassMonitorSet( ClassMonitorSet cms ) {
		if ( mClassMonitorSets == null ) {
			mClassMonitorSets = new ClassMonitorSet[4];
		}
		// Look for an empty slot.
		for(int i=0; i < mClassMonitorSets.length; ++i) {
			if ( mClassMonitorSets[i] != null ) continue;
			mClassMonitorSets[i] = cms;
			return;
		}
		// Re-allocate a new array.
		int N  = mClassMonitorSets.length;
		int NN = 2 * N;
		ClassMonitorSet[] classMonitorSets = new ClassMonitorSet[NN];
		System.err.println( "----- Reallocating ClassMonitorSet array: " + NN );
		for(int i=0; i < N; ++i) {
			classMonitorSets[i] = mClassMonitorSets[i];
		}
		classMonitorSets[N] = cms;
		mClassMonitorSets = classMonitorSets;
	}

	static {
		addClassMonitorSet( new ClassMonitorSet( "java.io.CharArrayReader                               ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.io.CharArrayWriter                               ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.io.Console                                       ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.io.DataOutput                                    ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.io.DataOutputStream                              ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.io.DeleteOnExitHook                              ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.io.File$TempDirectory                            ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.io.FileFilter                                    ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.io.FileOutputStream$1                            ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.io.FilenameFilter                                ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.io.FilterWriter                                  ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.io.LineNumberReader                              ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.io.ObjectInput                                   ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.io.ObjectInputStream                             ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.io.ObjectOutput                                  ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.io.ObjectOutputStream                            ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.io.ObjectStreamClass                             ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.io.PushbackInputStream                           ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.io.RandomAccessFile                              ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.io.SequenceInputStream                           ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.io.StreamTokenizer                               ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.io.StringBufferInputStream                       ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.io.StringReader                                  ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.io.StringWriter                                  ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.lang.Process                                     ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.lang.ProcessBuilder                              ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.lang.ProcessEnvironment                          ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.lang.ProcessImpl                                 ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.lang.invoke.BoundMethodHandle                    ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.lang.invoke.InnerClassLambdaMetafactory          ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.lang.invoke.InvokerBytecodeGenerator             ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.lang.invoke.Invokers                             ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.lang.invoke.MethodHandles                        ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.lang.invoke.SimpleMethodHandle                   ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.AbstractPlainSocketImpl                      ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.ContentHandler                               ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.ContentHandlerFactory                        ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.CookieHandler                                ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.DefaultInterface                             ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.DualStackPlainSocketImpl                     ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.FileNameMap                                  ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.HttpURLConnection                            ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.Inet4Address                                 ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.Inet4AddressImpl                             ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.Inet6Address                                 ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.Inet6AddressImpl                             ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.InetAddress                                  ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.InetAddressImpl                              ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.InetAddressImplFactory                       ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.InetSocketAddress                            ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.InterfaceAddress                             ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.JarURLConnection                             ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.NetworkInterface                             ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.PlainSocketImpl                              ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.Proxy                                        ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.ProxySelector                                ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.ResponseCache                                ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.ServerSocket                                 ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.Socket                                       ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.SocketAddress                                ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.SocketImpl                                   ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.SocketImplFactory                            ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.SocketInputStream                            ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.SocketOption                                 ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.SocketOptions                                ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.SocketOutputStream                           ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.SocksSocketImpl                              ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.URI                                          ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.URLDecoder                                   ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.net.URLEncoder                                   ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.nio.channels.ByteChannel                         ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.nio.channels.Channel                             ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.nio.channels.Channels                            ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.nio.channels.FileChannel                         ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.nio.channels.FileLock                            ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.nio.channels.GatheringByteChannel                ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.nio.channels.InterruptibleChannel                ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.nio.channels.NetworkChannel                      ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.nio.channels.Pipe                                ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.nio.channels.ReadableByteChannel                 ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.nio.channels.ScatteringByteChannel               ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.nio.channels.SeekableByteChannel                 ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.nio.channels.SelectableChannel                   ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.nio.channels.SelectionKey                        ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.nio.channels.Selector                            ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.nio.channels.ServerSocketChannel                 ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.nio.channels.SocketChannel                       ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.nio.channels.WritableByteChannel                 ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.nio.file.DirectoryStream                         ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.nio.file.FileSystem                              ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.nio.file.FileSystems                             ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.nio.file.Files                                   ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.nio.file.OpenOption                              ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.nio.file.Paths                                   ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.nio.file.StandardOpenOption                      ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.nio.file.TempFileHelper                          ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.rmi.MarshalledObject                             ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.rmi.Remote                                       ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.rmi.activation.ActivationDesc                    ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.rmi.activation.ActivationGroupDesc               ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.rmi.activation.ActivationGroupID                 ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.rmi.activation.ActivationID                      ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.rmi.activation.ActivationInstantiator            ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.rmi.activation.ActivationMonitor                 ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.rmi.activation.ActivationSystem                  ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.rmi.dgc.DGC                                      ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.rmi.dgc.Lease                                    ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.rmi.dgc.VMID                                     ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.rmi.registry.LocateRegistry                      ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.rmi.registry.Registry                            ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.rmi.server.LogStream                             ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.rmi.server.ObjID                                 ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.rmi.server.Operation                             ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.rmi.server.RMIClassLoader                        ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.rmi.server.RMIClassLoaderSpi                     ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.rmi.server.RMIClientSocketFactory                ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.rmi.server.RMIServerSocketFactory                ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.rmi.server.RMISocketFactory                      ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.rmi.server.RemoteCall                            ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.rmi.server.RemoteObject                          ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.rmi.server.RemoteRef                             ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.rmi.server.RemoteServer                          ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.rmi.server.RemoteStub                            ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.rmi.server.ServerRef                             ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.rmi.server.Skeleton                              ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.rmi.server.UID                                   ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.rmi.server.UnicastRemoteObject                   ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.security.AccessController                        ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.security.AlgorithmConstraints                    ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.security.AlgorithmParameters                     ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.security.AlgorithmParametersSpi                  ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.security.AllPermissionCollection                 ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.security.CryptoPrimitive                         ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.security.DigestException                         ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.security.DigestInputStream                       ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.security.DigestOutputStream                      ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.security.DomainCombiner                          ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.security.Key                                     ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.security.KeyFactory                              ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.security.KeyFactorySpi                           ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.security.KeyPairGenerator                        ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.security.KeyPairGeneratorSpi                     ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.security.KeyStore                                ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.security.KeyStoreSpi                             ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.security.MessageDigest                           ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.security.MessageDigestSpi                        ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.security.PermissionsEnumerator                   ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.security.PermissionsHash                         ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.security.Policy                                  ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.security.PrivateKey                              ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.security.ProtectionDomain$3$1                    ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.security.Provider                                ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.security.PublicKey                               ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.security.SecureRandom                            ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.security.SecureRandomSpi                         ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.security.Security                                ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.security.SecurityPermission                      ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.security.Signature                               ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.security.SignatureSpi                            ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.sql.Array                                        ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.sql.Blob                                         ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.sql.CallableStatement                            ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.sql.Clob                                         ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.sql.Connection                                   ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.sql.DatabaseMetaData                             ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.sql.Date                                         ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.sql.Driver                                       ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.sql.DriverInfo                                   ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.sql.DriverManager                                ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.sql.DriverPropertyInfo                           ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.sql.NClob                                        ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.sql.ParameterMetaData                            ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.sql.PreparedStatement                            ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.sql.Ref                                          ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.sql.ResultSet                                    ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.sql.ResultSetMetaData                            ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.sql.RowId                                        ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.sql.SQLPermission                                ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.sql.SQLType                                      ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.sql.SQLWarning                                   ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.sql.SQLXML                                       ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.sql.Savepoint                                    ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.sql.Statement                                    ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.sql.Time                                         ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.sql.Timestamp                                    ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.sql.Wrapper                                      ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.AbstractList                                ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.AbstractMap$SimpleImmutableEntry            ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.AbstractQueue                               ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.AbstractSequentialList                      ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.ArrayList                                   ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.Arrays$ArrayList                            ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.Arrays$LegacyMergeSort                      ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.Calendar                                    ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.Collections                                 ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.EnumMap                                     ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.EnumSet                                     ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.EventListener                               ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.EventListenerProxy                          ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.EventObject                                 ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.Formattable                                 ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.Formatter                                   ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.HashMap                                     ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.Hashtable                                   ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.IdentityHashMap                             ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.JumboEnumSet                                ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.LinkedHashSet                               ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.LinkedHashMap                               ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.LinkedList                                  ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.NavigableMap                                ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.NavigableSet                                ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.Observable                                  ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.Observer                                    ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.Random                                      ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.RandomAccessSubList                         ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.RegularEnumSet                              ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.ResourceBundle                              ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.SortedMap                                   ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.SortedSet                                   ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.Spliterator                                 ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.SubList                                     ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.TaskQueue                                   ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.TimSort                                     ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.Timer                                       ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.TimerTask                                   ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.TimerThread                                 ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.TreeMap                                     ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.TreeSet                                     ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.Vector                                      ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.WeakHashMap                                 ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.concurrent.AbstractExecutorService          ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.concurrent.ArrayBlockingQueue               ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.concurrent.BlockingQueue                    ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.concurrent.Callable                         ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.concurrent.ConcurrentHashMap                ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.concurrent.ConcurrentLinkedQueue            ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.concurrent.CopyOnWriteArrayList             ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.concurrent.CopyOnWriteArraySet              ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.concurrent.CountDownLatch                   ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.concurrent.Delayed                          ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.concurrent.Executor                         ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.concurrent.ExecutorService                  ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.concurrent.Executors                        ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.concurrent.Future                           ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.concurrent.FutureTask                       ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.concurrent.LinkedBlockingQueue              ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.concurrent.RunnableFuture                   ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.concurrent.RunnableScheduledFuture          ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.concurrent.ScheduledExecutorService         ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.concurrent.ScheduledFuture                  ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.concurrent.ScheduledThreadPoolExecutor      ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.concurrent.Semaphore                        ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.concurrent.SynchronousQueue                 ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.concurrent.ThreadFactory                    ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.concurrent.ThreadLocalRandom                ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.concurrent.ThreadPoolExecutor               ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.concurrent.locks.AbstractOwnableSynchronizer", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.concurrent.locks.AbstractQueuedSynchronizer ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.concurrent.locks.Condition                  ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.concurrent.locks.LockSupport                ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.concurrent.locks.ReadWriteLock              ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.concurrent.locks.ReentrantLock              ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.concurrent.locks.ReentrantReadWriteLock     ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.regex.Pattern                               ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.mail.BodyPart                                   ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.mail.Multipart                                  ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.mail.Part                                       ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.mail.internet.MimeBodyPart                      ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.mail.internet.MimeMultipart                     ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.mail.internet.MimePart                          ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.Filter                                  ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.FilterChain                             ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.FilterConfig                            ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.GenericServlet                          ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.RequestDispatcher                       ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.Servlet                                 ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.ServletConfig                           ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.ServletContext                          ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.ServletContextAttributeEvent            ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.ServletContextAttributeListener         ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.ServletContextEvent                     ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.ServletContextListener                  ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.ServletInputStream                      ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.ServletOutputStream                     ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.ServletRequest                          ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.ServletRequestAttributeEvent            ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.ServletRequestAttributeListener         ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.ServletRequestEvent                     ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.ServletRequestListener                  ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.ServletRequestWrapper                   ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.ServletResponse                         ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.ServletResponseWrapper                  ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.SingleThreadModel                       ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.http.Cookie                             ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.http.HttpServlet                        ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.http.HttpServletRequest                 ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.http.HttpServletRequestWrapper          ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.http.HttpServletResponse                ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.http.HttpServletResponseWrapper         ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.http.HttpSession                        ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.http.HttpSessionActivationListener      ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.http.HttpSessionAttributeListener       ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.http.HttpSessionBindingEvent            ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.http.HttpSessionBindingListener         ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.http.HttpSessionContext                 ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.http.HttpSessionEvent                   ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.http.HttpSessionListener                ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.HttpJspPage                         ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.JspApplicationContext               ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.JspContext                          ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.JspEngineInfo                       ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.JspFactory                          ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.JspPage                             ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.JspWriter                           ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.PageContext                         ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.el.Expression                       ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.el.ExpressionEvaluator              ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.el.FunctionMapper                   ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.el.ImplicitObjectELResolver         ", true , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.el.ScopedAttributeELResolver        ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.el.VariableResolver                 ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.jstl.core.ConditionalTagSupport     ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.jstl.core.Config                    ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.jstl.core.LoopTag                   ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.jstl.core.LoopTagStatus             ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.jstl.core.LoopTagSupport            ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.jstl.fmt.LocalizationContext        ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.tagext.BodyContent                  ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.tagext.BodyTag                      ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.tagext.BodyTagSupport               ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.tagext.DynamicAttributes            ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.tagext.FunctionInfo                 ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.tagext.IterationTag                 ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.tagext.JspFragment                  ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.tagext.JspIdConsumer                ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.tagext.JspTag                       ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.tagext.PageData                     ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.tagext.SimpleTag                    ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.tagext.SimpleTagSupport             ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.tagext.Tag                          ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.tagext.TagAdapter                   ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.tagext.TagAttributeInfo             ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.tagext.TagData                      ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.tagext.TagExtraInfo                 ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.tagext.TagFileInfo                  ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.tagext.TagInfo                      ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.tagext.TagLibraryInfo               ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.tagext.TagLibraryValidator          ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.tagext.TagSupport                   ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.tagext.TagVariableInfo              ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.tagext.TryCatchFinally              ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.servlet.jsp.tagext.VariableInfo                 ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.transaction.Synchronization                     ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.transaction.Transaction                         ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.transaction.TransactionManager                  ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.transaction.TransactionSynchronizationRegistry  ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.transaction.UserTransaction                     ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.transaction.xa.XAResource                       ", false, true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "javax.transaction.xa.Xid                              ", false, true, false ) );
	}


}




/*
	private static void addLinkedList() {
		addClassMonitorSet( new ClassMonitorSet( "java.util.LinkedList"        , true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.LinkedList$ListItr", true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.LinkedList$Node"   , true, false ) );
	}

	private static void addHashSet() {
		ClassMonitorSet cmsHashSet  = new ClassMonitorSet( "java.util.HashSet" , 4 );
		cmsHashSet.monitorSet[0] = "iterator";
		cmsHashSet.monitorSet[1] = "contains";
		cmsHashSet.monitorSet[2] = "add";
		cmsHashSet.monitorSet[3] = "remove";
		addClassMonitorSet( cmsHashSet );
	}

	private static void addHashMap() {
		ClassMonitorSet cmsHashMap  = new ClassMonitorSet( "java.util.HashMap" , 6 );
		cmsHashMap.monitorSet[0] = "put";
		cmsHashMap.monitorSet[1] = "get";
		cmsHashMap.monitorSet[2] = "keySet";
		cmsHashMap.monitorSet[3] = "valueSet";
		cmsHashMap.monitorSet[4] = "containsKey";
		cmsHashMap.monitorSet[5] = "remove";
		addClassMonitorSet( cmsHashMap );

		addClassMonitorSet( new ClassMonitorSet( "java.util.HashMap$KeySet",        true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.HashMap$Values",        true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.HashMap$ValueIterator", true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.HashMap$HashIterator",  true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.HashMap$KeyIterator",   true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.HashMap$EntrySet",      true, false ) );
		addClassMonitorSet( new ClassMonitorSet( "java.util.HashMap$EntryIterator", true, false ) );
	}

	private static void addIterator() {
		ClassMonitorSet cmsIterator = new ClassMonitorSet( "java.util.Iterator", 3 );
		cmsIterator.monitorSet[0] = "hasNext";
		cmsIterator.monitorSet[1] = "next";
		cmsIterator.monitorSet[2] = "remove";
		addClassMonitorSet( cmsIterator );
	}

	private static void addRandom() {
		ClassMonitorSet cmsRandom   = new ClassMonitorSet( "java.util.Random"  , 1 );
		cmsRandom.monitorSet[0] = "nextLong";
		addClassMonitorSet( cmsRandom );
	}

*/



