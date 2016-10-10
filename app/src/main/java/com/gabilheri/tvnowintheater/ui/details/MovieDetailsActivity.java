package com.gabilheri.tvnowintheater.ui.details;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.gabilheri.tvnowintheater.dagger.modules.HttpClientModule;
import com.gabilheri.tvnowintheater.data.models.Movie;
import com.gabilheri.tvnowintheater.ui.base.BaseTvActivity;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 10/8/16.
 */

public class MovieDetailsActivity extends BaseTvActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Movie movie = getIntent().getExtras().getParcelable(Movie.class.getSimpleName());
        MovieDetailsFragment detailsFragment = MovieDetailsFragment.newInstance(movie);
        addFragment(detailsFragment);

        if (movie != null) {
            Glide.with(this)
                    .load(HttpClientModule.BACKDROP_URL + movie.getBackdropPath())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            getWindow().setBackgroundDrawable(resource);
                        }
                    });
        }
    }
}
