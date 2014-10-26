package ar.com.lichtmaier.nearby;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Collection;
import java.util.Map;

import ar.com.lichtmaier.osm.Element;

public abstract class ActivityWithPlaces extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener
{
	protected GoogleApiClient client;
	private LocationRequest req;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		client = new GoogleApiClient.Builder(this)
			.addApi(LocationServices.API)
			.addConnectionCallbacks(this)
			.useDefaultAccount()
			//.enableAutoManage(this, 0, this)
			.build();
		req = new LocationRequest().setFastestInterval(10000).setInterval(1000 * 60).setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		client.connect();
	}

	@Override
	protected void onStop()
	{
		if(client.isConnected())
		{
			LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
			client.disconnect();
		}
		super.onStop();
	}

	@Override
	public void onConnected(Bundle bundle)
	{
		Location loc = LocationServices.FusedLocationApi.getLastLocation(client);
		Log.d("neaby", "loc=" + loc);
		if(loc != null)
			onNewLocation(loc);
		LocationServices.FusedLocationApi.requestLocationUpdates(client, req, this);
	}

	protected void onNewLocation(final Location loc)
	{
		new AsyncTask<Void, Void, Map<Category, Collection<Element>>>()
		{
			@Override
			protected Map<Category, Collection<Element>> doInBackground(Void... a)
			{
				return PlacesManager.around(ActivityWithPlaces.this, loc);
			}

			@Override
			protected void onPostExecute(Map<Category, Collection<Element>> places)
			{
				onPlacesReceived(places);
			}
		}.execute();
	}

	abstract void onPlacesReceived(Map<Category, Collection<Element>> places);

	@Override
	public void onConnectionSuspended(int i)
	{

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult)
	{
		Log.e("nearby", "fallo " + connectionResult);
	}

	@Override
	public void onLocationChanged(Location location)
	{
		onNewLocation(location);
	}
}
