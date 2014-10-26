package ar.com.lichtmaier.nearby;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;

import java.util.Collection;
import java.util.Map;

import ar.com.lichtmaier.osm.Element;
import ar.com.lichtmaier.osm.ElementSpec;


/**
 * An activity representing a list of places. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link PlaceDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link PlaceListFragment} and the item details
 * (if present) is a {@link PlaceDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link PlaceListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class PlaceListActivity extends ActivityWithPlaces
	implements PlaceListFragment.Callbacks
{
	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.d("nearby", "por crear la vista. f=" + getSupportFragmentManager().findFragmentById(0));
		setContentView(R.layout.activity_place_list);
		Log.d("nearby", "vista creada");
		// Show the Up button in the action bar.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		PlaceListFragment placeListFragment = (PlaceListFragment) getSupportFragmentManager()
			.findFragmentById(R.id.place_list);

		if(findViewById(R.id.place_detail_container) != null)
		{
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			placeListFragment.setActivateOnItemClick(true);
		}

		int categoryId = getIntent().getIntExtra("ar.com.lichtmaier.nearby.category", -1);
		if(categoryId == -1)
			throw new RuntimeException("category missing");
		placeListFragment.category = Category.values()[categoryId];
		setTitle(placeListFragment.category.descId);
	}

	@Override
	void onPlacesReceived(Map<Category, Collection<Element>> places)
	{
		PlaceListFragment f = (PlaceListFragment) getSupportFragmentManager().findFragmentById(R.id.place_list);
		if(f != null)
			f.onPlacesReceived(places);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		if(id == android.R.id.home)
		{
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Callback method from {@link PlaceListFragment.Callbacks}
	 * indicating that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(Element element)
	{
		if(mTwoPane)
		{
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putParcelable(PlaceDetailFragment.ARG_ELEMENT_SPEC, new ElementSpec(element));
			PlaceDetailFragment fragment = new PlaceDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
				.replace(R.id.place_detail_container, fragment)
				.commit();
		} else
		{
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, PlaceDetailActivity.class);
			detailIntent.putExtra(PlaceDetailFragment.ARG_ELEMENT_SPEC, new ElementSpec(element));
			startActivity(detailIntent);
		}
	}
}
