package com.example.prayertimequran.data.models.quran;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.icu.text.DecimalFormat;
import android.icu.text.DecimalFormatSymbols;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class QuranPagesManager {
        public Drawable getQuranImageByPageNumber(Context context, int pageNumber) {

            String drawableName = "quran/new_imgs/" + pageNumber + ".png";

            InputStream istr = null;
            try {
                istr = context.getAssets().open(drawableName);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return Drawable.createFromStream(istr, null);

        }



}
