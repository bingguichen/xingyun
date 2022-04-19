package com.lframework.xingyun.basedata.impl.product;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.lframework.common.constants.StringPool;
import com.lframework.common.exceptions.impl.DefaultClientException;
import com.lframework.common.utils.Assert;
import com.lframework.common.utils.CollectionUtil;
import com.lframework.common.utils.IdUtil;
import com.lframework.common.utils.ObjectUtil;
import com.lframework.common.utils.StringUtil;
import com.lframework.starter.mybatis.annotations.OpLog;
import com.lframework.starter.mybatis.enums.OpLogType;
import com.lframework.starter.mybatis.impl.BaseMpServiceImpl;
import com.lframework.starter.mybatis.resp.PageResult;
import com.lframework.starter.mybatis.utils.OpLogUtil;
import com.lframework.starter.mybatis.utils.PageHelperUtil;
import com.lframework.starter.mybatis.utils.PageResultUtil;
import com.lframework.xingyun.basedata.dto.product.saleprop.ProductSalePropGroupDto;
import com.lframework.xingyun.basedata.dto.product.saleprop.item.ProductSalePropItemDto;
import com.lframework.xingyun.basedata.dto.product.saleprop.item.SalePropItemByProductDto;
import com.lframework.xingyun.basedata.entity.ProductSalePropItem;
import com.lframework.xingyun.basedata.mappers.ProductSalePropItemMapper;
import com.lframework.xingyun.basedata.service.product.IProductSalePropGroupService;
import com.lframework.xingyun.basedata.service.product.IProductSalePropItemService;
import com.lframework.xingyun.basedata.vo.product.saleprop.item.CreateProductSalePropItemVo;
import com.lframework.xingyun.basedata.vo.product.saleprop.item.QueryProductSalePropItemVo;
import com.lframework.xingyun.basedata.vo.product.saleprop.item.UpdateProductSalePropItemVo;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductSalePropItemServiceImpl extends
    BaseMpServiceImpl<ProductSalePropItemMapper, ProductSalePropItem> implements
    IProductSalePropItemService {

  @Autowired
  private IProductSalePropGroupService productSalePropGroupService;

  @Override
  public PageResult<ProductSalePropItemDto> query(Integer pageIndex, Integer pageSize,
      QueryProductSalePropItemVo vo) {

    Assert.greaterThanZero(pageIndex);
    Assert.greaterThanZero(pageSize);

    PageHelperUtil.startPage(pageIndex, pageSize);
    List<ProductSalePropItemDto> datas = this.query(vo);

    return PageResultUtil.convert(new PageInfo<>(datas));
  }

  @Override
  public List<ProductSalePropItemDto> query(QueryProductSalePropItemVo vo) {

    return getBaseMapper().query(vo);
  }

  @Cacheable(value = ProductSalePropItemDto.CACHE_NAME, key = "#id", unless = "#result == null")
  @Override
  public ProductSalePropItemDto getById(String id) {

    return getBaseMapper().getById(id);
  }

  @OpLog(type = OpLogType.OTHER, name = "新增商品销售属性值，ID：{}, 编号：{}", params = {"#id", "#code"})
  @Transactional
  @Override
  public String create(CreateProductSalePropItemVo vo) {

    Wrapper<ProductSalePropItem> checkWrapper = Wrappers.lambdaQuery(ProductSalePropItem.class)
        .eq(ProductSalePropItem::getGroupId, vo.getGroupId())
        .eq(ProductSalePropItem::getCode, vo.getCode());
    if (getBaseMapper().selectCount(checkWrapper) > 0) {
      throw new DefaultClientException("编号重复，请重新输入！");
    }

    checkWrapper = Wrappers.lambdaQuery(ProductSalePropItem.class)
        .eq(ProductSalePropItem::getGroupId, vo.getGroupId())
        .eq(ProductSalePropItem::getName, vo.getName());
    if (getBaseMapper().selectCount(checkWrapper) > 0) {
      throw new DefaultClientException("名称重复，请重新输入！");
    }

    ProductSalePropGroupDto productSalePropGroup = productSalePropGroupService
        .getById(vo.getGroupId());
    if (ObjectUtil.isNull(productSalePropGroup)) {
      throw new DefaultClientException("销售属性组不存在！");
    }

    ProductSalePropItem data = new ProductSalePropItem();
    data.setId(IdUtil.getId());
    data.setCode(vo.getCode());
    data.setName(vo.getName());
    data.setGroupId(vo.getGroupId());
    data.setAvailable(Boolean.TRUE);
    data.setDescription(
        StringUtil.isBlank(vo.getDescription()) ? StringPool.EMPTY_STR : vo.getDescription());

    getBaseMapper().insert(data);

    OpLogUtil.setVariable("id", data.getId());
    OpLogUtil.setVariable("code", vo.getCode());
    OpLogUtil.setExtra(vo);

    return data.getId();
  }

  @OpLog(type = OpLogType.OTHER, name = "修改商品销售属性值，ID：{}, 编号：{}", params = {"#id", "#code"})
  @Transactional
  @Override
  public void update(UpdateProductSalePropItemVo vo) {

    ProductSalePropItem data = getBaseMapper().selectById(vo.getId());
    if (ObjectUtil.isNull(data)) {
      throw new DefaultClientException("销售属性值不存在！");
    }

    Wrapper<ProductSalePropItem> checkWrapper = Wrappers.lambdaQuery(ProductSalePropItem.class)
        .eq(ProductSalePropItem::getGroupId, data.getGroupId())
        .eq(ProductSalePropItem::getCode, vo.getCode())
        .ne(ProductSalePropItem::getId, vo.getId());
    if (getBaseMapper().selectCount(checkWrapper) > 0) {
      throw new DefaultClientException("编号重复，请重新输入！");
    }

    checkWrapper = Wrappers.lambdaQuery(ProductSalePropItem.class)
        .eq(ProductSalePropItem::getGroupId, data.getGroupId())
        .eq(ProductSalePropItem::getName, vo.getName())
        .ne(ProductSalePropItem::getId, vo.getId());
    if (getBaseMapper().selectCount(checkWrapper) > 0) {
      throw new DefaultClientException("名称重复，请重新输入！");
    }

    LambdaUpdateWrapper<ProductSalePropItem> updateWrapper = Wrappers
        .lambdaUpdate(ProductSalePropItem.class)
        .set(ProductSalePropItem::getCode, vo.getCode())
        .set(ProductSalePropItem::getName, vo.getName())
        .set(ProductSalePropItem::getAvailable, vo.getAvailable())
        .set(ProductSalePropItem::getDescription,
            StringUtil.isBlank(vo.getDescription()) ? StringPool.EMPTY_STR : vo.getDescription())
        .eq(ProductSalePropItem::getId, vo.getId());

    getBaseMapper().update(updateWrapper);

    OpLogUtil.setVariable("id", data.getId());
    OpLogUtil.setVariable("code", vo.getCode());
    OpLogUtil.setExtra(vo);

    IProductSalePropItemService thisService = getThis(this.getClass());
    thisService.cleanCacheByKey(data.getId());

    List<String> productIdList = getBaseMapper().getProductIdById(data.getId());
    if (!CollectionUtil.isEmpty(productIdList)) {
      for (String productId : productIdList) {
        thisService.cleanCacheByKey(productId);
      }
    }
  }

  @Override
  public List<ProductSalePropItemDto> getEnablesByGroupId(String groupId) {

    return getBaseMapper().getEnablesByGroupId(groupId);
  }

  @Cacheable(value = ProductSalePropItemDto.CACHE_NAME_BY_PRODUCT_ID, key = "#productId", unless = "#result == null || #result.size() == 0")
  @Override
  public List<SalePropItemByProductDto> getByProductId(String productId) {

    return getBaseMapper().getByProductId(productId);
  }

  @CacheEvict(value = {ProductSalePropItemDto.CACHE_NAME,
      ProductSalePropItemDto.CACHE_NAME_BY_PRODUCT_ID}, key = "#key")
  @Override
  public void cleanCacheByKey(String key) {

  }
}
