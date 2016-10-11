package com.gabilheri.tvnowintheater.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;

import com.gabilheri.tvnowintheater.App;
import com.gabilheri.tvnowintheater.Config;
import com.gabilheri.tvnowintheater.R;
import com.gabilheri.tvnowintheater.dagger.modules.HttpClientModule;
import com.gabilheri.tvnowintheater.data.Api.TheMovieDbAPI;
import com.gabilheri.tvnowintheater.data.models.Movie;
import com.gabilheri.tvnowintheater.data.models.MovieResponse;
import com.gabilheri.tvnowintheater.ui.base.GlideBackgroundManager;
import com.gabilheri.tvnowintheater.ui.details.MovieDetailsActivity;
import com.gabilheri.tvnowintheater.ui.details.MovieDetailsFragment;
import com.gabilheri.tvnowintheater.ui.movie.MovieCardView;
import com.gabilheri.tvnowintheater.ui.movie.MoviePresenter;
import com.gabilheri.tvnowintheater.ui.search.SearchActivity;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 10/8/16.
 */

public class MainFragment extends BrowseFragment implements OnItemViewClickedListener, OnItemViewSelectedListener {

    @Inject
    TheMovieDbAPI mDbAPI;

    private static final int NOW_PLAYING = 0;
    private static final int TOP_RATED = 1;
    private static final int POPULAR = 2;
    private static final int UPCOMING = 3;

    private CompositeSubscription mCompositeSubscription;
    private GlideBackgroundManager mBackgroundManager;
    private ArrayObjectAdapter mNowPlayingAdapter;
    private ArrayObjectAdapter mPopularAdapter;
    private ArrayObjectAdapter mTopRatedAdapter;
    private ArrayObjectAdapter mUpcomingAdapter;

    int mNowPlayingPage = 1;
    int mPopularPage = 1;
    int mTopRatedPage = 1;
    int mUpcomingPage = 1;

    public static MainFragment newInstance() {
        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        App.instance().appComponent().inject(this);
        mBackgroundManager = new GlideBackgroundManager(getActivity());
        setBrandColor(ContextCompat.getColor(getActivity(), R.color.blue));

        setOnSearchClickedListener(v -> {
            startActivity(new Intent(getActivity(), SearchActivity.class));
        });

        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);
        setBadgeDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.powered_by));
        createRows();
        prepareEntranceTransition();
        fetchNowPlayingMovies();
        fetchTopRatedMovies();
        fetchPopularMovies();
        fetchUpcomingMovies();
    }

    private void createRows() {
        ArrayObjectAdapter rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        MoviePresenter moviePresenter = new MoviePresenter();
        mNowPlayingAdapter = new ArrayObjectAdapter(moviePresenter);
        mTopRatedAdapter = new ArrayObjectAdapter(moviePresenter);
        mPopularAdapter = new ArrayObjectAdapter(moviePresenter);
        mUpcomingAdapter = new ArrayObjectAdapter(moviePresenter);
        rowsAdapter.add(new ListRow(new HeaderItem(NOW_PLAYING, "Now Playing"), mNowPlayingAdapter));
        rowsAdapter.add(new ListRow(new HeaderItem(TOP_RATED, "Top Rated"), mTopRatedAdapter));
        rowsAdapter.add(new ListRow(new HeaderItem(POPULAR, "Popular"), mPopularAdapter));
        rowsAdapter.add(new ListRow(new HeaderItem(UPCOMING, "Upcoming"), mUpcomingAdapter));
        setAdapter(rowsAdapter);
        setOnItemViewClickedListener(this);
        setOnItemViewSelectedListener(this);
    }

    private void fetchNowPlayingMovies() {
        mDbAPI.getNowPlayingMovies(Config.API_KEY_URL, mNowPlayingPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindNowPlayingMovies, e -> {
                    Timber.e(e, "Error fetching data: %s", e.getMessage());
                });
    }

    private void fetchPopularMovies() {
        mDbAPI.getPopularMovies(Config.API_KEY_URL, mPopularPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindPopularMovies, e -> {
                    Timber.e(e, "Error fetching data: %s", e.getMessage());
                });
    }

    private void fetchUpcomingMovies() {
        mDbAPI.getUpcomingMovies(Config.API_KEY_URL, mUpcomingPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindUpcomingMovies, e -> {
                    Timber.e(e, "Error fetching data: %s", e.getMessage());
                });
    }

    private void fetchTopRatedMovies() {
        mDbAPI.getTopRatedMovies(Config.API_KEY_URL, mTopRatedPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindTopRatedMovies, e -> {
                    Timber.e(e, "Error fetching data: %s", e.getMessage());
                });
    }

    private void bindNowPlayingMovies(MovieResponse response) {
        mNowPlayingPage++;
        for(Movie m : response.getResults()) {
            if (m.getPosterPath() != null) {
                mNowPlayingAdapter.add(m);
            }
        }
        startEntranceTransition();
    }

    private void bindPopularMovies(MovieResponse response) {
        mPopularPage++;
        for(Movie m : response.getResults()) {
            if (m.getPosterPath() != null) {
                mPopularAdapter.add(m);
            }
        }
    }

    private void bindUpcomingMovies(MovieResponse response) {
        mUpcomingPage++;
        for(Movie m : response.getResults()) {
            if (m.getPosterPath() != null) {
                mUpcomingAdapter.add(m);
            }
        }
    }

    private void bindTopRatedMovies(MovieResponse response) {
        mTopRatedPage++;
        for(Movie m : response.getResults()) {
            if (m.getPosterPath() != null) {
                mTopRatedAdapter.add(m);
            }
        }
    }

    @Override
    public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (item instanceof Movie) {
            Movie movie = (Movie) item;
            if (movie.getBackdropPath() != null) {
                mBackgroundManager.loadImage(HttpClientModule.BACKDROP_URL + movie.getBackdropPath());
            } else {
                mBackgroundManager.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.material_bg));
            }

            int id = (int) row.getHeaderItem().getId();
            int index = -1;
            switch (id) {
                case NOW_PLAYING:
                    index = mNowPlayingAdapter.indexOf(movie);
                    if (index == mNowPlayingAdapter.size() - 1) {
                        fetchNowPlayingMovies();
                    }
                    break;
                case UPCOMING:
                    index = mUpcomingAdapter.indexOf(movie);
                    if (index == mUpcomingAdapter.size() - 1) {
                        fetchUpcomingMovies();
                    }
                    break;
                case POPULAR:
                    index = mPopularAdapter.indexOf(movie);
                    if (index == mPopularAdapter.size() - 1) {
                        fetchPopularMovies();
                    }
                    break;
                case TOP_RATED:
                    index = mTopRatedAdapter.indexOf(movie);
                    if (index == mTopRatedAdapter.size() - 1) {
                        fetchTopRatedMovies();
                    }
                    break;
            }
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
