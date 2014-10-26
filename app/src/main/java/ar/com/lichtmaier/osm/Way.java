package ar.com.lichtmaier.osm;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class Way extends Element
{
	private List<Node> nodes;

	public Way(long id)
	{
		super(id);
	}

	public void add(Node node)
	{
		if(nodes == null)
			nodes = new ArrayList<>();
		nodes.add(node);
	}

	public boolean isClosed()
	{
		return nodes.get(0) == nodes.get(nodes.size() - 1);
	}

	@Override
	public void getBoundingBox(Location[] bb)
	{
		double minLat = Double.MAX_VALUE, minLon = Double.MAX_VALUE, maxLat = -Double.MAX_VALUE, maxLon = -Double.MAX_VALUE;
		for(Node node : nodes)
		{
			if(node.lat < minLat)
				minLat = node.lat;
			if(node.lon < minLon)
				minLon = node.lon;
			if(node.lat > maxLat)
				maxLat = node.lat;
			if(node.lon > maxLon)
				maxLon = node.lon;
		}
		bb[0] = new Location("?");
		bb[0].setLatitude(minLat);
		bb[0].setLongitude(minLon);
		bb[1] = new Location("?");
		bb[1].setLatitude(maxLat);
		bb[1].setLongitude(maxLon);
	}
}
