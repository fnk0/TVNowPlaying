package com.gabilheri.tvnowintheater.ui.details;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.gabilheri.tvnowintheater.R;
import com.gabilheri.tvnowintheater.dagger.modules.HttpClientModule;
import com.gabilheri.tvnowintheater.data.models.Movie;
import com.gabilheri.tvnowintheater.ui.base.BaseTvActivity;
import com.gabilheri.tvnowintheater.ui.base.GlideBackgroundManager;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 10/8/16.
 */

public class MovieDetailsActivity extends BaseTvActivity {

    GlideBackgroundManager mBackgroundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Movie movie = getIntent().getExtras().getParcelable(Movie.class.getSimpleName());
        MovieDetailsFragment detailsFragment = MovieDetailsFragment.newInstance(movie);
        addFragment(detailsFragment);

        mBackgroundManager = new GlideBackgroundManager(this);

        if (movie != null && movie.getBackdropPath() != null) {
            mBackgroundManager.loadImage(HttpClientModule.BACKDROP_URL + movie.getBackdropPath());
        } else {
            mBackgroundManager.setBackground(ContextCompat.getDrawable(this, R.drawable.material_bg));
        }
    }
}
