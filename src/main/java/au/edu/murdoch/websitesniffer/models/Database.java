/*
 * Copyright (C) 2016 Jordan Wilson
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

package au.edu.murdoch.websitesniffer.models;

public class Database
{
	public static final class Domains
	{
		public static final String TABLE_NAME = "Domains";
		public static final String FIELD_ID = "DomainID";
		public static final String FIELD_URL = "URL";
	}

	public static final class Locations
	{
		public static final String TABLE_NAME = "Locations";
		public static final String FIELD_ID = "LocationID";
		public static final String FIELD_CITY = "City";
		public static final String FIELD_COUNTRY = "Country";
		public static final String FIELD_LATITUDE = "Latitude";
		public static final String FIELD_LONGITUDE = "Longitude";
	}

	public static final class Tests
	{
		public static final String TABLE_NAME = "Tests";
		public static final String FIELD_ID = "TestID";
		public static final String FIELD_FK_DOMAIN_ID = "FK_DomainID";
		public static final String FIELD_TIMESTAMP = "Timestamp";
		public static final String FIELD_FK_LOCATION_ID = "FK_LocationID";
		public static final String FIELD_FK_IPV4_TEST_ID = "FK_IPv4TestID";
		public static final String FIELD_FK_IPV6_TEST_ID = "FK_IPv6TestID";
	}

	public static final class IPv4Tests
	{
		public static final String TABLE_NAME = "IPv4Tests";
		public static final String FIELD_ID = "IPv4TestID";
		public static final String FIELD_ADDRESS = "IPv4Address";
		public static final String FIELD_ADDRESS_PING = "IPv4AddressPing";
		public static final String FIELD_FK_ADDRESS_LOCATION_ID = "FK_IPv4AddressLocationID";
		public static final String FIELD_HTTP_STATUS_CODE = "IPv4HttpStatusCode";
		public static final String FIELD_MX_ADDRESS = "IPv4MXAddress";
		public static final String FIELD_FK_MX_ADDRESS_LOCATION_ID = "FK_IPv4MXAddressLocationID";
		public static final String FIELD_HAS_WORKING_SMTP = "IPv4HasWorkingSMTP";
	}

	public static final class IPv6Tests
	{
		public static final String TABLE_NAME = "IPv6Tests";
		public static final String FIELD_ID = "IPv6TestID";
		public static final String FIELD_ADDRESS = "IPv6Address";
		public static final String FIELD_ADDRESS_PING = "IPv6AddressPing";
		public static final String FIELD_FK_ADDRESS_LOCATION_ID = "FK_IPv6AddressLocationID";
		public static final String FIELD_HTTP_STATUS_CODE = "IPv6HttpStatusCode";
		public static final String FIELD_MX_ADDRESS = "IPv6MXAddress";
		public static final String FIELD_FK_MX_ADDRESS_LOCATION_ID = "FK_IPv6MXAddressLocationID";
		public static final String FIELD_HAS_WORKING_SMTP = "IPv6HasWorkingSMTP";
	}
}
