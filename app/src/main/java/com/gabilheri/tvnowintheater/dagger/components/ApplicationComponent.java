package com.gabilheri.tvnowintheater.dagger.components;

import com.gabilheri.tvnowintheater.App;
import com.gabilheri.tvnowintheater.dagger.AppScope;
import com.gabilheri.tvnowintheater.dagger.modules.ApplicationModule;
import com.gabilheri.tvnowintheater.dagger.modules.HttpClientModule;
import com.gabilheri.tvnowintheater.ui.details.MovieDetailsFragment;
import com.gabilheri.tvnowintheater.ui.main.MainFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 9/4/16.
 */
@AppScope
@Singleton
@Component(modules = {
        ApplicationModule.class,
        HttpClientModule.class,
})
public interface ApplicationComponent {

    void inject(App app);
    void inject(MainFragment mainFragment);
    void inject(MovieDetailsFragment detailsFragment);
}
