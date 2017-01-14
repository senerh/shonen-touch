package dao.slack;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dto.Channel;

public class ChannelSlackDAO extends AbstractSlackDAO {

    public List<Channel> getChannelList() {
        String json = call(new Method("channels.list").addArgument("exclude_archived", "1"));
        return jsonToChannelList(json);
    }

    public Channel getChannelByName(String name) {
        List<Channel> channelList = getChannelList();
        Channel channel = new Channel("", name);
        int index = channelList.indexOf(channel);
        if (index == -1) {
            Log.d(getClass().getName(), "The channel named <~" + name + "~> was not found.");
            return createChannel(name);
        } else {
            return channelList.get(index);
        }
    }

    private Channel createChannel(String name) {
        String json = call(new Method("channels.create").addArgument("name", name));
        return jsonToChannel(json);
    }

    private Channel jsonToChannel(String json) {
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

    private List<Channel> jsonToChannelList(String json) {
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
