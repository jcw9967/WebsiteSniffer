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

package au.edu.murdoch.websitesniffer.util;

import au.edu.murdoch.websitesniffer.models.Database.*;

public class SQLStatements
{
	public static final String CREATE_TABLE_DOMAINS = "CREATE TABLE IF NOT EXISTS " + Domains.TABLE_NAME + "("
			+ Domains.FIELD_ID + " INTEGER,"
			+ Domains.FIELD_URL + " TEXT NOT NULL,"
			+ "PRIMARY KEY(" + Domains.FIELD_ID + "),"
			+ "UNIQUE(" + Domains.FIELD_URL + ") ON CONFLICT IGNORE"
			+ ")";

	public static final String CREATE_TABLE_LOCATIONS = "CREATE TABLE IF NOT EXISTS " + Locations.TABLE_NAME + "("
			+ Locations.FIELD_ID + " INTEGER,"
			+ Locations.FIELD_CITY + " TEXT,"
			+ Locations.FIELD_COUNTRY + " TEXT,"
			+ Locations.FIELD_LATITUDE + " REAL,"
			+ Locations.FIELD_LONGITUDE + " REAL,"
			+ "PRIMARY KEY(" + Locations.FIELD_ID + "),"
			+ "UNIQUE(" + Locations.FIELD_CITY + ',' + Locations.FIELD_COUNTRY + ") ON CONFLICT IGNORE"
			+ ")";

	public static final String CREATE_TABLE_IPV4TESTS = "CREATE TABLE IF NOT EXISTS " + IPv4Tests.TABLE_NAME + "("
			+ IPv4Tests.FIELD_ID + " INTEGER,"
			+ IPv4Tests.FIELD_ADDRESS + " TEXT,"
			+ IPv4Tests.FIELD_ADDRESS_PING + " INTEGER,"
			+ IPv4Tests.FIELD_FK_ADDRESS_LOCATION_ID + " INTEGER,"
			+ IPv4Tests.FIELD_HTTP_STATUS_CODE + " INTEGER,"
			+ IPv4Tests.FIELD_MX_ADDRESS + " TEXT,"
			+ IPv4Tests.FIELD_FK_MX_ADDRESS_LOCATION_ID + " INTEGER,"
			+ IPv4Tests.FIELD_HAS_WORKING_SMTP + " INTEGER,"
			+ "FOREIGN KEY(" + IPv4Tests.FIELD_FK_ADDRESS_LOCATION_ID + ") REFERENCES " + Locations.TABLE_NAME + "(" + Locations.FIELD_ID + "),"
			+ "FOREIGN KEY(" + IPv4Tests.FIELD_FK_MX_ADDRESS_LOCATION_ID + ") REFERENCES " + Locations.TABLE_NAME + "(" + Locations.FIELD_ID + "),"
			+ "PRIMARY KEY(" + IPv4Tests.FIELD_ID + ")"
			+ ")";

	public static final String CREATE_TABLE_IPV6TESTS = "CREATE TABLE IF NOT EXISTS " + IPv6Tests.TABLE_NAME + "("
			+ IPv6Tests.FIELD_ID + " INTEGER,"
			+ IPv6Tests.FIELD_ADDRESS + " TEXT,"
			+ IPv6Tests.FIELD_ADDRESS_PING + " INTEGER,"
			+ IPv6Tests.FIELD_FK_ADDRESS_LOCATION_ID + " INTEGER,"
			+ IPv6Tests.FIELD_HTTP_STATUS_CODE + " INTEGER,"
			+ IPv6Tests.FIELD_MX_ADDRESS + " TEXT,"
			+ IPv6Tests.FIELD_FK_MX_ADDRESS_LOCATION_ID + " INTEGER,"
			+ IPv6Tests.FIELD_HAS_WORKING_SMTP + " INTEGER,"
			+ "FOREIGN KEY(" + IPv6Tests.FIELD_FK_ADDRESS_LOCATION_ID + ") REFERENCES " + Locations.TABLE_NAME + "(" + Locations.FIELD_ID + "),"
			+ "FOREIGN KEY(" + IPv6Tests.FIELD_FK_MX_ADDRESS_LOCATION_ID + ") REFERENCES " + Locations.TABLE_NAME + "(" + Locations.FIELD_ID + "),"
			+ "PRIMARY KEY(" + IPv6Tests.FIELD_ID + " )"
			+ ")";

