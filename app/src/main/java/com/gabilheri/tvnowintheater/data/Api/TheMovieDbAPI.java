package com.gabilheri.tvnowintheater.data.Api;

import com.gabilheri.tvnowintheater.dagger.modules.HttpClientModule;
import com.gabilheri.tvnowintheater.data.models.CreditsResponse;
import com.gabilheri.tvnowintheater.data.models.MovieDetails;
import com.gabilheri.tvnowintheater.data.models.MovieResponse;
import com.gabilheri.tvnowintheater.data.models.VideoResponse;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 10/8/16.
 */

public interface TheMovieDbAPI {

    @GET(HttpClientModule.NOW_PLAYING)
    Observable<MovieResponse> getNowPlayingMovies(
            @Query("api_key") String apiKey,
            @Query("page") int page
    );

    @GET(HttpClientModule.TOP_RATED)
    Observable<MovieResponse> getTopRatedMovies(
            @Query("api_key") String apiKey,
            @Query("page") int page
    );

    @GET(HttpClientModule.UPCOMING)
    Observable<MovieResponse> getUpcomingMovies(
            @Query("api_key") String apiKey,
            @Query("page") int page
    );

    @GET(HttpClientModule.POPULAR)
    Observable<MovieResponse> getPopularMovies(
            @Query("api_key") String apiKey,
            @Query("page") int page
    );

    @GET(HttpClientModule.MOVIE + "{id}/similar")
    Observable<MovieResponse> getSimilarMovies(
            @Path("id") String movieId,
            @Query("api_key") String apiKey
    );

    @GET(HttpClientModule.MOVIE + "{id}/recommendations")
    Observable<MovieResponse> getRecommendations(
            @Path("id") String movieId,
            @Query("api_key") String apiKey
    );

    @GET(HttpClientModule.MOVIE + "{id}/credits")
    Observable<CreditsResponse> getCredits(
            @Path("id") String movieId,
            @Query("api_key") String apiKey
    );

    @GET(HttpClientModule.MOVIE + "{id}")
    Observable<MovieDetails> getMovieDetails(
            @Path("id") String movieId,
            @Query("api_key") String apiKey
    );

    @GET(HttpClientModule.MOVIE + "{id}/videos")
    Observable<VideoResponse> getMovieVideos(
            @Path("id") String movieId,
            @Query("api_key") String apiKey
    );

}
