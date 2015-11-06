/*
 * Based on Java Security tutorial from 
 * http://docs.oracle.com/javase/tutorial/security/toolsign/index.html
 * See Java Tutorials Trail "Security Features in Java SE" at 
 * http://docs.oracle.com/javase/tutorial/security/index.html
 * 
 * To demonstrate the effect of permissions, the data file read by this code should be in a different (not sub-directory) directory
 * than the code (class).
 * The class file is in directory: /home/rathm/workspace_tests/Tests/bin/rathm/tests/security/
 * That is apparently enough for the Java security manager.
 * I placed the jar file and data file in the same directory, and the security manager prevented access without specific permissions.
 * 
 * Package into a jar file:
 * cd /home/rathm/Learning/SecurityTutorial/Lesson_SigningCodeAndGrantingItPermissions
 * jar cvf Count.jar -C /home/rathm/workspace_tests/Tests/bin/ rathm/tests/security/Count.class
 * 
 * Usage example 1: with no security manager
 * cd /home/rathm/Learning/SecurityTutorial/Lesson_SigningCodeAndGrantingItPermissions
 * java -cp Count.jar rathm.tests.security.Count count_me
 * 
 * Usage example 2: with security manager, signed jar file and policy containing permission
 * cd /home/rathm/Learning/SecurityTutorial/Lesson_SigningCodeAndGrantingItPermissions
 * java -Djava.security.manager -Djava.security.policy=exampleraypolicy -cp sCount.jar rathm.tests.security.Count count_me
 * 
 * ---------------------------------- Original copyright notice ----------------------------------
 * 
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package rathm.tests.security;

import java.io.*;

public class Count
{
	public static void countChars(InputStream in) throws IOException
	{
		int count = 0;
		
		while (in.read() != -1)
			count++;
		
		System.out.println("Counted " + count + " chars.");
	}
	
	public static void main(String[] args) throws Exception
	{
		if (args.length == 1)
			countChars(new FileInputStream(args[0]));
		else
			System.err.println("Usage: Count filename");
	}
}
