package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Ping
{
    public static Integer IPv6( final String ipv6Address )
    {
	try
	{
	    final Process process = new ProcessBuilder( "ping", "-6", ipv6Address ).start();
	    return processPing( process );
	}
	catch( final IOException ex )
	{
	    Logger.getLogger( Ping.class.getName() ).log( Level.SEVERE, null, ex );
	    return null;
	}
    }

    public static Integer IPv4( final String ipv4Address )
    {
	try
	{
	    final Process process = new ProcessBuilder( "ping", "-4", ipv4Address ).start();
	    return processPing( process );
	}
	catch( final IOException ex )
	{
	    Logger.getLogger( Ping.class.getName() ).log( Level.SEVERE, null, ex );
	    return null;
	}
    }

    private static Integer processPing( final Process process )
    {
	try
	{
	    final List<String> pingOutput = readPingOutput( process );
	    final int averagePing = getAveragePing( pingOutput );

	    return averagePing;
	}
	catch( IOException ex )
	{
	    Logger.getLogger( Ping.class.getName() ).log( Level.SEVERE, null, ex );
	    return null;
	}
    }

    private static List<String> readPingOutput( final Process process ) throws IOException
    {
	final BufferedReader reader = new BufferedReader( new InputStreamReader( process.getInputStream() ) );

	final List<String> lines = new ArrayList<>();
	String line;
	while( ( line = reader.readLine() ) != null )
	{
	    lines.add( line );
	}

	reader.close();

	return lines;
    }

    private static Integer getAveragePing( final List<String> pingOutput ) throws NoSuchFieldError
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
	    throw new NoSuchFieldError();
	}
    }
}
