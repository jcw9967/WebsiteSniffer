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

import au.edu.murdoch.websitesniffer.models.IPTest.Type;
import org.apache.commons.lang3.SystemUtils;
import org.xbill.DNS.TextParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static au.edu.murdoch.websitesniffer.models.IPTest.Type.IPv4;

public class Ping
{
	/**
	 * Ping a given URL using a given ping method.
	 *
	 * @param address the String value of the URL or IP address to ping
	 * @param ipType  the {@link Type} of IP to ping with
	 * @return the int value of the number of milliseconds passed to receive a reply
	 *
	 * @throws IOException
	 */
	public static int ping( final String address, final Type ipType ) throws IOException, TimeoutException
	{
		if( address == null )
		{
			throw new NullPointerException( "Address is null!" );
		}

		final ProcessBuilder builder = new ProcessBuilder();
		if( SystemUtils.IS_OS_WINDOWS )
		{
			builder.command( "ping", ipType == IPv4 ? "-4" : "-6", address );
		}
		else
		{
			builder.command( ipType == IPv4 ? "ping" : "ping6", address, "-c", "4", "-W", "1" );
		}
		builder.redirectErrorStream( true );

		final Process process = builder.start();

		final int ping = processPing( process );
		process.destroy();

		return ping;
	}

	private static int processPing( final Process process ) throws IOException, TimeoutException
	{
		final List<String> pingOutput = readPingOutput( process );
		return getMinimumPing( pingOutput );
	}

	private static List<String> readPingOutput( final Process process ) throws IOException
	{
		final List<String> pingOutput = new ArrayList<>();

		try( final BufferedReader reader = new BufferedReader( new InputStreamReader( process.getInputStream() ) ) )
		{
			String line;
			while( ( line = reader.readLine() ) != null )
			{
				pingOutput.add( line );
			}
		}

		return pingOutput;
	}

	private static int getMinimumPing( final List<String> pingOutput ) throws IOException, TimeoutException
	{
		int ping = 0;

		if( pingOutput.size() > 0 )
		{
			final String lastLine = pingOutput.get( pingOutput.size() - 1 );

			boolean failureParsing = false;
			int index = lastLine.indexOf( '=' );
			if( index != -1 )
			{
				index += 2;

				final int endIndex;
				if( SystemUtils.IS_OS_WINDOWS )
				{
					endIndex = lastLine.indexOf( 'm', index );
				}
				else
				{
					endIndex = lastLine.indexOf( '/', index );
				}

				if( endIndex != -1 )
				{
					ping = Math.round( Float.parseFloat( lastLine.substring( index, endIndex ) ) );
				}
				else
				{
					failureParsing = true;
				}
			}
			else
			{
				failureParsing = true;
			}

			if( failureParsing )
			{
				throw new TextParseException();
			}
		}
		else
		{
			throw new IOException();
		}

		return ping;
	}
}
