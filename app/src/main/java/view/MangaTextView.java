package view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by nathan on 03/02/2017.
 */

public class MangaTextView extends TextView {
    public MangaTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/font2.TTF"));
    }
}
