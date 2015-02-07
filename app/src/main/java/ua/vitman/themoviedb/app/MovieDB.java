package ua.vitman.themoviedb.app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieDB {

    private static final String BASE_POSTER_PATH_URL = "http://image.tmdb.org/t/p/w185";

    private String mMovieId;
    private String mTitle;
    private String mReleaseDate;
    private String mPosterPath;
    private double mVoteAverage;
    private int mVoteCount;

    public static MovieDB fromJson(JSONObject jsonObject) {
        MovieDB movie = new MovieDB();
        try {
            // Deserialize json into object fields
            movie.mMovieId = String.valueOf(jsonObject.getInt("id"));
            movie.mTitle = jsonObject.getString("original_title");
            movie.mReleaseDate = jsonObject.getString("release_date");
            movie.mPosterPath = jsonObject.getString("poster_path");
            movie.mVoteAverage = jsonObject.getDouble("vote_average");
            movie.mVoteCount = jsonObject.getInt("vote_count");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return movie;
    }

    // Decodes array of movie json results into movie objects
    public static ArrayList<MovieDB> fromJson(JSONArray jsonArray) {
        ArrayList<MovieDB> movies = new ArrayList<MovieDB>(jsonArray.length());
        // Process each result in json array, decode and convert to movie object
        // object
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject json = null;
            try {
                json = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            MovieDB movie = MovieDB.fromJson(json);
            if (movie != null) {
                movies.add(movie);
            }
        }
        return movies;
    }

    public String getMovieId() {
        return mMovieId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getPosterPathUrl() {
        return BASE_POSTER_PATH_URL + mPosterPath;
    }

    public double getVoteAverage() {
        return mVoteAverage;
    }

    public int getVoteCount() {
        return mVoteCount;
    }
}
