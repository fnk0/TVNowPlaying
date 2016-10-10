package com.gabilheri.tvnowintheater.ui.details;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v17.leanback.app.DetailsFragment;
import android.support.v17.leanback.widget.Action;
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
import android.support.v17.leanback.widget.SparseArrayObjectAdapter;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.gabilheri.tvnowintheater.App;
import com.gabilheri.tvnowintheater.Config;
import com.gabilheri.tvnowintheater.dagger.modules.HttpClientModule;
import com.gabilheri.tvnowintheater.data.Api.TheMovieDbAPI;
import com.gabilheri.tvnowintheater.data.models.CastMember;
import com.gabilheri.tvnowintheater.data.models.CreditsResponse;
import com.gabilheri.tvnowintheater.data.models.CrewMember;
import com.gabilheri.tvnowintheater.data.models.Movie;
import com.gabilheri.tvnowintheater.data.models.MovieDetails;
import com.gabilheri.tvnowintheater.data.models.MovieResponse;
import com.gabilheri.tvnowintheater.data.models.Video;
import com.gabilheri.tvnowintheater.data.models.VideoResponse;
import com.gabilheri.tvnowintheater.data.models.PaletteColors;
import com.gabilheri.tvnowintheater.helpers.PaletteUtils;
import com.gabilheri.tvnowintheater.ui.base.CustomFullWidthDetailsPresenter;
import com.gabilheri.tvnowintheater.ui.cast.PersonCardView;
import com.gabilheri.tvnowintheater.ui.cast.PersonPresenter;
import com.gabilheri.tvnowintheater.ui.movie.MovieCardView;
import com.gabilheri.tvnowintheater.ui.movie.MoviePresenter;
import com.gabilheri.tvnowintheater.ui.person_details.PersonActivity;
import com.gabilheri.tvnowintheater.ui.person_details.PersonDetailsFragment;

import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 10/8/16.
 */

public class MovieDetailsFragment extends DetailsFragment implements Palette.PaletteAsyncListener, OnItemViewClickedListener {

    public static String TRANSITION_NAME = "poster_transition";

    @Inject
    TheMovieDbAPI mDbAPI;

    private Movie movie;
    private MovieDetails movieDetails;
    private ArrayObjectAdapter mAdapter;
    private CustomFullWidthDetailsPresenter mFullWidthMovieDetailsPresenter;
    private DetailsOverviewRow mDetailsOverviewRow;
    private String youtubeID;
    private PaletteColors mPaletteColors;

    public static MovieDetailsFragment newInstance(Movie movie) {
        Bundle args = new Bundle();
        args.putParcelable(Movie.class.getSimpleName(), movie);
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.instance().appComponent().inject(this);
        if (getArguments() == null || !getArguments().containsKey(Movie.class.getSimpleName())) {
            return;
        }

        movie = getArguments().getParcelable(Movie.class.getSimpleName());
        setUpAdapter();
        setUpDetailsOverviewRow();
    }

