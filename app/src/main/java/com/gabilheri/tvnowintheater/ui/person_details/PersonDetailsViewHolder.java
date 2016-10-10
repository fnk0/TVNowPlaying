package com.gabilheri.tvnowintheater.ui.person_details;

import android.support.v17.leanback.widget.Presenter;
import android.view.View;
import android.widget.TextView;

import com.gabilheri.tvnowintheater.R;
import com.gabilheri.tvnowintheater.data.models.Person;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 10/10/16.
 */

public class PersonDetailsViewHolder extends Presenter.ViewHolder {

    @BindView(R.id.person_name_tv)
    TextView mPersonNameTV;

    @BindView(R.id.biography)
    TextView mBiographyTV;

    @BindView(R.id.biography_label)
    TextView mBiographyLabelTV;

    @BindView(R.id.birthday_tv)
    TextView mBirthdayTV;

    public PersonDetailsViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bind(Person person) {
        if (person.getName() != null) {
            if (person.getBirthday() != null) {
                try {
                    Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(person.getBirthday());
                    String formattedDate = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(date);
                    mBirthdayTV.setText(String.format(Locale.getDefault(), "Born %s - %s", formattedDate,
                            person.getPlaceOfBirth() == null ? "unknown" : person.getPlaceOfBirth()
                    ));
                } catch (Exception ex) {
                    mBirthdayTV.setText(String.format(Locale.getDefault(), "Born %s - %s", person.getBirthday(),
                            person.getPlaceOfBirth() == null ? "unknown" : person.getPlaceOfBirth()
                    ));
                }
            } else {
                mBirthdayTV.setText("Unknown");
            }

            mPersonNameTV.setText(person.getName());
            mBiographyTV.setText(person.getBiography());
        }
    }
}
