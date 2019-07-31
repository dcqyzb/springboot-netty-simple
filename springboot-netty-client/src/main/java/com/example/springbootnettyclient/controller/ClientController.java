package com.example.springbootnettyclient.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.springbootnettyclient.NettyClient;

/**
 * @author Mr.Deng
 * @date 2019/7/31 15:10
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: mitesofor </p>
 */
@RestController
public class ClientController {
    public static void main(String[] args) {

        // 开启10条线程，每条线程就相当于一个客户端
        new NettyClient("hello").run();
    }

}


