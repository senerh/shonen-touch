package holder;

import android.widget.CheckBox;
import android.widget.TextView;

public class MangaViewHolder {

    private CheckBox checkBox;
    private TextView textView;

    public MangaViewHolder() {

    }

    public MangaViewHolder(TextView textView, CheckBox checkBox) {
        this.checkBox = checkBox;
        this.textView = textView;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public TextView getTextView() {
        return textView;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }
}
