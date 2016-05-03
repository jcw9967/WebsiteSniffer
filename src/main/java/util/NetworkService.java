package util;

import models.json.LocationJson;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface NetworkService
{
	public static final String GEOIP_BASE_URL = "http://geoip.nekudo.com/api/";

	@GET( "{ip}" )
	Call<LocationJson> getLocationByIP( @Path( "ip" ) final String ip );
}
