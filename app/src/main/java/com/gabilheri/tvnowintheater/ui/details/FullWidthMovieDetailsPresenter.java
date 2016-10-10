package com.gabilheri.tvnowintheater.ui.details;

import android.support.v17.leanback.widget.DetailsOverviewLogoPresenter;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.RowPresenter;
import android.view.View;
import android.view.ViewGroup;

import com.gabilheri.tvnowintheater.R;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 10/8/16.
 */

public class FullWidthMovieDetailsPresenter extends FullWidthDetailsOverviewRowPresenter {

    private int previousState = STATE_FULL;

    public FullWidthMovieDetailsPresenter(Presenter detailsPresenter, DetailsOverviewLogoPresenter logoPresenter) {
        super(detailsPresenter, logoPresenter);
        setInitialState(FullWidthDetailsOverviewRowPresenter.STATE_FULL);
    }

    @Override
    protected void onLayoutLogo(ViewHolder viewHolder, int oldState, boolean logoChanged) {
        View v = viewHolder.getLogoViewHolder().view;
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        lp.setMarginStart(v.getResources().getDimensionPixelSize(R.dimen.logo_margin_start));
        lp.topMargin = v.getResources().getDimensionPixelSize(android.support.v17.leanback.R.dimen.lb_details_v2_blank_height) - lp.height / 2;

        float offset = v.getResources().getDimensionPixelSize(android.support.v17.leanback.R.dimen.lb_details_v2_actions_height) + v
                .getResources().getDimensionPixelSize(android.support.v17.leanback.R.dimen.lb_details_v2_description_margin_top) + (lp.height / 2);

        switch (viewHolder.getState()) {
            case STATE_FULL:
            default:
                if (previousState == STATE_HALF) {
                    v.animate().translationYBy(-offset);
                }

                break;
            case STATE_HALF:
                if (previousState == STATE_FULL) {
                    v.animate().translationYBy(offset);
                }

                break;
        }
        previousState = viewHolder.getState();
        v.setLayoutParams(lp);
    }

    @Override
    protected void onBindRowViewHolder(RowPresenter.ViewHolder holder, Object item) {
        super.onBindRowViewHolder(holder, item);
        FullWidthDetailsOverviewRowPresenter.ViewHolder vh = (FullWidthDetailsOverviewRowPresenter.ViewHolder) holder;
        View v = vh.getOverviewView();
        v.setBackgroundColor(getBackgroundColor());
        v.findViewById(android.support.v17.leanback.R.id.details_overview_actions_background)
                .setBackgroundColor(getActionsBackgroundColor());
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.presenter_movie_details;
    }
}
