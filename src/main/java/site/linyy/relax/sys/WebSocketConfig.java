package site.linyy.relax.sys;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // registry.addHandler(myHandler(), "/s").withSockJS();
        // 坑：页面用的不是sockJs，这里写了withSockJS后，正常的也连接不成功
        registry.addHandler(myHandler(), "/socket/slip");
    }

    @Bean
    public WebSocketHandler myHandler() {
        return new MyHandler();
    }

}
