package ua.vitman.themoviedb.app.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ua.vitman.themoviedb.app.R;
import ua.vitman.themoviedb.app.TheMovieDBHttpClient;
import ua.vitman.themoviedb.app.fragment.MovieFragment;

import java.util.ArrayList;


public class MainActivity extends Activity implements ActionBar.TabListener, AdapterView.OnItemClickListener {

    private static final String LOG_TAG = "log";

    private static final String TAB_POPULAR = "Popular";
    private static final String TAB_UPCOMING = "Upcoming";
    private static final String TAB_TOP = "Top";

    private MovieFragment mMovieFragment;
    private ActionBar mActionBar;

    private ArrayList<String> mMoviesId;
    private JSONObject mJsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initActionBar();
    }

    private void initActionBar() {
        mActionBar = getActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayShowCustomEnabled(true);
            mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

            LayoutInflater inflater = (LayoutInflater) this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.actionbar, null);
            mActionBar.setCustomView(view);

            AutoCompleteTextView autoCompleteMovieSearch = (AutoCompleteTextView) view
                    .findViewById(R.id.ac_movie_search);
            autoCompleteMovieSearch.setAdapter
                    (new MoviesAutoCompleteAdapter(MainActivity.this, android.R.layout.simple_dropdown_item_1line));
            autoCompleteMovieSearch.setOnItemClickListener(MainActivity.this);
            setupTabs();
        }
    }

    private void setupTabs() {
//        init Popular tab
        ActionBar.Tab tab = mActionBar.newTab();
        tab.setText(TAB_POPULAR);
        tab.setTabListener(MainActivity.this);
        mActionBar.addTab(tab);

//        init Upcoming tab
        tab = mActionBar.newTab();
        tab.setText(TAB_UPCOMING);
        tab.setTabListener(MainActivity.this);
        mActionBar.addTab(tab);

//        init Top tab
        tab = mActionBar.newTab();
        tab.setText(TAB_TOP);
        tab.setTabListener(MainActivity.this);
        mActionBar.addTab(tab);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

        String tabsName = (String) tab.getText();

        if (tabsName.equals(TAB_POPULAR)) {
            initMovieFragment(MovieFragment.POPULAR);

        } else if (tabsName.equals(TAB_TOP)) {
            initMovieFragment(MovieFragment.TOP_RATED);

        } else if (tabsName.equals(TAB_UPCOMING)) {
            initMovieFragment(MovieFragment.UPCOMING);
        }

        fragmentTransaction.replace(android.R.id.content, mMovieFragment);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        fragmentTransaction.remove(mMovieFragment);
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    private void initMovieFragment(String sortMovie) {
        mMovieFragment = new MovieFragment();
        mMovieFragment.setSortMovie(sortMovie);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
        Log.i("log", mMoviesId.get(position));
        intent.putExtra(MovieFragment.MOVIE_ID, mMoviesId.get(position));
        startActivity(intent);
    }

    private class MoviesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> resultList;

        public MoviesAutoCompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    private ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;
        ArrayList<String> moviesId = null;
        searchMovieDB(input);
        try {
            JSONArray predsJsonArray = mJsonObject.getJSONArray("results");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            moviesId = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(predsJsonArray.getJSONObject(i).getString("title"));
                moviesId.add(String.valueOf(predsJsonArray.getJSONObject(i).getInt("id")));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }
        mMoviesId = moviesId;
        return resultList;
    }

    private void searchMovieDB(String input) {
        TheMovieDBHttpClient mHttpClient = new TheMovieDBHttpClient();
        mHttpClient.searchMovie(input, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                mJsonObject = response;
            }
        });
    }
}
