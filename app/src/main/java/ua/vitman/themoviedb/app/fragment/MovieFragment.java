package ua.vitman.themoviedb.app.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ua.vitman.themoviedb.app.MovieDB;
import ua.vitman.themoviedb.app.MoviesAdapter;
import ua.vitman.themoviedb.app.R;
import ua.vitman.themoviedb.app.TheMovieDBHttpClient;
import ua.vitman.themoviedb.app.activity.MainActivity;
import ua.vitman.themoviedb.app.activity.MovieDetailActivity;

import java.util.ArrayList;

public class MovieFragment extends android.app.Fragment {

    public static final String POPULAR = "/popular";
    public static final String TOP_RATED = "/top_rated";
    public static final String UPCOMING = "/upcoming";

    private ArrayList<MovieDB> mMovies;
    private ListView mListViewMovies;
    private MoviesAdapter mAdapterMovies;
    private TheMovieDBHttpClient mHttpClient;
    private String mSortMovie;

    private ProgressDialog pDialog;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movies, null);
    }

    @Override
    public void onStart() {
        super.onStart();

        mMovies = new ArrayList<MovieDB>();
        mListViewMovies = (ListView) getActivity().findViewById(R.id.lv_movies);
        mAdapterMovies = new MoviesAdapter(getActivity(), mMovies);

        mListViewMovies.setAdapter(mAdapterMovies);
        fetchMovies();
        setupMovieSelectedListener();
    }

    private void fetchMovies() {

        mHttpClient = new TheMovieDBHttpClient();
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        mHttpClient.getMovies(mSortMovie, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray items = null;
                try {
                    if (pDialog.isShowing()) {
                        pDialog.dismiss();
                    }
                    // Get the movies json array
                    items = response.getJSONArray("results");
                    ArrayList<MovieDB> movies = MovieDB.fromJson(items);
                    // Load model objects into the adapter which displays them
                    mAdapterMovies.addAll(movies);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setupMovieSelectedListener() {
        mListViewMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View item, int position, long rowId) {
                Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                intent.putExtra(MainActivity.MOVIE_ID,
                        mAdapterMovies.getItem(position).getMovieId());
                startActivity(intent);
            }
        });
    }

    public void setSortMovie(String sortMovie) {
        this.mSortMovie = sortMovie;
    }
}
