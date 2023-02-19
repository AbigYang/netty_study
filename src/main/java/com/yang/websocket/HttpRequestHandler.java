package com.yang.websocket;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final String wsUri;
    //本地目录
    private static final File INDEX = new File("E:\\study\\Java\\netty_study\\src\\main\\java\\com\\yang\\websocket\\index.html");

//    static {
//        URL location = HttpRequestHandler.class
//                .getProtectionDomain()
//                .getCodeSource().getLocation();
//        try {
//            String path = location.toURI() + "index.html";
//            path = !path.contains("file:") ? path : path.substring(5);
//            INDEX = new File(path);
//        } catch (URISyntaxException e) {
//            throw new IllegalStateException(
//                    "Unable to locate index.html", e);
//        }
//    }

    public HttpRequestHandler(String wsUri) {
        this.wsUri = wsUri;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if (wsUri.equalsIgnoreCase(request.uri())) {
            //如果请求了WebSocket协议升级，则增加引⽤计数（调⽤retain()⽅法），并将它传递给下⼀个ChannelInboundHandler
            //  之所以需要调⽤retain()⽅法，是因为调⽤channelRead()⽅法完成之后，
            //  SimpleChannelInboundHandler将调⽤FullHttpRequest对象上的release()⽅法以释放它的资源
            ctx.fireChannelRead(request.retain());
        } else {
            if (HttpUtil.is100ContinueExpected(request)) {
                //处理100 Continue请求以符合HTTP1.1 规范
                send100Continue(ctx);
            }
            //读取index.html
            RandomAccessFile file = new RandomAccessFile(INDEX, "r");
            HttpResponse response = new DefaultHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
            boolean keepAlive = HttpUtil.isKeepAlive(request);
            //如果请求了keep-alive，则添加所需要的HTTP头信息
            if (keepAlive) {
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, file.length());
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }
            //将HttpResponse写到客户端
            ctx.write(response);
            //将index.html写到客户端
            if (ctx.pipeline().get(SslHandler.class) == null) {
                //零拷贝 直接传输
                ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
            } else {
                // 需要经过处理
                ctx.write(new ChunkedNioFile(file.getChannel()));
            }
            //写LastHttpContent并冲刷⾄客户端
            ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            //如果没有请求keep-alive，则在写操作完成后关闭Channel
            if (!keepAlive) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

