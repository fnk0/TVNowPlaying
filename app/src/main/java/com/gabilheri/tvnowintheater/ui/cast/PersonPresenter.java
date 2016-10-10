package com.gabilheri.tvnowintheater.ui.cast;

import android.support.v17.leanback.widget.Presenter;
import android.view.ViewGroup;

import com.gabilheri.tvnowintheater.data.models.CastMember;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 10/9/16.
 */

public class PersonPresenter extends Presenter {

    public PersonPresenter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new ViewHolder(new PersonCardView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        ((PersonCardView) viewHolder.view).bind((CastMember) item);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }
}
