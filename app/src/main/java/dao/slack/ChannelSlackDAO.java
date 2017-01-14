package dao.slack;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dto.Channel;

public class ChannelSlackDAO {

    public static List<Channel> getChannelList() {
        String json = null;
        try {
            json = UtilsSlackDAO.call(new Method("channels.list").addArgument("exclude_archived", "1"));
        } catch (ExceptionSlackDAO e) {
            Log.e(ChannelSlackDAO.class.getName(), "Error while getting list of channels.");
            Log.e(e.getClass().getName(), e.getMessage(), e);
        }
        return jsonToChannelList(json);
    }

    public static Channel getChannelByName(String name) {
        List<Channel> channelList = getChannelList();
        Channel channel = new Channel("", name);
        int index = channelList.indexOf(channel);
        if (index == -1) {
            Log.d(ChannelSlackDAO.class.getName(), "The channel named <~" + name + "~> was not found.");
            return createChannel(name);
        } else {
            return channelList.get(index);
        }
    }

    private static Channel createChannel(String name) {
        String json = null;
        try {
            json = UtilsSlackDAO.call(new Method("channels.create").addArgument("name", name));
        } catch (ExceptionSlackDAO e) {
            Log.e(ChannelSlackDAO.class.getName(), "Error while creating the channel <~" + name + "~>.");
            Log.e(e.getClass().getName(), e.getMessage(), e);
        }
        return jsonToChannel(json);
    }

    private static Channel jsonToChannel(String json) {
        Channel channel = null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject channelJson = jsonObject.getJSONObject("channel");
            channel = new Channel(channelJson.getString("id"), channelJson.getString("name"));
        } catch (JSONException e) {
            Log.e(ChannelSlackDAO.class.getName(), "Error while converting the string <~" + json + "~> into Channel instance.");
            Log.e(e.getClass().getName(), e.getMessage(), e);
        }
        return channel;
    }

    private static List<Channel> jsonToChannelList(String json) {
        List<Channel> channelList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("channels");
            for (int i=0; i<jsonArray.length(); i++) {
                JSONObject channelJson = jsonArray.getJSONObject(i);
                Channel channel = new Channel(channelJson.getString("id"), channelJson.getString("name"));
                channelList.add(channel);
            }
        } catch (JSONException e) {
            Log.e(ChannelSlackDAO.class.getName(), "Error while converting the string <~" + json + "~> into List<Channel> instance.");
            Log.e(e.getClass().getName(), e.getMessage(), e);
        }
        return channelList;
    }
}
