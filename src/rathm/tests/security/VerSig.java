/*
 * Verify a signature for a file
 * ------------------------------
 * 
 * Based on Java Security tutorial from 
 * http://docs.oracle.com/javase/tutorial/security/apisign/index.html
 * See Java Tutorials Trail "Security Features in Java SE" at 
 * http://docs.oracle.com/javase/tutorial/security/index.html
 * 
 * The program reads the contents of three files whose names are provided on the command line.
 * The files contain:
 * 
 *     publickeyfile   The public key from the pair whose private key was used to generate the signature
 *                     for the datafile content.
 *                     A PublickKey object is built using the content of the file publickeyfile.
 *                     
 *     signaturefile   The signature of the datafile's content.
 *                     A Signature object is created using the content of the file signaturefile.
 *                     The Signature is initialized with the PublicKey.
 *                     The Signature is then fed the contents of the datafile.
 *                     Using this, the Signature is used to verify the contents of datafile.
 *     
 *     datafile        File whose content we want to verify using this mechanism.
 * 
 * The program writes to console the result of the verification: true / false.
 * 
 * Usage:
 * java -cp ~rathm/workspace_tests/Tests/bin/ rathm.tests.security.VerSig publickeyfile signaturefile datafile
 */
package rathm.tests.security;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

class VerSig
{
	public static void main(String[] args)
	{
		if (args.length != 3)
		{
			System.out.println("Usage: VerSig publickeyfile signaturefile datafile");
		}
		else
		{
			String publicKeyFile = args[0];
			String signatureFile = args[1];
			String dataFile = args[2];
			
			try
			{
				/*
				 * Input and convert the encoded public key bytes
				 */
				
				// Read the encoded public key bytes from the public key file
				FileInputStream keyfis = new FileInputStream(publicKeyFile);
				byte[] encKey = new byte[keyfis.available()];
				keyfis.read(encKey);
				keyfis.close();
				
				// Build a key specification.
				// The key was generated with the built-in DSA key-pair generator
				// supplied by the SUN provider so we can use the X.509 standard
				X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encKey);
				
				// Get a KeyFactory for the algorithm and provider used by the key
				KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
				
				// Generate a PublicKey from the key specification
				PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
				
				/*
				 * Input the signature bytes
				 */
				
				FileInputStream sigfis = new FileInputStream(signatureFile);
				byte[] sigToVerify = new byte[sigfis.available()];
				sigfis.read(sigToVerify);
				sigfis.close();
				
				/*
				 * Verify the signature
				 */
				
				// Create a Signature object that uses the same signature algorithm as was used to generate
				// the signature.
				Signature sig = Signature.getInstance("SHA1withDSA", "SUN");
				
				// Initialize the Signature object, using the public key.
				sig.initVerify(pubKey);
				
				// Supply the Signature object with the data for which a signature was generated.
				// This is the contents of the datafile file.
				FileInputStream datafis = new FileInputStream(dataFile);
				BufferedInputStream bufin = new BufferedInputStream(datafis);
				
				byte[] buffer = new byte[1024];
				int len;
				while (bufin.available() != 0)
				{
					len = bufin.read(buffer);
					sig.update(buffer, 0, len);
				}
				;
				bufin.close();
				
				/*
				 * Verify the signature
				 */
				
				boolean verifies = sig.verify(sigToVerify);
				
				System.out.println("Signature verified: " + verifies);
			}
			catch (FileNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (NoSuchAlgorithmException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (NoSuchProviderException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (InvalidKeySpecException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (InvalidKeyException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (SignatureException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}