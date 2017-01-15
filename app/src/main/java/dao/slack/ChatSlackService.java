package dao.slack;

import android.util.Log;

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
                interfaceChatSlackServiceConsumer.handleMessages(ChatSlackDAO.getMessageList(loadChannel()));
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
                ChatSlackDAO.postMessage(message, loadChannel());
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
