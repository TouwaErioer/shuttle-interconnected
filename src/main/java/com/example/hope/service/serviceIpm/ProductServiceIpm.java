package com.example.hope.service.serviceIpm;

import com.example.hope.common.logger.LoggerHelper;
import com.example.hope.common.utils.JwtUtils;
import com.example.hope.common.utils.Utils;
import com.example.hope.config.exception.BusinessException;
import com.example.hope.config.redis.RedisService;
import com.example.hope.model.entity.Orders;
import com.example.hope.model.entity.Product;
import com.example.hope.model.mapper.ProductMapper;
import com.example.hope.repository.elasticsearch.EsPageHelper;
import com.example.hope.repository.elasticsearch.ProductRepository;
import com.example.hope.service.ProductService;
import com.example.hope.service.StoreService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.log4j.Log4j2;

import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.beans.Transient;
import java.util.*;

@Log4j2
@Service
public class ProductServiceIpm implements ProductService {

    @Resource
    private ProductMapper productMapper;

    @Resource
    private StoreService storeService;

    @Resource
    private RedisService redisService;

    @Resource
    private OrderServiceIpm orderServiceIpm;

    @Resource
    private ProductRepository productRepository;

    @Resource
    private EsPageHelper<Product> esPageHelper;

    /**
     * 添加产品
     *
     * @param product 产品
     */
    @Override
    @Transient
    @CacheEvict(value = "product", allEntries = true)
    public void insert(Product product) {
        if (!storeService.exist(product.getStoreId())) throw new BusinessException(0, "商店id不存在");
        int res = productMapper.insert(product);
        log.info(LoggerHelper.logger(product, res));
        BusinessException.check(res, "添加失败");
        productRepository.save(product);
    }

    /**
     * 增加产品销量
     *
     * @param id    产品id
     * @param sales 销量
     */
    @Transient
    public void addSales(long id, int sales) {
        int res = productMapper.addSales(id, sales);
        log.info("product addSales -> " + id + " for -> " + sales + " -> res " + res);
        BusinessException.check(res, "更新销量失败");
        redisService.incrScore("product_rank", String.valueOf(id), sales);
        // 增加商店销量
        storeService.sales(findById(id).getStoreId(), sales);
    }

    /**
     * 删除产品
     *
     * @param id 产品id
     */
    @Override
    @Transient
    @CacheEvict(value = "product", allEntries = true)
    public void delete(long id) {
        int res = productMapper.delete(id, "id");
        orderServiceIpm.deleteByPid(id);
        log.info(LoggerHelper.logger(id, res));
        BusinessException.check(res, "删除失败");
        productRepository.deleteById(id);
    }

    /**
     * 根据商店id删除产品
     *
     * @param storeId 商店id
     */
    @Transient
    @CacheEvict(value = "product", allEntries = true)
    public void deleteByStoreId(long storeId) {
        for (Product product : findByStoreId(storeId)) orderServiceIpm.deleteByPid(product.getId());
        int res = productMapper.delete(storeId, "StoreId");
        log.info(LoggerHelper.logger(storeId, res));
    }

    /**
     * 更新产品
     *
     * @param product 产品
     */
    @Override
    @Transient
    @CacheEvict(value = "product", allEntries = true)
    public void update(Product product) {
        if (!storeService.exist(product.getStoreId())) throw new BusinessException(0, "商店id不存在");
        int res = productMapper.update(product);
        log.info(LoggerHelper.logger(product, res));
        BusinessException.check(res, "更新失败");
        productRepository.save(product);
    }

    /**
     * 更新产品评分
     *
     * @param product 产品
     * @param token   Token
     */
    @Override
    @CacheEvict(value = "product", allEntries = true)
    public void review(Product product, String token) {
        long userId = JwtUtils.getUserId(token);
        int res = 0;
        boolean status = false;
        // 只允许下单此产品的用户或管理员对产品评分
        List<Orders> orders = orderServiceIpm.findByCid(userId);
        for (Orders order : orders) {
            System.out.println(order);
            if ((order.getStatus() == 0 && order.getPid() == product.getId()) || JwtUtils.is_admin(token)) {
                res = productMapper.review(product.getId(), product.getRate());
                storeService.review(product.getStoreId(), product.getRate());
                status = true;
                break;
            }
        }
        if (!status) throw new BusinessException(0, "只允许下单此产品的用户对产品评分");
        log.info(LoggerHelper.logger(product, res));
        BusinessException.check(res, "更新评分失败");
    }

    /**
     * 查询全部产品
     *
     * @return 分页包装类
     */
    @Override
    @Cacheable(value = "product", key = "methodName + #option.toString()")
    public PageInfo<Product> findAll(Map<String, String> option) {
        Utils.checkOption(option, Product.class);
        String orderBy = String.format("product.%s %s", option.get("sort"), option.get("order"));
        PageHelper.startPage(Integer.parseInt(option.get("pageNo")), Integer.parseInt(option.get("pageSize")), orderBy);
        return PageInfo.of(productMapper.select(null, null));
    }

    /**
     * 根据storeId查询产品
     *
     * @param storeId 商店id
     * @return 产品列表
     */
    @Override
    @Cacheable(value = "product", key = "methodName + #storeId")
    public List<Product> findByStoreId(long storeId) {
        return productMapper.select(String.valueOf(storeId), "storeId");
    }

    /**
     * 根据id查询产品
     *
     * @param id 产品id
     * @return 产品
     */
    @Override
    @Cacheable(value = "product", key = "methodName + #id")
    public Product findById(long id) {
        return productMapper.select(String.valueOf(id), "id").get(0);
    }

    /**
     * 排行榜
     *
     * @return 产品列表
     */
    @Override
    public List<Product> rank() {
        Set<String> rank = redisService.range("product_rank", 0, 9);
        // 如果排行榜为空，将所有产品加入进去，分数为0
        if (rank.size() == 0) {
            List<Product> stores = findAll(new HashMap<>()).getList();
            for (Product product : stores) {
                redisService.incrScore("product_rank", String.valueOf(product.getId()), 0);
            }
            rank = redisService.range("product_rank", 0, 9);
        }
        List<Product> products = new ArrayList<>();
        for (String id : rank) {
            products.add(findById(Long.parseLong(id)));
        }
        return products;
    }

    /**
     * 搜索
     *
     * @param keywords 关键词
     * @param option   分页参数
     * @return 分页包装类
     */
    @Override
    public SearchHits search(String keywords, Map<String, String> option) {
        return esPageHelper.build(QueryBuilders.matchQuery("name", keywords), option, Product.class);
    }

    /**
     * 是否存在产品
     *
     * @param id 产品id
     * @return boolean
     */
    @Override
    public boolean exist(long id) {
        return findById(id) != null;
    }
}