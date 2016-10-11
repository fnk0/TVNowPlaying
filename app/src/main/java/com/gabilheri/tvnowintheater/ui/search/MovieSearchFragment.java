package com.gabilheri.tvnowintheater.ui.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.app.SearchFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.ObjectAdapter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;

import com.gabilheri.tvnowintheater.App;
import com.gabilheri.tvnowintheater.Config;
import com.gabilheri.tvnowintheater.R;
import com.gabilheri.tvnowintheater.data.Api.TheMovieDbAPI;
import com.gabilheri.tvnowintheater.data.models.Movie;
import com.gabilheri.tvnowintheater.data.models.MovieResponse;
import com.gabilheri.tvnowintheater.ui.details.MovieDetailsActivity;
import com.gabilheri.tvnowintheater.ui.details.MovieDetailsFragment;
import com.gabilheri.tvnowintheater.ui.movie.MovieCardView;
import com.gabilheri.tvnowintheater.ui.movie.MoviePresenter;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 10/10/16.
 */

public class MovieSearchFragment extends SearchFragment implements SearchFragment.SearchResultProvider,
        OnItemViewClickedListener, OnItemViewSelectedListener {

    @Inject
    TheMovieDbAPI mTheMovieDB;

    private static final boolean FINISH_ON_RECOGNIZER_CANCELED = true;
    private static final int REQUEST_SPEECH = 0x00000010;
    private ArrayObjectAdapter mAdapter;
    private String mQuery;
    private boolean mResultsFound = false;

    public static MovieSearchFragment newInstance() {
        Bundle args = new Bundle();
        MovieSearchFragment fragment = new MovieSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.instance().appComponent().inject(this);

        mAdapter = new ArrayObjectAdapter(new ListRowPresenter());

        setSearchResultProvider(this);
        setOnItemViewSelectedListener(this);
        setOnItemViewClickedListener(this);
    }

    @Override
    public ObjectAdapter getResultsAdapter() {
        return mAdapter;
    }

    @Override
    public boolean onQueryTextChange(String newQuery) {
        search(newQuery);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        search(query);
        return true;
    }

    private void search(String query) {
        if (!query.equals(mQuery)) {
            mQuery = query;
            mTheMovieDB.searchMovies(query, Config.API_KEY_URL)
                    .debounce(500, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::bindMovieResponse, e-> {
                        Timber.e(e, "Error searching movies: %s", e.getMessage());
                    });
        }
    }

    private void bindMovieResponse(MovieResponse response) {
        mAdapter.clear();
        mResultsFound = !response.getResults().isEmpty();
        ArrayObjectAdapter moviesAdapter = new ArrayObjectAdapter(new MoviePresenter());
        for(Movie m : response.getResults()) {
            if (m.getPosterPath() != null) {
                moviesAdapter.add(m);
            }
        }
        mAdapter.add(0, new ListRow(new HeaderItem(0, mQuery), moviesAdapter));
    }

    public boolean hasResults() {
        return mAdapter.size() > 0 && mResultsFound;
    }

    public void focusOnSearch() {
        if (getView() != null) {
            getView().findViewById(R.id.lb_search_bar).requestFocus();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_SPEECH:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        setSearchQuery(data, true);
                        break;
                    default:
                        // If recognizer is canceled or failed, keep focus on the search orb
                        if (FINISH_ON_RECOGNIZER_CANCELED) {
                            if (!hasResults()) {
                                if (getView() != null) {
                                    getView().findViewById(R.id.lb_search_bar_speech_orb).requestFocus();
                                }
                            }
                        }
                        break;
                }
                break;
        }
    }

    @Override
    public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        Movie movie = (Movie) item;
        if (getActivity() instanceof SearchActivity && movie != null) {
            ((SearchActivity) getActivity()).changeBackground(movie.getBackdropPath());
        }
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (item instanceof Movie) {
            Movie movie = (Movie) item;
            Intent i = new Intent(getActivity(), MovieDetailsActivity.class);
            i.putExtra(Movie.class.getSimpleName(), movie);
            if (itemViewHolder.view instanceof MovieCardView) {
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        ((MovieCardView) itemViewHolder.view).getPosterIV(),
                        MovieDetailsFragment.TRANSITION_NAME).toBundle();
                getActivity().startActivity(i, bundle);
            } else {
                startActivity(i);
            }
        }
    }
}
