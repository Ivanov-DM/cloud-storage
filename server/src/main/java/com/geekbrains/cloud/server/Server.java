package com.geekbrains.cloud.server;

import com.geekbrains.cloud.server.services.AuthService;
import com.geekbrains.cloud.server.services.StorageService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class Server {
    public Server() {

    }

    public void start() throws InterruptedException {
        EventLoopGroup mainGroup = new NioEventLoopGroup();                                                                             // создаем пул-потоков для подключения новых клиентов
        EventLoopGroup workerGroup = new NioEventLoopGroup();                                                                           // создаем пул-потоков для обработки запросов клиентов

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();                                                                          // создаем объект для начальной настройки сервера:
            bootstrap.group(mainGroup, workerGroup)                                                                                     // указываем какие пулы-потоков использовать (для подключения, для обработки запросов)
                    .channel(NioServerSocketChannel.class)                                                                              // указываем какой класс используем в качестве канала
                    .childHandler(new ChannelInitializer<SocketChannel>() {                                                             // создаем обработчик для каждого клиента в момент его подключения к серверу
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(                                                                                      // ch - ссылка на канал клиента, pipeline() - строим конвейер из обработчиков, добавляем их
                                    new ObjectDecoder(2000 * 1024 * 1024, ClassResolvers.cacheDisabled(null)),    // обработчик ловит байты и преобразует к объекту, парам. 1 - максимальный размер байтового массива (на входе)
                                    new ObjectEncoder(),                                                                                // обработчик разбивает объект на байты и отправляет в сеть (на выходе)
                                    new ServerHandler(new AuthService(), new StorageService())                                          // обработчик, делает что-то полезное
                            );
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)                                                                        //
                    .childOption(ChannelOption.SO_KEEPALIVE, true);                                                               // указываем, что хотим поддерживать соединение с клиентом
            ChannelFuture future = bootstrap.bind(8189).sync();                                                                 // запускаем сервер на порту 8189 и ждем подключения клиентов
            System.out.println("Server started");
            future.channel().closeFuture().sync();                                                                                      //
            System.out.println("Server stoped");
        } finally {
            mainGroup.shutdownGracefully();                                                                                             // закрываем пулы потоков
            workerGroup.shutdownGracefully();
        }
    }


    public static void main(String[] args) throws InterruptedException {
        new Server().start();
    }
}
