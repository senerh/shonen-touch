package dao.slack;

import android.util.Log;

import java.util.Collections;
import java.util.List;

import dto.Channel;
import dto.Message;

public class ChatSlackService {

    private InterfaceChatSlackServiceConsumer interfaceChatSlackServiceConsumer;
    private String channelName;
    private boolean isRunning;
    private Channel channel;

    public ChatSlackService(InterfaceChatSlackServiceConsumer interfaceChatSlackServiceConsumer, String channelName) {
        this.interfaceChatSlackServiceConsumer = interfaceChatSlackServiceConsumer;
        this.channelName = channelName;
        isRunning = false;
    }

    public void start() {
        isRunning = true;
        new Thread() {
            @Override
            public void run() {
                loop();
            }
        }.start();
    }

    private void loop() {
        Message lastMessage = null;
        while (isRunning) {
            Message message = ChatSlackDAO.getLastMessage(loadChannel());
            if (lastMessage == null || !lastMessage.equals(message)) {
                lastMessage = message;
                List<Message> messageList = ChatSlackDAO.getMessageList(loadChannel());
                Collections.reverse(messageList);
                interfaceChatSlackServiceConsumer.handleMessages(messageList);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }
        }
    }

    public void stop() {
        isRunning = false;
    }

    public void sendMessage(final Message message) {
        new Thread() {
            @Override
            public void run() {
                Channel channel = loadChannel();
                Message lastMessage = ChatSlackDAO.getLastMessage(channel);
                if (!message.equals(lastMessage)) {
                    ChatSlackDAO.postMessage(message, channel);
                }
            }
        }.start();
    }

    private Channel loadChannel() {
        if (channel == null) {
            channel = ChannelSlackDAO.getChannelByName(channelName);
        }
        return channel;
    }
}
