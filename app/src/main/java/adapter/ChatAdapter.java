package adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import dao.preferences.UserPreferencesDAO;
import dao.slack.ChatSlackDAO;
import dto.Message;
import io.github.senerh.shonentouch.R;

public class ChatAdapter extends ArrayAdapter<Message> {

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
        Message message = getItem(position);
        UserPreferencesDAO userPreferencesDAO = new UserPreferencesDAO(this.context);
        LayoutInflater inflater = (LayoutInflater) this.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (message.getAuthor().equals(userPreferencesDAO.getUsername())) {
            convertView = inflater.inflate(R.layout.element_right_chat_list, parent, false);
        } else {
            convertView = inflater.inflate(R.layout.element_left_chat_list, parent, false);
            TextView authorTextView = (TextView) convertView.findViewById(R.id.msg_author);
            authorTextView.setText(message.getAuthor());
            if (message.getAuthor().equals(ChatSlackDAO.ADMIN_USERNAME)) {
                int color = convertView.getResources().getColor(R.color.colorAccent);
                authorTextView.setTextColor(color);
            }
        }

        TextView messageTextView = (TextView) convertView.findViewById(R.id.msg_row);
        messageTextView.setText(message.getMessage());

        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}