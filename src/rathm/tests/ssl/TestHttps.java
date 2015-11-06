/*
 * Test HTTPS
 * ==========
 * Write to console the content from an https://... URL
 * Use System properties such as -Djavax.net.debug=ssl:handshake:verbose to write to console debug data 
 * 
 * Useful web pages:
 *    https://blogs.oracle.com/java-platform-group/entry/diagnosing_tls_ssl_and_https
 *    http://docs.oracle.com/javase/8/docs/technotes/guides/security/jsse/ReadDebug.html
 *    http://docs.oracle.com/javase/8/docs/technotes/guides/security/jsse/JSSERefGuide.html
 *       See Related Documentation section of this page.
 *
 * Usage examples
 * --------------
 * java -Djavax.net.debug=ssl:handshake:verbose -cp ~rathm/workspace_tests/Tests/bin/ rathm.tests.ssl.TestHttps https://blogs.oracle.com/java-platform-group/entry/diagnosing_tls_ssl_and_https | less
 * 
 * @author rathm
 *
 */

package rathm.tests.ssl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class TestHttps
{
	
	/**
	 * @param args URL to test
	 */
	public static void main(String[] args)
	{
		String strUrl = "";
		
		// Verify arguments
		if (args.length == 1)
		{
			strUrl = args[0];
		}
		else
		{
			System.err.println("[ERROR] Missing mandatory argument: URL to test");
			System.exit(1);
		}

		// Convert argument to URL
		URL url = null;
		try
		{
			url = new URL(strUrl);
		}
		catch (MalformedURLException e)
		{
			System.err.println("[ERROR] Malformed URL: [" + strUrl + "]");
			e.printStackTrace();
			System.exit(1);
		}

		// Open connection to URL
		HttpsURLConnection conn = null;
		try
		{
			conn = (HttpsURLConnection) url.openConnection();
			conn.connect();
		}
		catch (IOException e)
		{
			System.err.println("[ERROR] Error getting or opening connection to URL: [" + strUrl + "]");
			e.printStackTrace();
			System.exit(1);
		}

		// Read all content from URL
		try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream())))
		{
			System.out.println("Data read from URL: [" + strUrl + "]");
			System.out.println("----------------------------------------------------------------------------------------");

			String line;
			while ((line = br.readLine()) != null)
				System.out.println(line);
		}
		catch (IOException e)
		{
			System.err.println("[ERROR] Error reading from URL: [" + strUrl + "]");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
}
