package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import models.IPTest.Type;
import static models.IPTest.Type.IPv6;
import org.xbill.DNS.TextParseException;

public class Ping
{
	/**
	 * Ping a given URL using a given ping method.
	 *
	 * @param url    the String value of the URL to ping
	 * @param ipType the {@link Type} of IP to ping with
	 * @return the int value of the number of milliseconds passed to receive a reply
	 *
	 * @throws IOException
	 */
	public static int ping( final String url, final Type ipType ) throws IOException
	{
		if( url == null )
		{
			throw new NullPointerException();
		}

		final Process process = new ProcessBuilder( "ping", ipType == IPv6 ? "-6" : "-4", url ).start();
		return processPing( process );
	}

	/**
	 * Ping a given IP address.
	 *
	 * @param ipAddress the String value of the IP address to ping
	 * @return the int value of the number of milliseconds passed to receive a reply
	 *
	 * @throws IOException
	 */
	public static int ping( final String ipAddress ) throws IOException
	{
		if( ipAddress == null )
		{
			throw new NullPointerException();
		}

		final Process process = new ProcessBuilder( "ping", ipAddress ).start();
		return processPing( process );
	}

	private static int processPing( final Process process ) throws IOException
	{
		final List<String> pingOutput = readPingOutput( process );
		return getMinimumPing( pingOutput );
	}

	private static List<String> readPingOutput( final Process process ) throws IOException
	{
		final List<String> pingOutput = new ArrayList<>();

		try( final InputStreamReader inputStreamReader = new InputStreamReader( process.getInputStream() );
			 final BufferedReader reader = new BufferedReader( inputStreamReader ) )
		{
			String line;
			while( ( line = reader.readLine() ) != null )
			{
				pingOutput.add( line );
			}
		}

		return pingOutput;
	}

	private static int getMinimumPing( final List<String> pingOutput ) throws TextParseException
	{
		final String lastLine = pingOutput.get( pingOutput.size() - 1 );
		int index = lastLine.indexOf( '=' );

		if( index != -1 )
		{
			index += 2;
			final int endIndex = lastLine.indexOf( 'm', index );
			if( endIndex != -1 )
			{
				return Integer.parseInt( lastLine.substring( index, endIndex ) );
			}
			else
			{
				throw new TextParseException();
			}
		}
		else
		{
			throw new TextParseException();
		}
	}
}
