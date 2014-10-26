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
			((TextView) rootView.findViewById(R.id.place_detail)).setText(mItem.toString());
		}

		return rootView;
	}
}
