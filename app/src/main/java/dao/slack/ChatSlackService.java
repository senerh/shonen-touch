package dao.slack;

import android.util.Log;

import dto.Channel;
import dto.Message;

public class ChatSlackService {

    private InterfaceChatSlackServiceConsumer interfaceChatSlackServiceConsumer;
    private String channelName;
    private boolean isRunning;

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
        Channel channel = ChannelSlackDAO.getChannelByName(channelName);
        while (isRunning) {
            Message message = ChatSlackDAO.getLastMessage(channel);
            if (lastMessage == null || !lastMessage.equals(message)) {
                lastMessage = message;
                interfaceChatSlackServiceConsumer.handleMessages(ChatSlackDAO.getMessageList(channel));
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
}
