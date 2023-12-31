package com.cxy.rpc.consumer.controller;

import com.cxy.rpc.api.service.AbstractService;
import com.cxy.rpc.api.service.HelloService;
import com.cxy.rpc.client.annotation.RpcReference;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
public class HelloController {

    @RpcReference
    private HelloService helloService;

    @RpcReference
    private AbstractService abstractService;

    @RequestMapping("/hello/{name}")
    public String hello(@PathVariable("name") String name){
        return helloService.sayHello(name);
    }

    @RequestMapping("/hello/test/{count}")
    public Map<String, Long> performTest(@PathVariable("count") Long count){
        Map<String, Long> result = new HashMap<>();
        result.put("调用次数", count);
        long start = System.currentTimeMillis();
        for(long i = 0; i < count; i++){
            helloService.sayHello(Long.toString(i));
        }
        result.put("耗时", System.currentTimeMillis() - start);
        return result;
    }

    @RequestMapping("abstractHello/{name}")
    public String abstractHello(@PathVariable("name") String name){
        return abstractService.abstractHello(name);
    }
}
