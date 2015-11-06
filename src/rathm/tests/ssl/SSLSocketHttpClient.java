/*
 * Based on demo program see: 
 * http://docs.oracle.com/javase/8/docs/technotes/guides/security/jsse/samples/sockets/client/SSLSocketClient.java
 * Does a GET http request to a host (e.g. blogs.oracle.com) using https with standard (443) port.
 * Writes the response to console.
 * Equivalent of browsing to: https://blogs.oracle.com
 * 
 * Usage:
 *     java rathm.tests.ssl.SSLSocketHttpClient <HOST>
 *     NOTE: the HOST must use https
 * Example:
 *     java rathm.tests.ssl.SSLSocketHttpClient blogs.oracle.com
 *     This is equivalent to browsing: https://blogs.oracle.com
 * 
 * ---------- Original demo copyright notice ----------
 * Copyright (c) 1994, 2004, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 *
 * -Redistribution of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * Redistribution in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the
 * distribution.
 *
 * Neither the name of Oracle nor the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT
 * OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 * THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 */

package rathm.tests.ssl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;

import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/*
 * This example demonstrates how to use a SSLSocket as client to
 * send a HTTP request and get response from an HTTPS server.
 * It assumes that the client is not behind a firewall
 */

public class SSLSocketHttpClient
{
	
	/**
	 * Write String to standard output with title
	 * 
	 * @param title
	 *            Title of string
	 * @param str
	 *            String
	 */
	static void printString(String title, String str)
	{
		System.out.println("\n" + title);
		System.out.println("------------------------------------------");
		System.out.format("%s%n", str);
	}
	
	/**
	 * Write array of strings to standard output. Array is sorted before output.
	 * 
	 * @param title
	 *            Title of array.
	 * @param array
	 *            Array of strings, output sorted and numbered.
	 */
	static void printSortedArray(String title, String[] array)
	{
		System.out.println("\n" + title);
		System.out.println("------------------------------------------");
		Arrays.sort(array);
		for (int i = 0; i < array.length; i++)
		{
			System.out.format("%3d) %s%n", i + 1, array[i]);
		}
	}
	
	/**
	 * @param args
	 *            Host to test (e.g. www.verisign.com)
	 */
	public static void main(String[] args)
	{
		String host = "";
		
		// Verify arguments
		if (args.length == 1)
		{
			host = args[0];
		}
		else
		{
			System.err.println("[ERROR] Missing mandatory argument: host to test");
			System.exit(1);
		}
		
		try
		{
			SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			
			SSLSocket socket = (SSLSocket) factory.createSocket(host, 443);
			
			/*
			 * Show attributes supported by the Java implementation, and those attributes enabled for this socket 
			 */
			String[] supportedProtocols = socket.getSupportedProtocols();
			printSortedArray(
					"Names of the protocols which could be enabled for use on an SSL connection (sorted alphabetically)",
					supportedProtocols);
			
			String[] enabledProtocols = socket.getEnabledProtocols();
			printSortedArray(
					"Names of the protocol versions which are currently enabled for use on this connection (sorted alphabetically)",
					enabledProtocols);
			
			String[] supportedCipherSuites = socket.getSupportedCipherSuites();
			printSortedArray(
					"Names of the cipher suites which could be enabled for use on this connection (sorted alphabetically)",
					supportedCipherSuites);
			
			String[] enabledCipherSuites = socket.getEnabledCipherSuites();
			printSortedArray(
					"Names of the SSL cipher suites which are currently enabled for use on this connection (sorted alphabetically)",
					enabledCipherSuites);
			
			/*
			 * send http request
			 *
			 * Before any application data is sent or received, the
			 * SSL socket will do SSL handshaking first to set up
			 * the security attributes.
			 *
			 * SSL handshaking can be initiated by either flushing data
			 * down the pipe, or by starting the handshaking by hand.
			 *
			 * Handshaking is started manually in this example because
			 * PrintWriter catches all IOExceptions (including
			 * SSLExceptions), sets an internal error flag, and then
			 * returns without rethrowing the exception.
			 *
			 * Unfortunately, this means any error messages are lost,
			 * which caused lots of confusion for others using this
			 * code.  The only way to tell there was an error is to call
			 * PrintWriter.checkError().
			 */
			socket.startHandshake();
			
			SSLSession session = socket.getSession();
			
			/*
			 * Show attributes of this SSL session
			 */
			printString("Standard name of the protocol used for all connections in the session",
					session.getProtocol());
			
			printString("Name of the SSL cipher suite which is used for all connections in the session",
					session.getCipherSuite());
			
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			
			/*
			 * Send the HTTP request to the host
			 */
			out.println("GET / HTTP/1.1");
			out.println("Host: " + host);
			// We indicate we want the connect closed after response, so we can easily end the read response
			// loop.
			out.println("Connection: close");
			out.println();
			out.flush();
			
			/*
			 * Make sure there were no surprises
			 */
			if (out.checkError())
				System.err.println("[ERROR] java.io.PrintWriter error");
			
			/*
			 * Display response from server.
			 */
			System.out.println("\nResponse from server");
			System.out.println("--------------------");
			
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			String inputLine;
			/*
			 * This loop will exit only if the server closes the connection after sending the response.
			 * Because of this, I send the "Connection: close" header in the request (see above) which tells the server I 
			 * cannot handle more than one document per connection. 
			 */
			while ((inputLine = in.readLine()) != null)
				System.out.println(inputLine);
			
			in.close();
			out.close();
			socket.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
