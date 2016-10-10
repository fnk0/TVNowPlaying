package com.gabilheri.tvnowintheater.ui.person_details;

import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.app.DetailsFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewSharedElementHelper;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.gabilheri.tvnowintheater.App;
import com.gabilheri.tvnowintheater.Config;
import com.gabilheri.tvnowintheater.R;
import com.gabilheri.tvnowintheater.dagger.modules.HttpClientModule;
import com.gabilheri.tvnowintheater.data.Api.TheMovieDbAPI;
import com.gabilheri.tvnowintheater.data.models.CastMember;
import com.gabilheri.tvnowintheater.data.models.Movie;
import com.gabilheri.tvnowintheater.data.models.MovieResponse;
import com.gabilheri.tvnowintheater.data.models.Person;
import com.gabilheri.tvnowintheater.ui.base.CustomFullWidthDetailsPresenter;
import com.gabilheri.tvnowintheater.ui.details.MovieDetailsActivity;
import com.gabilheri.tvnowintheater.ui.details.MovieDetailsFragment;
import com.gabilheri.tvnowintheater.ui.details.PictureDetailsOverviewLogoPresenter;
import com.gabilheri.tvnowintheater.ui.movie.MovieCardView;
import com.gabilheri.tvnowintheater.ui.movie.MoviePresenter;

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

public class PersonDetailsFragment extends DetailsFragment implements OnItemViewClickedListener {

    public static String TRANSITION_NAME = "profile_transition";

    @Inject
    TheMovieDbAPI mTheMovieDbAPI;

    private CastMember mCastMember;
    private Person mPerson;
    private ArrayObjectAdapter mAdapter;
    private CustomFullWidthDetailsPresenter mFullWidthMovieDetailsPresenter;
    private DetailsOverviewRow mDetailsOverviewRow;

    public static PersonDetailsFragment newInstance(CastMember castMember) {
        Bundle args = new Bundle();
        args.putParcelable(CastMember.class.getSimpleName(), castMember);
        PersonDetailsFragment fragment = new PersonDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.instance().appComponent().inject(this);
        if (getArguments() == null || !getArguments().containsKey(CastMember.class.getSimpleName())) {
            return;
        }

        mCastMember = getArguments().getParcelable(CastMember.class.getSimpleName());
        setUpAdapter();
        setUpDetailsOverviewRow();
        fetchPerson();
        fetchKnownMovies();
    }

    private void setUpAdapter() {
        mFullWidthMovieDetailsPresenter = new CustomFullWidthDetailsPresenter(new PersonDetailsPresenter(),
                new PictureDetailsOverviewLogoPresenter());

        mFullWidthMovieDetailsPresenter.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.accent));
        mFullWidthMovieDetailsPresenter.setActionsBackgroundColor(ContextCompat.getColor(getActivity(), R.color.accent_dark));

        FullWidthDetailsOverviewSharedElementHelper helper = new FullWidthDetailsOverviewSharedElementHelper();
        helper.setSharedElementEnterTransition(getActivity(), TRANSITION_NAME);
        mFullWidthMovieDetailsPresenter.setListener(helper);
        mFullWidthMovieDetailsPresenter.setParticipatingEntranceTransition(false);

        ClassPresenterSelector classPresenterSelector = new ClassPresenterSelector();
        classPresenterSelector.addClassPresenter(DetailsOverviewRow.class, mFullWidthMovieDetailsPresenter);
        classPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
        mAdapter = new ArrayObjectAdapter(classPresenterSelector);
        setOnItemViewClickedListener(this);
        setAdapter(mAdapter);
    }

    private void setUpDetailsOverviewRow() {
        loadImage(HttpClientModule.POSTER_URL + mCastMember.getProfilePath());
        mDetailsOverviewRow = new DetailsOverviewRow(new Person());
        mAdapter.add(mDetailsOverviewRow);
    }

    private SimpleTarget<GlideDrawable> mGlideDrawableSimpleTarget = new SimpleTarget<GlideDrawable>() {
        @Override
        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
            mDetailsOverviewRow.setImageDrawable(resource);
        }
    };

    private void loadImage(String url) {
        Glide.with(getActivity())
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mGlideDrawableSimpleTarget);
    }

    private void fetchPerson() {
        mTheMovieDbAPI.getPerson(mCastMember.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindPerson, e -> {
                    Timber.e(e, "Error fetching person: %s", e.getMessage());
                });
    }

    private void fetchKnownMovies() {
        mTheMovieDbAPI.getMoviesForCastID(mCastMember.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindMovieResponse, e -> {
                    Timber.e(e, "Error fetching movies for person: %s", e.getMessage());
                });
    }

    private void bindMovieResponse(MovieResponse response) {
        ArrayObjectAdapter moviesAdapter = new ArrayObjectAdapter(new MoviePresenter());
        for(Movie m : response.getResults()) {
            if (m.getPosterPath() != null) {
                moviesAdapter.add(m);
            }
        }
        mAdapter.add(new ListRow(new HeaderItem(0, "Known For"), moviesAdapter));
    }

    private void bindPerson(Person person) {
        this.mPerson = person;
        mDetailsOverviewRow.setItem(this.mPerson);
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
