package com.geekbrains.cloud.server;

import com.geekbrains.cloud.common.*;
import com.geekbrains.cloud.server.services.AuthService;
import com.geekbrains.cloud.server.services.StorageService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ServerHandler extends ChannelInboundHandlerAdapter {                                   // для согдания своего Handler необходимо унаследоваться от класса ChannelInboundHandlerAdapter
    AuthService authService;
    StorageService storageService;

    public ServerHandler(AuthService authService, StorageService storageService) {
        this.authService = authService;
        this.storageService = storageService;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {                         // метод срабатывает когда клиент подключается
        super.channelActive(ctx);
        // здесь должна быть логика по отправке клиенту списка имеющихся в хранилище файлов
        // для конкретного подключившегося клиента, и приветственного сообщения
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {               // метод, который срабатывает когда приходит информация от клиента
        try {                                                                                       // ChannelHandlerContext - канал общения с клиентом, Object msg - сообщение от него
            if (msg == null) {                                                                      // ничего не делаем, если пришло что-то непонятное
                return;
            }
            System.out.println("Letter!!!");
            if (msg instanceof AuthMessage) {
                System.out.println("Received message type: " + msg.getClass().getName());
                AuthMessage authMsg = (AuthMessage) msg;
                if (authService.authUser(authMsg.getLogin(), authMsg.getPassword())) {
                    authMsg.setAuthStatus(true);
                    ctx.writeAndFlush(authMsg);
                    System.out.println("Sent message type: " + authMsg.getClass().getName());
                } else {
                    ctx.writeAndFlush(authMsg);
                    System.out.println("sent message type: " + authMsg.getClass().getName() + " error Auth");
                }
            }
            if (msg instanceof FilePathMessage) {
                System.out.println("Letter filePathMessage");
                FilePathMessage file = (FilePathMessage) msg;
                System.out.println("Letter filePathMessage:" + file.getFileName() + ", " + file.isDirectory());
                if (file.isDirectory()) {
                    if (file.getFileName().equals("rootDirectory")) {
                        ctx.writeAndFlush(storageService.sendStorageFiles(Paths.get("server_storage")));
                    } else {
                        storageService.sendStorageFiles(Paths.get(file.getFileName())).stream().forEach(x -> System.out.println(x.getFileName() + ", " + x.isDirectory()));

                        ctx.writeAndFlush(storageService.sendStorageFiles(Paths.get(file.getFileName())));
                    }
                } else {
                    if (file.isDelete()) {
                        Path parentPath = Paths.get(file.getFileName()).getParent();
                        Files.delete(Paths.get(file.getFileName()));
                        ctx.writeAndFlush(storageService.sendStorageFiles(parentPath));
                    } else {
                        FileMessage fileMessage = new FileMessage(Paths.get(file.getFileName()), "");
                        ctx.writeAndFlush(fileMessage);
                    }
                }
            }
            if (msg instanceof FileMessage) {
                FileMessage fileMsg = (FileMessage) msg;


                Files.write(Paths.get(fileMsg.getSaveDirectoryName() + "/" + fileMsg.getFileName()), fileMsg.getData(), StandardOpenOption.CREATE);
                ctx.writeAndFlush(storageService.sendStorageFiles(Paths.get(fileMsg.getSaveDirectoryName())));
            }

        } finally {
            ReferenceCountUtil.release(msg);                                                        // очищаем буфер
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {      // метод для перехвата Exception, если его не переопределять, то это не будет
        cause.printStackTrace();                                                                    // ошибкой, просто если данные не прийдут с сервера, то не будет видно где
        ctx.close();                                                                                // именно произошла ошибка, ChannelHandlerContext - это канал общения с клиентом
    }
}
