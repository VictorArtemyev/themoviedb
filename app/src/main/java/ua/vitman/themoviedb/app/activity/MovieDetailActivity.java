package ua.vitman.themoviedb.app.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import ua.vitman.themoviedb.app.R;
import ua.vitman.themoviedb.app.TheMovieDBHttpClient;
import ua.vitman.themoviedb.app.fragment.MovieFragment;

import java.text.NumberFormat;
import java.util.Locale;

public class MovieDetailActivity extends Activity {

    private static final String BASE_POSTER_PATH_URL = "http://image.tmdb.org/t/p/w396";

    private String mMovieId;
    private String mTitle;
    private String mPosterImageUrl;
    private String mVoteAverage;
    private String mStatus;
    private String mRuntime;
    private String mBudget;
    private String mRevenue;
    private String mOverview;
    private String mTagline;

    private ImageView mIvPosterImage;
    private TextView mTvTitle;
    private TextView mTvFieldVoteAverage;
    private TextView mTvFieldStatus;
    private TextView mTvFieldRuntime;
    private TextView mTvFieldBudget;
    private TextView mTvFieldRevenue;
    private TextView mTvFieldOverview;
    private TextView mTvFieldTagline;

    private ProgressDialog pDialog;
    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Intent intent = getIntent();
        if (intent != null) {
            mMovieId = "/" + intent.getStringExtra(MovieFragment.MOVIE_ID);
        }
        initViews();
        fetchMovies();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        mActionBar = getActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mIvPosterImage = (ImageView) findViewById(R.id.iv_poster_image);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvFieldVoteAverage = (TextView) findViewById(R.id.tv_vote_average);
        mTvFieldStatus = (TextView) findViewById(R.id.tv_field_status);
        mTvFieldRuntime = (TextView) findViewById(R.id.tv_field_runtime);
        mTvFieldBudget = (TextView) findViewById(R.id.tv_field_budget);
        mTvFieldRevenue = (TextView) findViewById(R.id.tv_field_revenue);
        mTvFieldOverview = (TextView) findViewById(R.id.tv_field_overview);
        mTvFieldTagline = (TextView) findViewById(R.id.tv_field_tagline);
    }

    private void setupDetailMovieFromJSON(JSONObject jsonObject) {
        // Deserialize json into object fields
        try {
            mTitle = jsonObject.getString("title");
            mVoteAverage = jsonObject.getDouble("vote_average") +
                    " (" + jsonObject.getInt("vote_count") + " votes)";
            mStatus = jsonObject.getString("status");
            mRuntime = String.valueOf(jsonObject.getInt("runtime"));
            mBudget = "$" + NumberFormat.getNumberInstance(Locale.US).
                    format(jsonObject.getInt("budget"));
            mRevenue = "$" + NumberFormat.getNumberInstance(Locale.US).
                    format(jsonObject.getInt("revenue"));
            mOverview = jsonObject.getString("overview");
            mTagline = jsonObject.getString("tagline");
            mPosterImageUrl = getPosterPathUrl(jsonObject.getString("poster_path"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setupDetailMovieOnLayout() {
        mActionBar.setTitle(mTitle);
        Picasso.with(getApplicationContext()).load(mPosterImageUrl).into(mIvPosterImage);
        mTvTitle.setText(mTitle);
        mTvFieldVoteAverage.setText(mVoteAverage);
        mTvFieldStatus.setText(mStatus);
        mTvFieldRuntime.setText(mRuntime);
        mTvFieldBudget.setText(mBudget);
        mTvFieldRevenue.setText(mRevenue);
        mTvFieldOverview.setText(mOverview);
        mTvFieldTagline.setText(mTagline);
    }

    private void fetchMovies() {
        pDialog = new ProgressDialog(MovieDetailActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();
        TheMovieDBHttpClient mHttpClient = new TheMovieDBHttpClient();
        mHttpClient.getMovies(mMovieId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                setupDetailMovieFromJSON(response);
                setupDetailMovieOnLayout();

                if (pDialog.isShowing()){
                    pDialog.dismiss();
                }
            }
        });
    }

    private String getPosterPathUrl(String imagePath) {
        return BASE_POSTER_PATH_URL + imagePath;
    }
}
