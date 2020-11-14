package com.example.hope.service.serviceIpm;

import com.example.hope.config.exception.BusinessException;
import com.example.hope.model.mapper.ServiceMapper;
import com.example.hope.service.ServiceService;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.util.List;

@Log4j2
@Service
public class ServiceServiceIpm implements ServiceService {

    private ServiceMapper serviceMapper;
    
    @Autowired
    public ServiceServiceIpm(ServiceMapper serviceMapper){
        this.serviceMapper = serviceMapper;
    }

    /**
     * 添加服务
     * @param serviceName
     */
    @Override
    @Transient
    @CacheEvict(value = "service",allEntries = true)
    public void insert(String serviceName){
        int res = serviceMapper.insert(serviceName);
        log.info("service insert serviceName -> " + serviceName + " -> res -> " + res);
        BusinessException.check(res,"添加失败");
    }

    /**
     * 删除服务
     *
     * @param id
     */
    @Override
    @Transient
    @CacheEvict(value = "service",allEntries = true)
    public void delete(Long id) {
        int res = serviceMapper.delete(id);
        log.info("service delete id -> " + id + " -> res -> " + res);
        BusinessException.check(res,"删除失败");
    }

    /**
     * 查询全部服务
     * @return
     */
    @Override
    @Cacheable(value = "service",key = "methodName")
    public List<com.example.hope.model.entity.Service> findAll(){
        return serviceMapper.findAll();
    }
}