	public static final String CREATE_TABLE_TESTS = "CREATE TABLE IF NOT EXISTS " + Tests.TABLE_NAME + "("
			+ Tests.FIELD_ID + " INTEGER,"
			+ Tests.FIELD_FK_DOMAIN_ID + " INTEGER,"
			+ Tests.FIELD_TIMESTAMP + " INTEGER NOT NULL,"
			+ Tests.FIELD_FK_LOCATION_ID + " INTEGER NOT NULL,"
			+ Tests.FIELD_FK_IPV4_TEST_ID + " INTEGER,"
			+ Tests.FIELD_FK_IPV6_TEST_ID + " INTEGER,"
			+ "FOREIGN KEY(" + Tests.FIELD_FK_DOMAIN_ID + ") REFERENCES " + Domains.TABLE_NAME + "(" + Domains.FIELD_ID + "),"
			+ "FOREIGN KEY(" + Tests.FIELD_FK_LOCATION_ID + ") REFERENCES " + Locations.TABLE_NAME + "(" + Locations.FIELD_ID + "),"
			+ "FOREIGN KEY(" + Tests.FIELD_FK_IPV4_TEST_ID + ") REFERENCES " + IPv4Tests.TABLE_NAME + "(" + IPv4Tests.FIELD_ID + "),"
			+ "FOREIGN KEY(" + Tests.FIELD_FK_IPV6_TEST_ID + ") REFERENCES " + IPv6Tests.TABLE_NAME + "(" + IPv6Tests.FIELD_ID + "),"
			+ "PRIMARY KEY(" + Tests.FIELD_ID + ')'
			+ ")";

	public static final String GET_ALL_DOMAINS = "SELECT * FROM " + Domains.TABLE_NAME;

	public static final String INSERT_DOMAIN = "INSERT INTO " + Domains.TABLE_NAME + "("
			+ Domains.FIELD_URL
			+ ") VALUES (?)";

	public static final String GET_LOCATION_FROM_COMPONENTS = "SELECT "
			+ Locations.FIELD_ID + ','
			+ Locations.FIELD_CITY + ','
			+ Locations.FIELD_COUNTRY
			+ " FROM "
			+ Locations.TABLE_NAME
			+ " WHERE "
			+ Locations.FIELD_CITY + "=? AND "
			+ Locations.FIELD_COUNTRY + "=?"
			+ " LIMIT 1";

	public static final String INSERT_LOCATION = "INSERT INTO " + Locations.TABLE_NAME + "("
			+ Locations.FIELD_CITY + ','
			+ Locations.FIELD_COUNTRY + ','
			+ Locations.FIELD_LATITUDE + ','
			+ Locations.FIELD_LONGITUDE
			+ ") VALUES (?,?,?,?)";

	public static final String INSERT_TEST = "INSERT INTO " + Tests.TABLE_NAME + "("
			+ Tests.FIELD_FK_DOMAIN_ID + ','
			+ Tests.FIELD_TIMESTAMP + ','
			+ Tests.FIELD_FK_LOCATION_ID + ','
			+ Tests.FIELD_FK_IPV4_TEST_ID + ','
			+ Tests.FIELD_FK_IPV6_TEST_ID
			+ ") VALUES (?,?,?,?,?)";

	public static final String INSERT_IPV4TEST = "INSERT INTO " + IPv4Tests.TABLE_NAME + "("
			+ IPv4Tests.FIELD_ADDRESS + ','
			+ IPv4Tests.FIELD_ADDRESS_PING + ','
			+ IPv4Tests.FIELD_FK_ADDRESS_LOCATION_ID + ','
			+ IPv4Tests.FIELD_HTTP_STATUS_CODE + ','
			+ IPv4Tests.FIELD_MX_ADDRESS + ','
			+ IPv4Tests.FIELD_FK_MX_ADDRESS_LOCATION_ID + ','
			+ IPv4Tests.FIELD_HAS_WORKING_SMTP
			+ ") VALUES (?,?,?,?,?,?,?)";

	public static final String INSERT_IPV6TEST = "INSERT INTO " + IPv6Tests.TABLE_NAME + "("
			+ IPv6Tests.FIELD_ADDRESS + ','
			+ IPv6Tests.FIELD_ADDRESS_PING + ','
			+ IPv6Tests.FIELD_FK_ADDRESS_LOCATION_ID + ','
			+ IPv6Tests.FIELD_HTTP_STATUS_CODE + ','
			+ IPv6Tests.FIELD_MX_ADDRESS + ','
			+ IPv6Tests.FIELD_FK_MX_ADDRESS_LOCATION_ID + ','
			+ IPv6Tests.FIELD_HAS_WORKING_SMTP
			+ ") VALUES (?,?,?,?,?,?,?)";
}
