package ua.vitman.themoviedb.app;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class TheMovieDBHttpClient {

    private final String API_KEY = "dbb5c2493cdc33fea9f42ae21f358a91";
    private final String BASE_URL = "http://api.themoviedb.org/3/movie";
    private final String BASE_SEARCH_URL = "http://api.themoviedb.org/3/search/movie";

    private AsyncHttpClient mAsyncHttpClient;

    public TheMovieDBHttpClient() {
        this.mAsyncHttpClient = new AsyncHttpClient();
    }

    public void getMovies(String sortMovie, JsonHttpResponseHandler handler) {
        String url = getApiUrl(sortMovie);
        RequestParams params = new RequestParams("api_key", API_KEY);
        mAsyncHttpClient.get(url, params, handler);
    }

    public void searchMovie(String query, JsonHttpResponseHandler handler) {
        RequestParams params = new RequestParams("api_key", API_KEY);
        params.put("query", query);
        mAsyncHttpClient.get(BASE_SEARCH_URL, params, handler);
    }

    private String getApiUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
