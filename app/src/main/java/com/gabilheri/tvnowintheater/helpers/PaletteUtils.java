package com.gabilheri.tvnowintheater.helpers;

import android.graphics.Color;
import android.support.v7.graphics.Palette;

/**
 * Created by <a href="mailto:marcus@gabilheri.com">Marcus Gabilheri</a>
 *
 * @author Marcus Gabilheri
 * @version 1.0
 * @since 10/8/16.
 */

public class PaletteUtils {

    public static PaletteColors getPaletteColors(Palette palette) {
        PaletteColors colors = new PaletteColors();

        //figuring out toolbar palette color in order of preference
        if (palette.getDarkVibrantSwatch() != null) {
            colors.toolbarBackgroundColor = palette.getDarkVibrantSwatch().getRgb();
        } else if (palette.getDarkMutedSwatch() != null) {
            colors.toolbarBackgroundColor = palette.getDarkMutedSwatch().getRgb();
        } else if (palette.getVibrantSwatch() != null) {
            colors.toolbarBackgroundColor = palette.getVibrantSwatch().getRgb();
        }


        //set the status bar color to be a darker version of the toolbar background Color;
        if (colors.toolbarBackgroundColor != 0) {
            float[] hsv = new float[3];
            int color = colors.toolbarBackgroundColor;
            Color.colorToHSV(color, hsv);
            hsv[2] *= 0.8f; // value component
            colors.statusBarColor = Color.HSVToColor(hsv);
        }

        //This is for the ripple colors and the style border colors
        if (palette.getLightVibrantSwatch() != null) {
            colors.styleBorderColor = palette.getLightVibrantSwatch().getRgb();
            colors.styleRippleColor = palette.getLightVibrantSwatch().getRgb();
        } else if (palette.getVibrantSwatch() != null) {
            colors.styleBorderColor = palette.getVibrantSwatch().getRgb();
            colors.styleRippleColor = palette.getVibrantSwatch().getRgb();
        } else if (palette.getLightMutedSwatch() != null) {
            colors.styleBorderColor = palette.getLightMutedSwatch().getRgb();
            colors.styleRippleColor = palette.getLightMutedSwatch().getRgb();
        }

        if (colors.toolbarBackgroundColor != 0) {
            colors.progressColor = colors.toolbarBackgroundColor;
        } else {
            colors.progressColor = colors.styleBorderColor;
        }

        return colors;
    }

}
