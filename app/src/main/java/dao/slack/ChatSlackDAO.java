package dao.slack;

import android.text.Html;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import dto.Channel;
import dto.Message;

public class ChatSlackDAO {

    public static final String ADMIN_USERNAME = "admin";
    private static final String SPLITTER = " : ";

    public static List<Message> getMessageList(Channel channel) throws SlackDAOException {
        return getMessageList(channel, 20);
    }

    private static List<Message> getMessageList(Channel channel, int nbMax) throws SlackDAOException {
        String json = null;
        try {
            json = UtilsSlackDAO.call(
                    new Method("channels.history")
                            .addArgument("channel", channel.getId())
                            .addArgument("count", "" + nbMax)
            );
        } catch (SlackDAOException e) {
            throw new SlackDAOException("Error while getting list of messages for channel <~" + channel + "~>.", e);
        }
        return jsonToMessageList(json);
    }

    public static Message getLastMessage(Channel channel) throws SlackDAOException {
        List<Message> messageList = getMessageList(channel, 1);
        return messageList.get(0);
    }

    public static void postMessage(Message message, Channel channel) throws SlackDAOException {
        try {
            String text = messageToText(message);
            UtilsSlackDAO.call(
                    new Method("chat.postMessage").
                            addArgument("channel", channel.getId())
                            .addArgument("text", text)
            );
        } catch (SlackDAOException e) {
            throw new SlackDAOException("Error while posting message on channel <~" + channel + "~>.", e);
        }
    }

    private static List<Message> jsonToMessageList(String json) throws SlackDAOException {
        List<Message> messageList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("messages");
            for (int i=0; i<jsonArray.length(); i++) {
                JSONObject messageJson = jsonArray.getJSONObject(i);
                String text = messageJson.getString("text");
                text = Html.fromHtml(text).toString();
                String[] s = text.split(SPLITTER, 2);
                Message message;
                if (s.length == 2) {
                    message = new Message(s[0], s[1]);
                } else {
                    message = new Message(ADMIN_USERNAME, text);
                }
                messageList.add(message);
            }
        } catch (JSONException e) {
            throw new SlackDAOException("Error while converting the string <~" + json + "~> into List<Channel> instance.", e);
        }
        return messageList;
    }

    private static String messageToText(Message message) {
        String text = message.getAuthor() + SPLITTER + message.getMessage();
        try {
            text = URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(e.getClass().getName(), e.getMessage(), e);
        }
        return text;
    }
}
