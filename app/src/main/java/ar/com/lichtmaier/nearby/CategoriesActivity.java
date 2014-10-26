package ar.com.lichtmaier.nearby;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ar.com.lichtmaier.osm.Element;

public class CategoriesActivity extends ActivityWithPlaces
{
	private final Map<View, Category> viewToCategory = new HashMap<>();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_categories);
	}

	@Override
	void onPlacesReceived(Map<Category, Collection<Element>> places)
	{
		LinearLayout categoryList = (LinearLayout) findViewById(R.id.category_list);
		categoryList.removeAllViews();
		viewToCategory.clear();
		for(Category category : Category.values())
		{
			Collection<Element> elements = places.get(category);
			if(elements != null)
			{
				View h = getLayoutInflater().inflate(R.layout.category_header, categoryList, false);
				((TextView)h.findViewById(R.id.description)).setText(getString(category.descId));
				((TextView)h.findViewById(R.id.summary)).setText(getString(R.string.things, elements.size()));
				h.setOnClickListener(categoryClickListener);
				categoryList.addView(h);
				viewToCategory.put(h, category);
			}
		}
		findViewById(R.id.progressBar).setVisibility(View.GONE);
	}

	class CategoryClickListener implements View.OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			Intent i = new Intent(CategoriesActivity.this, PlaceListActivity.class);
			i.putExtra("ar.com.lichtmaier.nearby.category", viewToCategory.get(v).ordinal());
			startActivity(i);
		}
	}
	final private View.OnClickListener categoryClickListener = new CategoryClickListener();

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.categories, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if(id == R.id.action_settings)
		{
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
