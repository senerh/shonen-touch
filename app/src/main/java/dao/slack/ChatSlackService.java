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
            try {
                Message message = ChatSlackDAO.getLastMessage(loadChannel());
                if (lastMessage == null || !lastMessage.equals(message)) {
                    lastMessage = message;
                    List<Message> messageList = ChatSlackDAO.getMessageList(loadChannel());
                    Collections.reverse(messageList);
                    interfaceChatSlackServiceConsumer.handleMessages(messageList);
                }
            } catch (SlackDAOException e) {
                Log.e(getClass().getName(), "Error while getting chat messages", e);
            }
            ChatSlackService.this.sleep(500);
        }
    }

    public void stop() {
        isRunning = false;
    }

    public void sendMessage(final Message message) {
        new Thread() {
            @Override
            public void run() {
                boolean isPosted = false;
                while (!isPosted) {
                    try {
                        Channel channel = loadChannel();
                        Message lastMessage = ChatSlackDAO.getLastMessage(channel);
                        if (!message.equals(lastMessage)) {
                            ChatSlackDAO.postMessage(message, channel);
                        }
                        isPosted = true;
                    } catch (SlackDAOException e) {
                        Log.e(getClass().getName(), "Error while posting chat message", e);
                        ChatSlackService.this.sleep(5000);
                    }
                }
            }
        }.start();
    }

    private Channel loadChannel() {
        if (channel == null) {
            boolean isLoaded = false;
            while (!isLoaded) {
                try {
                    channel = ChannelSlackDAO.getChannelByName(channelName);
                    isLoaded = true;
                } catch (SlackDAOException e) {
                    Log.e(getClass().getName(), "Error while getting channel", e);
                    sleep(5000);
                }
            }
        }
        return channel;
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Log.e(getClass().getName(), e.getMessage(), e);
        }
    }
}
