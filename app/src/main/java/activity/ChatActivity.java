package activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.shonen.shonentouch.R;
import java.util.ArrayList;
import java.util.List;
import adapter.ChatAdapter;
import dao.preferences.UserPreferences;
import dao.slack.ChatSlackService;
import dao.slack.InterfaceChatSlackServiceConsumer;
import dto.Manga;
import dto.Message;

public class ChatActivity extends AppCompatActivity implements InterfaceChatSlackServiceConsumer{

    private List<Message> msgList;
    private Manga manga;
    private ListView chat_list_view;
    private ChatAdapter chatAdapter;
    private ChatSlackService chatSlackService;
    private Button buttonSend;
    private EditText chatText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        msgList = new ArrayList<>();

        Bundle b = getIntent().getExtras();
        manga = b.getParcelable("manga");

        chatSlackService = new ChatSlackService(this, manga.getSlug());
        chatSlackService.start();
        setContentView(R.layout.activity_chat);

        buttonSend = (Button) findViewById(R.id.btn_send);
        chatText = (EditText) findViewById(R.id.msg);

        setTitle("Chat - "+manga.getName());

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                UserPreferences userPreferences = new UserPreferences(getBaseContext());
                Message message = new Message();
                message.setMessage(chatText.getText().toString());
                message.setAuthor(userPreferences.getPseudonyme());
                chatSlackService.sendMessage(message);
                chatText.setText("");
            }
        });

        chat_list_view = (ListView) findViewById(R.id.msgview);

        chatAdapter = new ChatAdapter(getBaseContext(), msgList);
        chat_list_view.setAdapter(chatAdapter);
    }

    @Override
    public void handleMessages(List<Message> messageList) {
        this.msgList.clear();

        //TODO: A enlever
        for(int i=messageList.size()-1 ; i>-1 ; i--){
            this.msgList.add(messageList.get(i));
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onBackPressed() {
        chatSlackService.stop();
        super.onBackPressed();
    }
}
