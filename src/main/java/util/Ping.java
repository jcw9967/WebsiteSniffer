package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.xbill.DNS.TextParseException;

public class Ping
{
	public static int IPv6( final String ipv6Address ) throws IOException
	{
		final Process process = new ProcessBuilder( "ping", "-6", ipv6Address ).start();

		return processPing( process );
	}

	public static int IPv4( final String ipv4Address ) throws IOException
	{
		final Process process = new ProcessBuilder( "ping", "-4", ipv4Address ).start();
		
		return processPing( process );
	}

	private static int processPing( final Process process ) throws IOException
	{
		final List<String> pingOutput = readPingOutput( process );
			
		return getAveragePing( pingOutput );
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

	private static int getAveragePing( final List<String> pingOutput ) throws TextParseException
	{
		final String lastLine = pingOutput.get( pingOutput.size() - 1 );
		int index = lastLine.lastIndexOf( '=' );

		if( index != -1 )
		{
			index += 2;
			final int endIndex = lastLine.lastIndexOf( 'm' );
			return Integer.parseInt( lastLine.substring( index, endIndex ) );
		}
		else
		{
			throw new TextParseException();
		}
	}
}
