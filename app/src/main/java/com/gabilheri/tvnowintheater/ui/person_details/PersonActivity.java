package com.gabilheri.tvnowintheater.ui.person_details;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.gabilheri.tvnowintheater.R;
import com.gabilheri.tvnowintheater.data.models.CastMember;
import com.gabilheri.tvnowintheater.ui.base.BaseTvActivity;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 10/10/16.
 */

public class PersonActivity extends BaseTvActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CastMember castMember = getIntent().getExtras().getParcelable(CastMember.class.getSimpleName());
        PersonDetailsFragment detailsFragment = PersonDetailsFragment.newInstance(castMember);
        addFragment(detailsFragment);
        getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.material_bg));
    }

}
