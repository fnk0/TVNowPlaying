package com.gabilheri.tvnowintheater.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;

import com.gabilheri.tvnowintheater.App;
import com.gabilheri.tvnowintheater.Config;
import com.gabilheri.tvnowintheater.dagger.modules.HttpClientModule;
import com.gabilheri.tvnowintheater.data.Api.TheMovieDbAPI;
import com.gabilheri.tvnowintheater.data.models.Movie;
import com.gabilheri.tvnowintheater.data.models.MovieResponse;
import com.gabilheri.tvnowintheater.ui.base.GlideBackgroundManager;
import com.gabilheri.tvnowintheater.ui.details.MovieDetailsActivity;
import com.gabilheri.tvnowintheater.ui.movie.MoviePresenter;

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

    private CompositeSubscription mCompositeSubscription;
    private GlideBackgroundManager mBackgroundManager;
    private ArrayObjectAdapter mAdapter;

    int mCurrentPage = 1;

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
        mBackgroundManager = GlideBackgroundManager.getInstance(getActivity());
        setHeadersState(HEADERS_DISABLED);
        createRows();
        fetchData();
    }

    private void createRows() {
        ArrayObjectAdapter rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        MoviePresenter moviePresenter = new MoviePresenter();
        mAdapter = new ArrayObjectAdapter(moviePresenter);
        rowsAdapter.add(new ListRow(mAdapter));
        setAdapter(rowsAdapter);
        setOnItemViewClickedListener(this);
        setOnItemViewSelectedListener(this);
    }

    private void fetchData() {
        mDbAPI.getNowPlayingMovies(Config.API_KEY_URL, mCurrentPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindMovies, e -> {
                    Timber.e(e, "Error fetching data: %s", e.getMessage());
                });
    }

    private void bindMovies(MovieResponse response) {
        mCurrentPage++;
        mAdapter.addAll(mAdapter.size(), response.getResults());
    }

    @Override
    public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (item instanceof Movie) {
            Movie movie = (Movie) item;
            setTitle(movie.getTitle());
            mBackgroundManager.loadImage(HttpClientModule.BACKDROP_URL + movie.getBackdropPath());

            int index = mAdapter.indexOf(movie);
            if (index == mAdapter.size() - 1) {
                fetchData();
            }
        }
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (item instanceof Movie) {
            Movie movie = (Movie) item;
            Intent i = new Intent(getActivity(), MovieDetailsActivity.class);
            i.putExtra(Movie.class.getSimpleName(), movie);
            startActivity(i);
        }
    }
}
