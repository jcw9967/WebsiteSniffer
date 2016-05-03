package util;

import java.util.Arrays;
import java.util.List;
import models.Domain;
import org.junit.Test;
import static org.junit.Assert.*;
import util.DatabaseHelper.Domains;

public class DatabaseHelperTest
{
	public DatabaseHelperTest()
	{
	}

	@Test
	public void testGetInstance() throws Exception
	{
		final DatabaseHelper result = DatabaseHelper.getInstance();

		assertNotNull( result );
	}

	@Test
	public void testGetAllDomains() throws Exception
	{
		final List<String> domains = Arrays.asList(
				"www.facebook.com",
				"www.google.com",
				"www.twitter.com"
		);
		DatabaseHelper.getInstance().insertDomains( domains );

		final List<Domain> result = DatabaseHelper.getInstance().getAllDomains();

		assertNotEquals( result.size(), 0 );
	}

	@Test
	public void testInsertDomains() throws Exception
	{
		final List<String> domains = Arrays.asList(
				"www.youtube.com",
				"www.tumblr.com"
		);
		
		DatabaseHelper.getInstance().insertDomains( domains );
	}
	
	@Test
	public void testConflictingDomains() throws Exception
	{
		final List<String> domains = Arrays.asList(
				"www.facebook.com",
				"www.facebook.com"
		);
		DatabaseHelper.getInstance().insertDomains( domains );
		
		final int count = DatabaseHelper.getInstance().count( Domains.TABLE_NAME.toString(), "url = 'www.facebook.com'" );
		assertEquals( count, 1 );
	}

	@Test
	public void testGetLocation() throws Exception
	{
	}

	@Test
	public void testInsertLocation() throws Exception
	{
	}

	@Test
	public void testInsertIPv4Test() throws Exception
	{
	}

	@Test
	public void testInsertIPv6Test() throws Exception
	{
	}
}
