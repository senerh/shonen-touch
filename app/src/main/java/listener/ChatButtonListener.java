package listener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import activity.ChatActivity;
import dto.Manga;

public class ChatButtonListener implements View.OnClickListener {

    private Manga manga;
    private Activity activity;

    public ChatButtonListener(Manga manga, Activity activity) {
        this.activity = activity;
        this.manga = manga;
    }

    @Override
    public void onClick(View v) {
        Intent myIntent = new Intent(activity, ChatActivity.class);

        Bundle b = new Bundle();
        b.putParcelable("manga", manga);
        myIntent.putExtras(b);

        activity.startActivity(myIntent);
    }
}
