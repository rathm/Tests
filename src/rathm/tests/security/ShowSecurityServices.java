/*
 * List the security providers and services available in this Java environment 
 * 
 * Usage:
 * java -cp ~rathm/workspace_tests/Tests/bin/ rathm.tests.security.ShowSecurityServices | less
 */

package rathm.tests.security;

import java.security.Provider;
import java.security.Security;
import java.security.Provider.Service;
import java.util.Set;

public class ShowSecurityServices
{
	
	/**
	 * @param args
	 *            none
	 */
	public static void main(String[] args)
	{
		Provider[] providers = Security.getProviders();
		
		System.out.println("Installed providers ordered by preference, and their services");
		System.out.println("-------------------------------------------------------------");
		for (int i = 0; i < providers.length; i++)
		{
			System.out.format("%d\tName: %s%n", i + 1, providers[i].getName());
			System.out.format("\tVersion: %f%n", providers[i].getVersion());
			
			// Get all services supported by this Provider
			Set<Provider.Service> services = providers[i].getServices();
			
			// Display details about the services
			for (Service service : services)
			{
				System.out.format("\t\tType: %s%n", service.getType());
				System.out.format("\t\tAlgorithm: %s%n", service.getAlgorithm());
				System.out.format("\t\tClassname: %s%n", service.getClassName());
				System.out.println();
			}
			
		}
		
	}
	
}