    private void setUpAdapter() {
        mFullWidthMovieDetailsPresenter = new CustomFullWidthDetailsPresenter(new DetailsDescriptionPresenter(),
                new PictureDetailsOverviewLogoPresenter());

        FullWidthDetailsOverviewSharedElementHelper helper = new FullWidthDetailsOverviewSharedElementHelper();
        helper.setSharedElementEnterTransition(getActivity(), TRANSITION_NAME);
        mFullWidthMovieDetailsPresenter.setListener(helper);
        mFullWidthMovieDetailsPresenter.setParticipatingEntranceTransition(false);

        mFullWidthMovieDetailsPresenter.setOnActionClickedListener(action -> {
            int actionId = (int) action.getId();
            switch (actionId) {
                case 0:
                    if (youtubeID != null) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + youtubeID)));
                    }
                    break;
            }
        });

        ClassPresenterSelector classPresenterSelector = new ClassPresenterSelector();
        classPresenterSelector.addClassPresenter(DetailsOverviewRow.class, mFullWidthMovieDetailsPresenter);
        classPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
        mAdapter = new ArrayObjectAdapter(classPresenterSelector);
        setOnItemViewClickedListener(this);
        setAdapter(mAdapter);
    }

    private void setUpDetailsOverviewRow() {
        loadImage(HttpClientModule.POSTER_URL + movie.getPosterPath());
        mDetailsOverviewRow = new DetailsOverviewRow(new MovieDetails());
        mAdapter.add(mDetailsOverviewRow);
        fetchMovieDetails();
    }

    private void fetchMovieDetails() {
        mDbAPI.getMovieDetails(movie.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindMovieDetails, e -> {
                    Timber.e(e, "Error fetching data: %s", e.getMessage());
                });
    }

    private void bindMovieDetails(MovieDetails movieDetails) {
        this.movieDetails = movieDetails;
        mDetailsOverviewRow.setItem(this.movieDetails);
        fetchCastMembers();
        fetchVideos();
    }

    private void fetchVideos() {
        mDbAPI.getMovieVideos(movie.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleVideoResponse, e -> {
                    Timber.e(e, "Error fetching video response: %s", e.getMessage());
                });
    }

    private void handleVideoResponse(VideoResponse response) {
        youtubeID = getTrailer(response.getResults(), "official");
        if (youtubeID == null) {
            youtubeID = getTrailer(response.getResults(), "trailer");
        }

        if (youtubeID == null) {
            youtubeID = getTrailer(response.getResults(), "teaser");
        }

        if (youtubeID == null) {
            youtubeID = getTrailerByType(response.getResults(), "trailer");
        }

        if (youtubeID == null) {
            youtubeID = getTrailerByType(response.getResults(), "featurette");
        }

        if (youtubeID != null) {
            SparseArrayObjectAdapter adapter = new SparseArrayObjectAdapter();
            adapter.set(0, new Action(0, "WATCH TRAILER", null, null));
            mDetailsOverviewRow.setActionsAdapter(adapter);
            notifyDetailsChanged();
        }
    }

    private String getTrailer(List<Video> videos, String keyword) {
        String id = null;
        for(Video v : videos) {
            if (v.getName().toLowerCase().contains(keyword)) {
                id = v.getKey();
            }
        }
        return id;
    }

    private String getTrailerByType(List<Video> videos, String keyword) {
        String id = null;
        for(Video v : videos) {
            if (v.getType().toLowerCase().contains(keyword)) {
                id = v.getKey();
            }
        }
        return id;
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
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        changePalette(((GlideBitmapDrawable) resource).getBitmap());
                        return false;
                    }
                })
                .into(mGlideDrawableSimpleTarget);
    }

    private void changePalette(Bitmap bmp) {
        Palette.from(bmp).generate(this);
    }

    private void fetchCastMembers() {
        mDbAPI.getCredits(movie.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindCastMembers, e -> {
                    Timber.e(e, "Error fetching data: %s", e.getMessage());
                });
    }

    private void bindCastMembers(CreditsResponse response) {
        fetchSimilarMovies();
        if (!response.getCast().isEmpty()) {
            ArrayObjectAdapter castAdapter = new ArrayObjectAdapter(new PersonPresenter());
            castAdapter.addAll(0, response.getCast());
            mAdapter.add(new ListRow(new HeaderItem(0, "Cast"), castAdapter));
        }
        if (!response.getCrew().isEmpty()) {
            for(CrewMember c : response.getCrew()) {
                if (c.getJob().equals("Director")) {
                    movieDetails.setDirector(c.getName());
                    notifyDetailsChanged();
                }
            }
        }
    }

    private void fetchSimilarMovies() {
        mDbAPI.getSimilarMovies(movie.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindSimilarItems, e -> {
                    Timber.e(e, "Error fetching data: %s", e.getMessage());
                });
    }

    private void fetchRecommendations() {
        mDbAPI.getRecommendations(movie.getId(), Config.API_KEY_URL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bindRecommendations, e -> {
                    Timber.e(e, "Error fetching recommendations: %s", e.getMessage());
                });
    }

    private void bindSimilarItems(MovieResponse response) {
        if (!response.getResults().isEmpty()) {
            ArrayObjectAdapter similarMoviesAdapter = new ArrayObjectAdapter(new MoviePresenter());
            similarMoviesAdapter.addAll(0, response.getResults());
            mAdapter.add(new ListRow(new HeaderItem(1, "Similar Movies"), similarMoviesAdapter));
        }
        fetchRecommendations();
    }

    private void bindRecommendations(MovieResponse response) {
        if (!response.getResults().isEmpty()) {
            ArrayObjectAdapter recommendationsAdapter = new ArrayObjectAdapter(new MoviePresenter());
            recommendationsAdapter.addAll(0, response.getResults());
            mAdapter.add(new ListRow(new HeaderItem(2, "Recommendations"), recommendationsAdapter));
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
        } else if (item instanceof CastMember) {
            CastMember castMember = (CastMember) item;
            Intent personIntent = new Intent(getActivity(), PersonActivity.class);
            personIntent.putExtra(CastMember.class.getSimpleName(), castMember);
            if (itemViewHolder.view instanceof PersonCardView) {
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        ((PersonCardView) itemViewHolder.view).getProfileImageIV(),
                        PersonDetailsFragment.TRANSITION_NAME).toBundle();
                getActivity().startActivity(personIntent, bundle);
            } else {
                startActivity(personIntent);
            }
        }
    }

    @Override
    public void onGenerated(Palette palette) {
        mPaletteColors = PaletteUtils.getPaletteColors(palette);
        mFullWidthMovieDetailsPresenter.setActionsBackgroundColor(mPaletteColors.getStatusBarColor());
        mFullWidthMovieDetailsPresenter.setBackgroundColor(mPaletteColors.getToolbarBackgroundColor());
        bindPalette();
        notifyDetailsChanged();
    }

    private void bindPalette() {
        if (movieDetails != null) {
            this.movieDetails.setPaletteColors(mPaletteColors);
        }
    }

    private void notifyDetailsChanged() {
        mDetailsOverviewRow.setItem(this.movieDetails);
        int index = mAdapter.indexOf(mDetailsOverviewRow);
        mAdapter.notifyArrayItemRangeChanged(index, 1);
    }
}
