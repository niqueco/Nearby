package ar.com.lichtmaier.osm;

import android.content.Context;
import android.location.Location;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GlobalCoordinates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ar.com.lichtmaier.nearby.PlaceType;
import ar.com.lichtmaier.nearby.R;

abstract public class Element
{
	final public long id;

	private Map<String, String> tags;
	public PlaceType type;

	public Element(long id)
	{
		this.id = id;
	}

	public void setTag(String tag, String value)
	{
		if(tags == null)
			tags = new HashMap<>();
		tags.put(tag, value);
	}

	public String getTag(String tag)
	{
		return tags == null ? null : tags.get(tag);
	}

	public boolean hasTag(String tag)
	{
		return tags != null && tags.containsKey(tag);
	}

	public String dumpTags()
	{
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for(Map.Entry<String, String> e : tags.entrySet())
		{
			if(first)
				first = false;
			else
				sb.append('\n');
			sb.append(e.getKey()).append('=').append(e.getValue());
		}
		return sb.toString();
	}

	public abstract void getBoundingBox(Location[] bb);

	public Location[] getBoundingBox()
	{
		Location[] bb = new Location[2];
		getBoundingBox(bb);
		return bb;
	}

	public Location getCenter()
	{
		Location[] bb = getBoundingBox();
		Location l = new Location("?");
		l.setLatitude((bb[0].getLatitude() + bb[1].getLatitude()) / 2.0);
		l.setLongitude((bb[0].getLongitude() + bb[1].getLongitude()) / 2.0);
		return l;
	}

	@Override
	public String toString()
	{
		String name = getTag("name");
		if(name != null)
			return name;
		return '<' + getClass().getSimpleName() + ' ' + id + '>';
	}

	private transient double lastLon = Double.MAX_VALUE, lastLat = Double.MAX_VALUE, dist;
	public synchronized double distanceTo(Location loc)
	{
		Location center = getCenter();
		double latitude = loc.getLatitude();
		double longitude = loc.getLongitude();
		if(latitude != lastLat || longitude != lastLon)
		{
			GlobalCoordinates coords = new GlobalCoordinates(latitude, longitude);
			dist = GeodeticCalculator.calculateGeodeticCurve(Ellipsoid.WGS84, new GlobalCoordinates(center.getLatitude(), center.getLongitude()), coords).getEllipsoidalDistance();
			lastLat = latitude;
			lastLon = longitude;
		}
		return dist;
	}

	public Iterable<? extends String> getInformations(Context ctx)
	{
		ArrayList<String> l = new ArrayList<>();
		int str = 0;
		String v = getTag("cuisine");
		if(v != null) switch(v)
		{
			case "chinese":
				str = R.string.cuisine_chinese;
				break;
			case "french":
				str = R.string.cuisine_french;
				break;
			case "indian":
				str = R.string.cuisine_indian;
				break;
			case "italian":
				str = R.string.cuisine_italian;
				break;
			case "japanese":
				str = R.string.cuisine_japanese;
				break;
			case "kebab":
				str = R.string.cuisine_kebab;
				break;
			case "mexican":
				str = R.string.cuisine_mexican;
				break;
			case "pizza":
				str = R.string.cuisine_pizza;
				break;
			case "sandwich":
				str = R.string.cuisine_sandwich;
				break;
			case "sushi":
				str = R.string.cuisine_sushi;
				break;
			case "thai":
				str = R.string.cuisine_thai;
				break;
		}
		if(str != 0)
		{
			l.add(ctx.getString(R.string.cuisine) + ": " + ctx.getString(str));
			str = 0;
		}
		v = getTag("internet_access");
		String v2;
		if(v != null) switch(v)
		{
			case "wlan":
				v2 = getTag("internet_access:fee");
				str = v2 == null ? R.string.wifi_available
					: "no".equals(v2) ? R.string.free_wifi_available
						: R.string.paid_wifi_available;
				break;
			case "yes":
				str = R.string.internet_available;
				break;
		}
		if(str != 0)
			l.add(ctx.getString(str));
		return l;
	}
}
