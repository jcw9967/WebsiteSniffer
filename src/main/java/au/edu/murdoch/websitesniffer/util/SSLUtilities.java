/* 
 * Copyright (C) 2016 Jordan
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package au.edu.murdoch.websitesniffer.util;

import javax.net.ssl.*;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * This class provide various static methods that relax X509 certificate and
 * hostname verification while using the SSL over the HTTP protocol.
 *
 * @author Jiramot.info
 */
public final class SSLUtilities
{

	private static HostnameVerifier _hostnameVerifier;
	/**
	 * Trust managers.
	 */
	private static TrustManager[] _trustManagers;

	/**
	 * Set the default Hostname Verifier to an instance of a fake class that
	 * trust all hostnames.
	 */
	private static void _trustAllHostnames()
	{
		// Create a trust manager that does not validate certificate chains
		if( _hostnameVerifier == null )
		{
			_hostnameVerifier = new SSLUtilities.FakeHostnameVerifier();
		}

		// Install the all-trusting host name verifier:
		HttpsURLConnection.setDefaultHostnameVerifier( _hostnameVerifier );
	}

	/**
	 * Set the default X509 Trust Manager to an instance of a fake class that
	 * trust all certificates, even the self-signed ones.
	 */
	private static void _trustAllHttpsCertificates()
	{
		SSLContext context;

		// Create a trust manager that does not validate certificate chains
		if( _trustManagers == null )
		{
			_trustManagers = new TrustManager[]
					{
							new SSLUtilities.FakeX509TrustManager()
					};
		}
		// Install the all-trusting trust manager:
		try
		{
			context = SSLContext.getInstance( "SSL" );
			context.init( null, _trustManagers, new SecureRandom() );
		}
		catch( final GeneralSecurityException gse )
		{
			throw new IllegalStateException( gse.getMessage() );
		}

		HttpsURLConnection.setDefaultSSLSocketFactory( context.getSocketFactory() );
	}

	/**
	 * Set the default Hostname Verifier to an instance of a fake class that
	 * trust all hostnames.
	 */
	public static void trustAllHostnames()
	{
		_trustAllHostnames();
	}

	/**
	 * Set the default X509 Trust Manager to an instance of a fake class that
	 * trust all certificates, even the self-signed ones.
	 */
	public static void trustAllHttpsCertificates()
	{
		_trustAllHttpsCertificates();
	}

	/**
	 * This class implements a fake hostname verificator, trusting any host
	 * name.
	 *
	 * @author Jiramot.info
	 */
	private static class FakeHostnameVerifier implements HostnameVerifier
	{

		/**
		 * Always return true, indicating that the host name is an acceptable
		 * match with the server's authentication scheme.
		 *
		 * @param hostname the host name.
		 * @param session  the SSL session used on the connection to host.
		 * @return the true boolean value indicating the host name is trusted.
		 */
		@Override
		public boolean verify( final String hostname, final javax.net.ssl.SSLSession session )
		{
			return true;
		}
	}

	/**
	 * This class allow any X509 certificates to be used to authenticate the
	 * remote side of a secure socket, including self-signed certificates.
	 *
	 * @author Jiramot.info
	 */
	private static class FakeX509TrustManager implements X509TrustManager
	{
		/**
		 * Empty array of certificate authority certificates.
		 */
		private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[]
				{
				};

		/**
		 * Always trust for client SSL chain peer certificate chain with any
		 * authType authentication types.
		 *
		 * @param chain    the peer certificate chain.
		 * @param authType the authentication type based on the client
		 *                 certificate.
		 */
		@Override
		public void checkClientTrusted( final X509Certificate[] chain, final String authType )
		{
		}

		/**
		 * Always trust for server SSL chain peer certificate chain with any
		 * authType exchange algorithm types.
		 *
		 * @param chain    the peer certificate chain.
		 * @param authType the key exchange algorithm used.
		 */
		@Override
		public void checkServerTrusted( final X509Certificate[] chain, final String authType )
		{
		}

		/**
		 * Return an empty array of certificate authority certificates which are
		 * trusted for authenticating peers.
		 *
		 * @return a empty array of issuer certificates.
		 */
		@Override
		public X509Certificate[] getAcceptedIssuers()
		{
			return ( _AcceptedIssuers );
		}
	}
}
