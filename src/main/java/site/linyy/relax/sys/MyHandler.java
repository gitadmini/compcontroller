package site.linyy.relax.sys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import site.linyy.relax.controller.MouseController;

@Component
public class MyHandler extends TextWebSocketHandler {

    @Autowired
    MouseController mouseController;

    protected void handleTextMessage(WebSocketSession session,
            TextMessage message) throws Exception {

        // 因touchmove（移动鼠标）时需要大量的http请求，连接比较耗时，造成延迟，所以改用socket长连接，效果不错~
        String payLoad = message.getPayload();
        String[] strs = payLoad.split(",");
        mouseController.slip(strs[0], strs[1]);
    }
}
