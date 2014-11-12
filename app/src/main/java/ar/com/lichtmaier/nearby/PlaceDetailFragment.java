package ar.com.lichtmaier.nearby;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ar.com.lichtmaier.osm.Element;
import ar.com.lichtmaier.osm.ElementSpec;

/**
 * A fragment representing a single place detail screen.
 * This fragment is either contained in a {@link PlaceListActivity}
 * in two-pane mode (on tablets) or a {@link PlaceDetailActivity}
 * on handsets.
 */
public class PlaceDetailFragment extends Fragment
{
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ELEMENT_SPEC = "element_spec";

	/**
	 * The content this fragment is presenting.
	 */
	private Element mItem;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public PlaceDetailFragment()
	{
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if(getArguments().containsKey(ARG_ELEMENT_SPEC))
		{
			mItem = PlacesManager.get((ElementSpec) getArguments().getParcelable(ARG_ELEMENT_SPEC));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
				 Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_place_detail, container, false);

		if(mItem != null)
		{
			((TextView) rootView.findViewById(R.id.place_title)).setText(mItem.toString());
			if(mItem.type != null)
				((TextView) rootView.findViewById(R.id.place_subtitle)).setText(mItem.type.description);

			StringBuilder description = new StringBuilder();

			String value = mItem.getTag("addr:street");
			if(value != null)
			{
				description.append(value.replaceAll(";.*$", ""));
				value = mItem.getTag("addr:housenumber");
				if(value != null)
					description.append(' ').append(value);
				description.append('\n');
			}
			boolean first = true;
			for(String info : mItem.getInformations(getActivity()))
			{
				if(first)
					first = false;
				else
					description.append("; ");
				description.append(info);
			}
			description.append("\n\n").append(mItem.dumpTags());
			((TextView) rootView.findViewById(R.id.place_description)).setText(description);
		}

		return rootView;
	}
}
