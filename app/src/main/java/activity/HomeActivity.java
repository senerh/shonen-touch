package activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.shonen.shonentouch.R;


public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_home:
                return true;
            case R.id.menu_item_favorites:
                startActivity(new Intent(this, FavoritesActivity.class));
                return true;
            case R.id.menu_item_chat:
                Toast.makeText(getApplicationContext(), "Cette fonctionnalité n'a pas encore été développée.", Toast.LENGTH_LONG).show();
                return true;
            case R.id.menu_item_history:
                Toast.makeText(getApplicationContext(), "Cette fonctionnalité n'a pas encore été développée.", Toast.LENGTH_LONG).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
