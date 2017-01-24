package adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.shonen.shonentouch.R;
import java.util.List;
import dao.preferences.UserPreferences;
import dto.Message;


public class ChatAdapter extends ArrayAdapter<Message> {

    private TextView chatText;
    private List<Message> chatMessageList;
    private Context context;


    public ChatAdapter(Context context, List<Message> chatMessageList) {
        super(context, R.layout.element_right_chat_list, chatMessageList);
        this.context = context;
        this.chatMessageList = chatMessageList;
    }

    @Override
    public void add(Message object) {
        chatMessageList.add(object);
        super.add(object);
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public Message getItem(int index) {
        return this.chatMessageList.get(index);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message chatMessage = getItem(position);
        UserPreferences userPreferences = new UserPreferences(this.context);
        LayoutInflater inflater = (LayoutInflater) this.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        String msg;

        if (chatMessage.getAuthor().equals(userPreferences.getPseudonyme())) {
            msg = chatMessage.getMessage();
            convertView = inflater.inflate(R.layout.element_right_chat_list, parent, false);

        }else{
            msg = String.format(
                    context.getResources().getString(R.string.msg),
                    chatMessage.getAuthor(),
                    chatMessage.getMessage()
            );
            convertView = inflater.inflate(R.layout.element_left_chat_list, parent, false);
        }

        chatText = (TextView) convertView.findViewById(R.id.msg_row);
        chatText.setText(msg);

        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}