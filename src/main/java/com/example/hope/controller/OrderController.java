package com.example.hope.controller;

import com.example.hope.annotation.Admin;
import com.example.hope.annotation.LoginUser;
import com.example.hope.common.utils.ReturnMessageUtil;
import com.example.hope.model.entity.ReturnMessage;
import com.example.hope.model.entity.Orders;
import com.example.hope.service.OrderService;
import com.example.hope.service.serviceIpm.OrderServiceIpm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @description: 订单相关路由
 * @author: DHY
 * @created: 2020/10/25 14:20
 */
@RestController
@RequestMapping("/order")
@Api(tags = "订单相关接口")
public class OrderController {

    private OrderService orderService;

    @Autowired
    public OrderController(OrderServiceIpm orderService) {
        this.orderService = orderService;
    }

    @LoginUser
    @ApiOperation("添加订单")
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public ReturnMessage<Object> insert(@RequestBody List<Orders> orderList, @RequestParam(defaultValue = "false") boolean isExpired) {
        orderService.insert(orderList, isExpired);
        return ReturnMessageUtil.sucess();
    }

    @LoginUser
    @ApiOperation("删除订单")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ReturnMessage<Object> delete(@RequestBody List<Orders> orders, HttpServletRequest request) {
        orderService.delete(orders, request.getHeader("Authorization"));
        return ReturnMessageUtil.sucess();
    }

    @LoginUser
    @ApiOperation("修改订单")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ReturnMessage<Object> update(Orders order) {
        orderService.update(order);
        return ReturnMessageUtil.sucess();
    }

    @LoginUser
    @ApiOperation("服务员接单")
    @RequestMapping(value = "/receive", method = RequestMethod.POST)
    public ReturnMessage<Object> receive(long orderId, long userId) {
        orderService.receive(orderId, userId);
        return ReturnMessageUtil.sucess();
    }

    @Admin
    @ApiOperation("查询全部订单")
    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public ReturnMessage<Object> findAll(@RequestParam Map<String, String> option) {
        return ReturnMessageUtil.sucess(orderService.findAll(option));
    }

    @Admin
    @ApiOperation("根据pid查询订单")
    @RequestMapping(value = "/findByPid/{id}", method = RequestMethod.GET)
    public ReturnMessage<Object> findByPid(@PathVariable long id, @RequestParam Map<String, String> option) {
        return ReturnMessageUtil.sucess(orderService.findByPid(id, option));
    }

    @LoginUser
    @ApiOperation("根据cid查询订单")
    @RequestMapping(value = "/findByCid/{id}", method = RequestMethod.GET)
    public ReturnMessage<Object> findByCid(@PathVariable long id, @RequestParam Map<String, String> option) {
        return ReturnMessageUtil.sucess(orderService.findByCid(id, option));
    }

    @LoginUser
    @ApiOperation("根据sid查询订单")
    @RequestMapping(value = "/findBySid/{id}", method = RequestMethod.GET)
    public ReturnMessage<Object> findBySid(@PathVariable long id, @RequestParam Map<String, String> option) {
        return ReturnMessageUtil.sucess(orderService.findBySid(id, option));
    }

    @LoginUser
    @ApiOperation("按订单id查询订单")
    @RequestMapping(value = "/findById/{id}", method = RequestMethod.GET)
    public ReturnMessage<Object> findById(@PathVariable long id) {
        return ReturnMessageUtil.sucess(orderService.findById(id));
    }

    @LoginUser
    @ApiOperation("完成订单")
    @RequestMapping(value = "/completed", method = RequestMethod.POST)
    public ReturnMessage<Object> completed(HttpServletRequest request, Orders orders) {
        orderService.completed(orders, request.getHeader("Authorization"));
        return ReturnMessageUtil.sucess();
    }

    @LoginUser
    @ApiOperation("查询全部未接单订单")
    @RequestMapping(value = "/findByReceive", method = RequestMethod.GET)
    public ReturnMessage<Object> findByReceive(@RequestParam Map<String, String> option) {
        return ReturnMessageUtil.sucess(orderService.findByReceive(option));
    }

    @LoginUser
    @ApiOperation("查询全部完成订单")
    @RequestMapping(value = "/findByCompleted", method = RequestMethod.GET)
    public ReturnMessage<Object> findByCompleted(@RequestParam Map<String, String> option) {
        return ReturnMessageUtil.sucess(orderService.findByCompleted(option));
    }

    @LoginUser
    @ApiOperation("查询全部配送订单")
    @RequestMapping(value = "/findByPresent", method = RequestMethod.GET)
    public ReturnMessage<Object> findByPresent(@RequestParam Map<String, String> option) {
        return ReturnMessageUtil.sucess(orderService.findByPresent(option));
    }
}