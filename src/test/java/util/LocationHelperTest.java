package util;

import models.Location;
import org.junit.Test;
import static org.junit.Assert.*;

public class LocationHelperTest
{
	public LocationHelperTest()
	{
	}

	@Test
	public void testGetLocationByIP()
	{
		final Location location = LocationHelper.getLocationByIP( "8.8.8.8" );
		
		assertNotNull( location );
		assertNotNull( location.getId() );
		assertEquals( location.getCity(), "Mountain View" );
		assertEquals( location.getCountry(), "United States" );
		assertNotEquals( location.getLatitude(), 0 );
		assertNotEquals( location.getLongitude(), 0 );
	}
}
