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
package au.edu.murdoch.websitesniffer.core;

import au.edu.murdoch.websitesniffer.gui.MainFrame;
import au.edu.murdoch.websitesniffer.models.*;
import au.edu.murdoch.websitesniffer.util.DatabaseHelper;
import au.edu.murdoch.websitesniffer.util.LocationHelper;
import au.edu.murdoch.websitesniffer.util.Ping;
import au.edu.murdoch.websitesniffer.util.SSLUtilities;
import org.apache.commons.cli.*;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static au.edu.murdoch.websitesniffer.models.IPTest.Type.IPv6;

public class Main
{
	private static String mOutputFilename = "sniffer.db";
	private static Boolean HAS_IPV6;
	private static int mTestCount = 0;
	private static int mThreadCount = 50;

	public static void main( final String args[] )
	{
		SSLUtilities.trustAllHostnames();
		SSLUtilities.trustAllHttpsCertificates();

		final Options options = new Options();
		options.addOption( "cli", "Use the command line interface" );
		options.addOption( Option.builder( "f" )
				.longOpt( "urlfile" )
				.hasArg()
				.argName( "FILE" )
				.desc( "Add URLs from a given file" )
				.build()
		);
		options.addOption( Option.builder( "t" )
				.longOpt( "threads" )
				.hasArg()
				.argName( "COUNT" )
				.desc( "Specify the number of concurrent threads to use. Default = 50" )
				.build()
		);
		options.addOption( Option.builder( "o" )
				.longOpt( "output" )
				.hasArg()
				.argName( "FILENAME" )
				.desc( "Define the output filename of the SQLite database" )
				.build()
		);
		options.addOption( "h", "help", false, "Display this help menu" );

		try
		{
			final CommandLine cli = new DefaultParser().parse( options, args );
			if( cli.hasOption( "help" ) )
			{
				final HelpFormatter helpFormatter = new HelpFormatter();
				helpFormatter.printHelp( "[options]", options );
			}
			else
			{
				if( cli.hasOption( "urlfile" ) )
				{
					final File file = new File( cli.getOptionValue( "urlfile" ) );
					System.out.println( "Reading URLs from " + file.getAbsolutePath() + "..." );
					try
					{
						Main.addUrlsFromFile( file );
					}
					catch( final SQLException | IOException ex )
					{
						Logger.getLogger( Main.class.getName() ).log( Level.SEVERE, ex.getMessage(), ex );
						return;
					}
				}

				if( cli.hasOption( "output" ) )
				{
					mOutputFilename = cli.getOptionValue( "output" );
				}

				if( cli.hasOption( "threads" ) )
				{
					mThreadCount = Integer.parseInt( cli.getOptionValue( "threads" ) );
					if( mThreadCount <= 0 )
					{
						return;
					}
				}

				if( cli.hasOption( "cli" ) )
				{
					Main.CLI();
				}
				else
				{
					try
					{
						UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
					}
					catch( final ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex )
					{
						Logger.getLogger( MainFrame.class.getName() ).log( Level.SEVERE, ex.getMessage(), ex );
					}

					new MainFrame();
				}
			}
		}
		catch( final ParseException ex )
		{
			Logger.getLogger( MainFrame.class.getName() ).log( Level.SEVERE, null, ex );
		}
	}

	public static String getOutputFilename()
	{
		return mOutputFilename;
	}

	private static void CLI()
	{
		try
		{
			final List<Domain> domains = DatabaseHelper.getInstance().getAllDomains();
			if( domains.size() > 0 )
			{
				System.out.println( "IPv6 is " + ( hasIPv6() ? "" : "NOT " ) + "available" );

				//Get user's location
				System.out.println( "Getting your location..." );
				final Location userLocation = LocationHelper.getLocationForHost();

				final ExecutorService executor = Executors.newFixedThreadPool( mThreadCount );
				System.out.println( domains.size() + " URLs available. Beginning tests..." );
				for( int i = 0; i < domains.size(); ++i )
				{
					final int testNumber = i;
					final Domain domain = domains.get( i );
					executor.execute( new Runnable()
					{
						@Override
						public void run()
						{
							if( testNumber < mThreadCount )
							{
								try
								{
									Thread.sleep( new Random().nextInt( 999 ) );
								}
								catch( final InterruptedException ex )
								{
									Logger.getLogger( Main.class.getName() ).log( Level.SEVERE, null, ex );
								}
							}

							try
							{
								final Test test = new Test( domain, userLocation );
								test.setIPv4Test( new IPv4Test( domain ) );

								if( hasIPv6() )
								{
									test.setIPv6Test( new IPv6Test( domain ) );
								}

								DatabaseHelper.getInstance().insertTest( test );
								Logger.getLogger( MainFrame.class.getName() ).log( Level.INFO, "Finished " + ( ++mTestCount ) + " / " + domains.size() + " : " + domain.getUrl() );
							}
							catch( final SQLException ex )
							{
								Logger.getLogger( MainFrame.class.getName() ).log( Level.SEVERE, ex.getMessage(), ex );
							}
						}
					} );
				}

				//Hack to block the main thread until finished
				executor.shutdown();
				try
				{
					executor.awaitTermination( Long.MAX_VALUE, TimeUnit.DAYS );
				}
				catch( final InterruptedException ignored )
				{
				}
			}
			else
			{
				System.out.println( "There are no URLs to test! Did you use the '-urlfile <FILE>' argument?" );
			}
		}
		catch( final SQLException ex )
		{
			Logger.getLogger( MainFrame.class.getName() ).log( Level.SEVERE, null, ex );
		}
	}

	public static boolean hasIPv6()
	{
		if( HAS_IPV6 == null )
		{
			try
			{
				//If we can't ping via ipv6, prevent it
				Ping.ping( "google.com", IPv6 );
				HAS_IPV6 = true;
			}
			catch( final TimeoutException | IOException ex )
			{
				HAS_IPV6 = false;
			}
		}

		return HAS_IPV6;
	}

	public static void addUrlsFromFile( final File file ) throws IOException, SQLException
	{
		try( final FileReader fileReader = new FileReader( file );
			 final BufferedReader reader = new BufferedReader( fileReader ) )
		{
			final List<String> domains = new ArrayList<>();

			String line;
			while( ( line = reader.readLine() ) != null )
			{
				domains.add( line );
			}

			DatabaseHelper.getInstance().insertDomains( domains );
		}
	}
}
