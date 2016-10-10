package com.gabilheri.tvnowintheater.ui.cast;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gabilheri.tvnowintheater.R;
import com.gabilheri.tvnowintheater.dagger.modules.HttpClientModule;
import com.gabilheri.tvnowintheater.data.models.CastMember;
import com.gabilheri.tvnowintheater.ui.base.BindableCardView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 10/9/16.
 */

public class PersonCardView extends BindableCardView<CastMember> {

    @BindView(R.id.cast_profile)
    ImageView mProfileImage;

    @BindView(R.id.cast_name)
    TextView mNameTV;

    @BindView(R.id.cast_character)
    TextView mCharacterNameTV;

    public PersonCardView(Context context) {
        super(context);
        ButterKnife.bind(this);
    }

    @Override
    protected void bind(CastMember data) {
        mNameTV.setText(data.getName());
        mCharacterNameTV.setText(data.getCharacter());
        if (data.getProfilePath() != null) {
            Glide.with(getContext())
                    .load(HttpClientModule.POSTER_URL + data.getProfilePath())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mProfileImage);
        } else {
            mProfileImage.setImageResource(R.drawable.popcorn);
        }
    }

    public ImageView getProfileImageIV() {
        return mProfileImage;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.card_cast;
    }
}
