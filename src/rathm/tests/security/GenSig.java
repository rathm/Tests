/*
 * Generate a signature for a file
 * -------------------------------
 * 
 * Based on Java Security tutorial from 
 * http://docs.oracle.com/javase/tutorial/security/apisign/index.html
 * See Java Tutorials Trail "Security Features in Java SE" at 
 * http://docs.oracle.com/javase/tutorial/security/index.html
 * 
 * The signature is written to a file whose name is the name of the signed file + ".sig"
 * 
 * The program also generates a key-pair used to sign and verify the file.
 * The public key is written to a file named "GenSig.key.pub"
 * 
 * Usage:
 * java -cp ~rathm/workspace_tests/Tests/bin/ rathm.tests.security.GenSig nameOfFileToSign
 */

package rathm.tests.security;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;

class GenSig
{
	
	/**
	 * @param args
	 *            File to sign
	 */
	public static void main(String[] args)
	{
		final String PUBLIC_KEY_FILE_NAME = "GenSig.key.pub";
		final String SIGNATURE_FILE_SUFFIX = ".sig";
		
		String fileName = null;
		
		if (args.length != 1)
		{
			System.err.println("Usage: GenSig nameOfFileToSign");
		}
		else
		{
			try
			{
				fileName = args[0];
				
				// Get a key-pair generator for the Digital Signature Algorithm (DSA), as provided by the
				// built-in SUN provider
				KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
				
				/*
				 * Get a cryptographically strong random number generator (RNG)
				 * ------------------------------------------------------------
				 * Starting from JDK 8 Java distributions include a list of known strong SecureRandom
				 * implementations in the securerandom.strongAlgorithms property.
				 * You can use code like the below to show which algorithms and providers are available in your Java environment.
				 *     System.out.println(Security.getProperty("securerandom.strongAlgorithms"));
				 * Example output for openjdk version "1.8.0_40"
				 *     NativePRNGBlocking:SUN
				 *     
				 * Using this example, you could get a random number generator like so:
				 *     SecureRandom random = SecureRandom.getInstance("NativePRNGBlocking", "SUN");
				 *     
				 * Alternatively, you can use the SecureRandom.getInstanceStrong() method, available from Java 8.
				 * This method returns a SecureRandom object that was selected by using the algorithms/providers specified 
				 * in the above mentioned securerandom.strongAlgorithms Security property.
				 * Example:
				 *     SecureRandom random = SecureRandom.getInstanceStrong();
				 */
				
				// Get a cryptographically strong random number generator (RNG) which uses the SHA1PRNG
				// algorithm, as provided by the built-in SUN provider.
				// I use the algorithm and provider from the Java tutorial example
				// http://docs.oracle.com/javase/tutorial/security/apisign/step2.html
				SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
				
				// Initialize the key-pair generator with a key size and a source of randomness.
				keyGen.initialize(1024, random);
				
				// Generate the key pair
				KeyPair pair = keyGen.generateKeyPair();
				PrivateKey priv = pair.getPrivate();
				PublicKey pub = pair.getPublic();
				
				// Save the public key in a file
				byte[] key = pub.getEncoded();
				FileOutputStream keyfos = new FileOutputStream("GenSig.key.pub");
				keyfos.write(key);
				keyfos.close();
				System.out.println("Created public key in file: " + PUBLIC_KEY_FILE_NAME );
				
				// Get a Signature Object for generating signatures using the DSA algorithm, the same
				// algorithm I used to generate the keys.
				// Note: When specifying the signature algorithm name, you should also include the name of the
				// message digest algorithm used by the signature algorithm.
				// SHA1withDSA is a way of specifying the DSA signature algorithm, using the SHA-1 message
				// digest algorithm.
				Signature dsa = Signature.getInstance("SHA1withDSA", "SUN");
				
				// Before a Signature object can be used for signing or verifying, it must be initialized.
				// The initialization method for signing requires a private key.
				dsa.initSign(priv);
				
				// Supply the Signature Object the data to be signed.
				// Read the file, a buffer at a time, and supply it to the Signature object by calling the
				// update method.
				FileInputStream fis = new FileInputStream(fileName);
				BufferedInputStream bufin = new BufferedInputStream(fis);
				byte[] buffer = new byte[1024];
				int len;
				while ((len = bufin.read(buffer)) >= 0)
				{
					dsa.update(buffer, 0, len);
				}
				bufin.close();
				
				// Generate the digital signature of the data provided to the Signature object.
				byte[] realSig = dsa.sign();
				
				// Save the signature in a file whose name is the name of the signed file + ".sig"
				String signatureFileName = fileName + SIGNATURE_FILE_SUFFIX;
				FileOutputStream sigfos = new FileOutputStream(signatureFileName);
				sigfos.write(realSig);
				sigfos.close();
				System.out.println("Created signature in file: " + signatureFileName);
			}
			catch (NoSuchAlgorithmException | NoSuchProviderException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (InvalidKeyException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (FileNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (SignatureException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}