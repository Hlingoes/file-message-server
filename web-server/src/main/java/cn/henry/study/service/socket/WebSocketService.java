package cn.henry.study.service.socket;

import cn.henry.study.utils.ShellExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * description: WebSocket服务端的配置
 *
 * @author Hlingoes
 * @date 2020/2/25 22:59
 */
@ServerEndpoint(value = "/websocket")
@Component
public class WebSocketService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketService.class);

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的WebSocketService对象
     * 虽然@Component默认是单例模式的，但springboot还是会为每个websocket连接初始化一个bean，
     * 所以可以用一个静态set保存起来
     */
    private static CopyOnWriteArraySet<WebSocketService> webSocketSet = new CopyOnWriteArraySet<WebSocketService>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据

    /**
     *  操作SSH的工具类
      */
    private ShellExecutor shellExecutor;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        LOGGER.info("客户端连接！");
        this.session = session;
        try {
            webSocketSet.add(this);
            sendMessage("来自服务端的问候");
            // 在客户端通过webSocket连接时，创建一个SSH的会话
//            this.shellExecutor = new ShellExecutor();
            // 这里写上你的远程服务器ip，账号密码。当然你可以抽取成配置文件
//            this.shellExecutor.initSession("192.168.142.138", "root", "111111");
            // 准备执行命令
//            shellExecutor.execCommand(this);
        } catch (IOException e) {
            LOGGER.error("websocket IO异常", e);
        }
    }

    /**
     * description: 连接关闭调用的方法
     *
     * @param
     * @return void
     * @author Hlingoes 2020/2/25
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        this.shellExecutor.close();
        LOGGER.info("有一连接关闭！");
    }

    /**
     * description: 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     * @param session
     * @return void
     * @author Hlingoes 2020/2/25
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        LOGGER.info("来自客户端的消息:{}", message);
        try {
            // 通过工具类的标准输入网远程服务器中写内容
            this.shellExecutor.printWriter.write(message + "\r\n");
            this.shellExecutor.printWriter.flush();
        } catch (Exception e) {
            LOGGER.info("消息发送失败", e);
        }
    }

    /**
     * description: 发生错误时调用
     *
     * @param session
     * @param error
     * @return void
     * @author Hlingoes 2020/2/25
     */
    @OnError
    public void onError(Session session, Throwable error) {
        LOGGER.error("发生错误", error);
    }

    /**
     * description: 群发消息
     *
     * @param message
     * @return void
     * @author Hlingoes 2020/2/25
     */
    public static void sendToAll(String message) {
        for (WebSocketService item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                LOGGER.info("{}：发送消息失败", item.session.getId());
                continue;
            }
        }
    }

    /**
     * description: 给客户端发送消息
     *
     * @param message
     * @return void
     * @author Hlingoes 2020/2/25
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }
}
