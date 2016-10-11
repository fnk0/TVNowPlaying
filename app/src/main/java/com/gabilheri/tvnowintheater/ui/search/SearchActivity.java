package com.gabilheri.tvnowintheater.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;

import com.gabilheri.tvnowintheater.R;
import com.gabilheri.tvnowintheater.dagger.modules.HttpClientModule;
import com.gabilheri.tvnowintheater.ui.base.BaseTvActivity;
import com.gabilheri.tvnowintheater.ui.base.GlideBackgroundManager;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 10/10/16.
 */

public class SearchActivity extends BaseTvActivity {

    MovieSearchFragment mSearchFragment;
    GlideBackgroundManager mBackgroundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSearchFragment = MovieSearchFragment.newInstance();
        addFragment(mSearchFragment);
        mBackgroundManager = new GlideBackgroundManager(this);
        mBackgroundManager.setBackground(ContextCompat.getDrawable(this, R.drawable.material_bg));
    }

    @Override
    public boolean onSearchRequested() {
        if (mSearchFragment.hasResults()) {
            startActivity(new Intent(this, SearchActivity.class));
        } else {
            mSearchFragment.startRecognition();
        }
        return true;
    }

    public void changeBackground(String path) {
        if (path != null) {
            mBackgroundManager.loadImage(HttpClientModule.BACKDROP_URL + path);
        } else {
            mBackgroundManager.setBackground(ContextCompat.getDrawable(this, R.drawable.material_bg));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // If there are no results found, press the left key to reselect the microphone
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && !mSearchFragment.hasResults()) {
            mSearchFragment.focusOnSearch();
        }
        return super.onKeyDown(keyCode, event);
    }
}
