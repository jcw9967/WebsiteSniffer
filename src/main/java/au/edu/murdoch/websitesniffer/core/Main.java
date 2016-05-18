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
import au.edu.murdoch.websitesniffer.models.Domain;
import static au.edu.murdoch.websitesniffer.models.IPTest.Type.IPv6;
import au.edu.murdoch.websitesniffer.models.IPv4Test;
import au.edu.murdoch.websitesniffer.models.IPv6Test;
import au.edu.murdoch.websitesniffer.models.Location;
import au.edu.murdoch.websitesniffer.models.Test;
import au.edu.murdoch.websitesniffer.util.DatabaseHelper;
import au.edu.murdoch.websitesniffer.util.LocationHelper;
import au.edu.murdoch.websitesniffer.util.Ping;
import au.edu.murdoch.websitesniffer.util.SSLUtilities;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main
{
	private static boolean HAS_IPV6;
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
				.desc( "Specify the number of concurrent threads to use" )
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
					Main.addUrlsFromFile( file );
				}

				if( cli.hasOption( "t" ) )
				{
					mThreadCount = Integer.parseInt( cli.getOptionValue( "t" ) );
				}

				System.out.println( "Determining if IPv6 is available..." );
				HAS_IPV6 = hasIPv6();
				System.out.println( "IPv6 is " + ( HAS_IPV6 ? "" : "NOT " ) + "available!" );

				if( cli.hasOption( "cli" ) )
				{
					Main.CLI();
				}
				else
				{
					java.awt.EventQueue.invokeLater( new Runnable()
					{
						@Override
						public void run()
						{
							try
							{
								UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
							}
							catch( final ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex )
							{
								Logger.getLogger( MainFrame.class.getName() ).log( Level.SEVERE, null, ex );
							}

							new MainFrame().setVisible( true );
						}
					} );
				}
			}
		}
		catch( final ParseException ex )
		{
			Logger.getLogger( MainFrame.class.getName() ).log( Level.SEVERE, null, ex );
		}
	}

	public static void CLI()
	{
		try
		{
			final List<Domain> domains = DatabaseHelper.getAllDomains();
			if( domains.size() > 0 )
			{
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

								if( HAS_IPV6 )
								{
									test.setIPv6Test( new IPv6Test( domain ) );
								}

								DatabaseHelper.insertTest( test );
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
				catch( final InterruptedException e )
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
		try
		{
			//If we can't ping via ipv6, prevent it
			Ping.ping( "google.com", IPv6 );
		}
		catch( final IOException ex )
		{
			return false;
		}

		return true;
	}

	public static void addUrlsFromFile( final File file )
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

			DatabaseHelper.insertDomains( domains );
		}
		catch( final IOException | SQLException ex )
		{
			Logger.getLogger( MainFrame.class.getName() ).log( Level.SEVERE, null, ex );
		}
	}
}
