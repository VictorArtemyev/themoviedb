package ua.vitman.themoviedb.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MoviesAdapter extends ArrayAdapter<MovieDB> {

    public MoviesAdapter(Context context, ArrayList<MovieDB> aMovies) {
        super(context, 0, aMovies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        MovieDB movie = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.adapter_movie, null);
        }

        // Lookup view for data population
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
        TextView tvVoteAverage = (TextView) convertView.findViewById(R.id.tv_vote_average);
        TextView tvDate = (TextView) convertView.findViewById(R.id.tv_date);
        ImageView ivPosterImage = (ImageView) convertView.findViewById(R.id.iv_poster_image);

        // Populate the data into the template view using the data object
        tvTitle.setText(movie.getTitle());
        tvVoteAverage.setText(movie.getVoteAverage() + " (" + movie.getVoteCount() + " votes)");
        tvDate.setText(movie.getReleaseDate());
        Picasso.with(getContext()).load(movie.getPosterPathUrl()).into(ivPosterImage);

        // Return the completed view to render on screen
        return convertView;
    }
}
