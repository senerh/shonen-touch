package dao.slack;

import java.util.List;

import dto.Message;

public interface InterfaceChatSlackServiceConsumer {
    
    public void handleMessages(List<Message> messageList);
    
}
