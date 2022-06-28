package com.lframework.xingyun.sc.impl.sale;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import com.lframework.common.constants.StringPool;
import com.lframework.common.exceptions.ClientException;
import com.lframework.common.exceptions.impl.DefaultClientException;
import com.lframework.common.exceptions.impl.InputErrorException;
import com.lframework.common.utils.Assert;
import com.lframework.common.utils.CollectionUtil;
import com.lframework.common.utils.NumberUtil;
import com.lframework.common.utils.StringUtil;
import com.lframework.starter.mybatis.annotations.OpLog;
import com.lframework.starter.mybatis.enums.OpLogType;
import com.lframework.starter.mybatis.impl.BaseMpServiceImpl;
import com.lframework.starter.mybatis.resp.PageResult;
import com.lframework.starter.mybatis.service.IUserService;
import com.lframework.starter.mybatis.utils.OpLogUtil;
import com.lframework.starter.mybatis.utils.PageHelperUtil;
import com.lframework.starter.mybatis.utils.PageResultUtil;
import com.lframework.starter.web.dto.UserDto;
import com.lframework.starter.web.service.IGenerateCodeService;
import com.lframework.starter.web.utils.ApplicationUtil;
import com.lframework.starter.web.utils.IdUtil;
import com.lframework.web.common.security.SecurityUtil;
import com.lframework.xingyun.basedata.dto.product.info.ProductDto;
import com.lframework.xingyun.basedata.entity.Customer;
import com.lframework.xingyun.basedata.entity.StoreCenter;
import com.lframework.xingyun.basedata.service.customer.ICustomerService;
import com.lframework.xingyun.basedata.service.product.IProductService;
import com.lframework.xingyun.basedata.service.storecenter.IStoreCenterService;
import com.lframework.xingyun.core.events.order.impl.ApprovePassSaleOrderEvent;
import com.lframework.xingyun.sc.components.code.GenerateCodeTypePool;
import com.lframework.xingyun.sc.dto.sale.SaleOrderFullDto;
import com.lframework.xingyun.sc.dto.sale.SaleOrderWithOutDto;
import com.lframework.xingyun.sc.entity.SaleConfig;
import com.lframework.xingyun.sc.entity.SaleOrder;
import com.lframework.xingyun.sc.entity.SaleOrderDetail;
import com.lframework.xingyun.sc.enums.SaleOrderStatus;
import com.lframework.xingyun.sc.mappers.SaleOrderMapper;
import com.lframework.xingyun.sc.service.sale.ISaleConfigService;
import com.lframework.xingyun.sc.service.sale.ISaleOrderDetailService;
import com.lframework.xingyun.sc.service.sale.ISaleOrderService;
import com.lframework.xingyun.sc.vo.sale.ApprovePassSaleOrderVo;
import com.lframework.xingyun.sc.vo.sale.ApproveRefuseSaleOrderVo;
import com.lframework.xingyun.sc.vo.sale.BatchApprovePassSaleOrderVo;
import com.lframework.xingyun.sc.vo.sale.BatchApproveRefuseSaleOrderVo;
import com.lframework.xingyun.sc.vo.sale.CreateSaleOrderVo;
import com.lframework.xingyun.sc.vo.sale.QuerySaleOrderVo;
import com.lframework.xingyun.sc.vo.sale.QuerySaleOrderWithOutVo;
import com.lframework.xingyun.sc.vo.sale.SaleOrderSelectorVo;
import com.lframework.xingyun.sc.vo.sale.SaleProductVo;
import com.lframework.xingyun.sc.vo.sale.UpdateSaleOrderVo;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SaleOrderServiceImpl extends BaseMpServiceImpl<SaleOrderMapper, SaleOrder> implements
    ISaleOrderService {

  @Autowired
  private ISaleOrderDetailService saleOrderDetailService;

  @Autowired
  private IGenerateCodeService generateCodeService;

  @Autowired
  private IStoreCenterService storeCenterService;

  @Autowired
  private ICustomerService customerService;

  @Autowired
  private IUserService userService;

  @Autowired
  private IProductService productService;

  @Autowired
  private ISaleConfigService saleConfigService;

  @Override
  public PageResult<SaleOrder> query(Integer pageIndex, Integer pageSize, QuerySaleOrderVo vo) {

    Assert.greaterThanZero(pageIndex);
    Assert.greaterThanZero(pageSize);

    PageHelperUtil.startPage(pageIndex, pageSize);
    List<SaleOrder> datas = this.query(vo);

    return PageResultUtil.convert(new PageInfo<>(datas));
  }

  @Override
  public List<SaleOrder> query(QuerySaleOrderVo vo) {

    return getBaseMapper().query(vo);
  }

  @Override
  public PageResult<SaleOrder> selector(Integer pageIndex, Integer pageSize,
      SaleOrderSelectorVo vo) {

    Assert.greaterThanZero(pageIndex);
    Assert.greaterThanZero(pageSize);

    PageHelperUtil.startPage(pageIndex, pageSize);
    List<SaleOrder> datas = getBaseMapper().selector(vo);

    return PageResultUtil.convert(new PageInfo<>(datas));
  }

  @Override
  public SaleOrderFullDto getDetail(String id) {

    SaleOrderFullDto order = getBaseMapper().getDetail(id);
    if (order == null) {
      throw new InputErrorException("订单不存在！");
    }

    return order;
  }

  @Override
  public SaleOrderWithOutDto getWithOut(String id) {

    SaleConfig saleConfig = saleConfigService.get();

    SaleOrderWithOutDto order = getBaseMapper().getWithOut(id, saleConfig.getOutStockRequireSale());
    if (order == null) {
      throw new InputErrorException("订单不存在！");
    }
    return order;
  }

  @Override
  public PageResult<SaleOrder> queryWithOut(Integer pageIndex, Integer pageSize,
      QuerySaleOrderWithOutVo vo) {

    Assert.greaterThanZero(pageIndex);
    Assert.greaterThanZero(pageSize);

    SaleConfig saleConfig = saleConfigService.get();

    PageHelperUtil.startPage(pageIndex, pageSize);
    List<SaleOrder> datas = getBaseMapper().queryWithOut(vo,
        saleConfig.getOutStockMultipleRelateSale());

    return PageResultUtil.convert(new PageInfo<>(datas));
  }

  @OpLog(type = OpLogType.OTHER, name = "创建订单，单号：{}", params = "#code")
  @Transactional
  @Override
  public String create(CreateSaleOrderVo vo) {

    SaleOrder order = new SaleOrder();
    order.setId(IdUtil.getId());
    order.setCode(generateCodeService.generate(GenerateCodeTypePool.SALE_ORDER));

    this.create(order, vo);

    order.setStatus(SaleOrderStatus.CREATED);

    getBaseMapper().insert(order);

    OpLogUtil.setVariable("code", order.getCode());
    OpLogUtil.setExtra(vo);

    return order.getId();
  }

  @OpLog(type = OpLogType.OTHER, name = "修改订单，单号：{}", params = "#code")
  @Transactional
  @Override
  public void update(UpdateSaleOrderVo vo) {

    SaleOrder order = getBaseMapper().selectById(vo.getId());
    if (order == null) {
      throw new InputErrorException("订单不存在！");
    }

    if (order.getStatus() != SaleOrderStatus.CREATED
        && order.getStatus() != SaleOrderStatus.APPROVE_REFUSE) {

      if (order.getStatus() == SaleOrderStatus.APPROVE_PASS) {
        throw new DefaultClientException("订单已审核通过，无法修改！");
      }

      throw new DefaultClientException("订单无法修改！");
    }

    // 删除订单明细
    Wrapper<SaleOrderDetail> deleteDetailWrapper = Wrappers.lambdaQuery(SaleOrderDetail.class)
        .eq(SaleOrderDetail::getOrderId, order.getId());
    saleOrderDetailService.remove(deleteDetailWrapper);

    this.create(order, vo);

    order.setStatus(SaleOrderStatus.CREATED);

    List<SaleOrderStatus> statusList = new ArrayList<>();
    statusList.add(SaleOrderStatus.CREATED);
    statusList.add(SaleOrderStatus.APPROVE_REFUSE);

    Wrapper<SaleOrder> updateOrderWrapper = Wrappers.lambdaUpdate(SaleOrder.class)
        .set(SaleOrder::getApproveBy, null).set(SaleOrder::getApproveTime, null)
        .set(SaleOrder::getRefuseReason, StringPool.EMPTY_STR).eq(SaleOrder::getId, order.getId())
        .in(SaleOrder::getStatus, statusList);
    if (getBaseMapper().update(order, updateOrderWrapper) != 1) {
      throw new DefaultClientException("订单信息已过期，请刷新重试！");
    }

    OpLogUtil.setVariable("code", order.getCode());
    OpLogUtil.setExtra(vo);
  }

  @OpLog(type = OpLogType.OTHER, name = "审核通过订单，单号：{}", params = "#code")
  @Transactional
  @Override
  public void approvePass(ApprovePassSaleOrderVo vo) {

    SaleOrder order = getBaseMapper().selectById(vo.getId());
    if (order == null) {
      throw new InputErrorException("订单不存在！");
    }

    if (order.getStatus() != SaleOrderStatus.CREATED
        && order.getStatus() != SaleOrderStatus.APPROVE_REFUSE) {

      if (order.getStatus() == SaleOrderStatus.APPROVE_PASS) {
        throw new DefaultClientException("订单已审核通过，不允许继续执行审核！");
      }

      throw new DefaultClientException("订单无法审核通过！");
    }

    order.setStatus(SaleOrderStatus.APPROVE_PASS);

    List<SaleOrderStatus> statusList = new ArrayList<>();
    statusList.add(SaleOrderStatus.CREATED);
    statusList.add(SaleOrderStatus.APPROVE_REFUSE);

    LambdaUpdateWrapper<SaleOrder> updateOrderWrapper = Wrappers.lambdaUpdate(SaleOrder.class)
        .set(SaleOrder::getApproveBy, SecurityUtil.getCurrentUser().getId())
        .set(SaleOrder::getApproveTime, LocalDateTime.now()).eq(SaleOrder::getId, order.getId())
        .in(SaleOrder::getStatus, statusList);
    if (!StringUtil.isBlank(vo.getDescription())) {
      updateOrderWrapper.set(SaleOrder::getDescription, vo.getDescription());
    }
    if (getBaseMapper().update(order, updateOrderWrapper) != 1) {
      throw new DefaultClientException("订单信息已过期，请刷新重试！");
    }

    OpLogUtil.setVariable("code", order.getCode());
    OpLogUtil.setExtra(vo);

    this.sendApprovePassEvent(order);
  }

  @Transactional
  @Override
  public void batchApprovePass(BatchApprovePassSaleOrderVo vo) {

    int orderNo = 1;
    for (String id : vo.getIds()) {
      ApprovePassSaleOrderVo approvePassSaleOrderVo = new ApprovePassSaleOrderVo();
      approvePassSaleOrderVo.setId(id);

      try {
        ISaleOrderService thisService = getThis(this.getClass());
        thisService.approvePass(approvePassSaleOrderVo);
      } catch (ClientException e) {
        throw new DefaultClientException("第" + orderNo + "个销售订单审核通过失败，失败原因：" + e.getMsg());
      }

      orderNo++;
    }
  }

  @Transactional
  @Override
  public void directApprovePass(CreateSaleOrderVo vo) {

    ISaleOrderService thisService = getThis(this.getClass());

    String orderId = thisService.create(vo);

    ApprovePassSaleOrderVo approvePassSaleOrderVo = new ApprovePassSaleOrderVo();
    approvePassSaleOrderVo.setId(orderId);
    approvePassSaleOrderVo.setDescription(vo.getDescription());

    thisService.approvePass(approvePassSaleOrderVo);
  }

  @OpLog(type = OpLogType.OTHER, name = "审核拒绝订单，单号：{}", params = "#code")
  @Transactional
  @Override
  public void approveRefuse(ApproveRefuseSaleOrderVo vo) {

    SaleOrder order = getBaseMapper().selectById(vo.getId());
    if (order == null) {
      throw new InputErrorException("订单不存在！");
    }

    if (order.getStatus() != SaleOrderStatus.CREATED) {

      if (order.getStatus() == SaleOrderStatus.APPROVE_PASS) {
        throw new DefaultClientException("订单已审核通过，不允许继续执行审核！");
      }

      if (order.getStatus() == SaleOrderStatus.APPROVE_REFUSE) {
        throw new DefaultClientException("订单已审核拒绝，不允许继续执行审核！");
      }

      throw new DefaultClientException("订单无法审核拒绝！");
    }

    order.setStatus(SaleOrderStatus.APPROVE_REFUSE);

    LambdaUpdateWrapper<SaleOrder> updateOrderWrapper = Wrappers.lambdaUpdate(SaleOrder.class)
        .set(SaleOrder::getApproveBy, SecurityUtil.getCurrentUser().getId())
        .set(SaleOrder::getApproveTime, LocalDateTime.now())
        .set(SaleOrder::getRefuseReason, vo.getRefuseReason()).eq(SaleOrder::getId, order.getId())
        .eq(SaleOrder::getStatus, SaleOrderStatus.CREATED);
    if (getBaseMapper().update(order, updateOrderWrapper) != 1) {
      throw new DefaultClientException("订单信息已过期，请刷新重试！");
    }

    OpLogUtil.setVariable("code", order.getCode());
    OpLogUtil.setExtra(vo);
  }

  @Transactional
  @Override
  public void batchApproveRefuse(BatchApproveRefuseSaleOrderVo vo) {

    int orderNo = 1;
    for (String id : vo.getIds()) {
      ApproveRefuseSaleOrderVo approveRefuseSaleOrderVo = new ApproveRefuseSaleOrderVo();
      approveRefuseSaleOrderVo.setId(id);
      approveRefuseSaleOrderVo.setRefuseReason(vo.getRefuseReason());

      try {
        ISaleOrderService thisService = getThis(this.getClass());
        thisService.approveRefuse(approveRefuseSaleOrderVo);
      } catch (ClientException e) {
        throw new DefaultClientException("第" + orderNo + "个销售订单审核拒绝失败，失败原因：" + e.getMsg());
      }

      orderNo++;
    }
  }

  @OpLog(type = OpLogType.OTHER, name = "删除订单，单号：{}", params = "#code")
  @Transactional
  @Override
  public void deleteById(String id) {

    Assert.notBlank(id);
    SaleOrder order = getBaseMapper().selectById(id);
    if (order == null) {
      throw new InputErrorException("订单不存在！");
    }

    if (order.getStatus() != SaleOrderStatus.CREATED
        && order.getStatus() != SaleOrderStatus.APPROVE_REFUSE) {

      if (order.getStatus() == SaleOrderStatus.APPROVE_PASS) {
        throw new DefaultClientException("“审核通过”的销售单据不允许执行删除操作！");
      }

      throw new DefaultClientException("订单无法删除！");
    }

    // 删除订单明细
    Wrapper<SaleOrderDetail> deleteDetailWrapper = Wrappers.lambdaQuery(SaleOrderDetail.class)
        .eq(SaleOrderDetail::getOrderId, order.getId());
    saleOrderDetailService.remove(deleteDetailWrapper);

    // 删除订单
    getBaseMapper().deleteById(id);

    OpLogUtil.setVariable("code", order.getCode());
  }

  @Transactional
  @Override
  public void deleteByIds(List<String> ids) {

    if (!CollectionUtil.isEmpty(ids)) {
      int orderNo = 1;
      for (String id : ids) {

        try {
          ISaleOrderService thisService = getThis(this.getClass());
          thisService.deleteById(id);
        } catch (ClientException e) {
          throw new DefaultClientException("第" + orderNo + "个销售订单删除失败，失败原因：" + e.getMsg());
        }

        orderNo++;
      }
    }
  }

  private void create(SaleOrder order, CreateSaleOrderVo vo) {

    StoreCenter sc = storeCenterService.findById(vo.getScId());
    if (sc == null) {
      throw new InputErrorException("仓库不存在！");
    }

    order.setScId(vo.getScId());

    Customer customer = customerService.findById(vo.getCustomerId());
    if (customer == null) {
      throw new InputErrorException("客户不存在！");
    }
    order.setCustomerId(vo.getCustomerId());

    if (!StringUtil.isBlank(vo.getSalerId())) {
      UserDto saler = userService.findById(vo.getSalerId());
      if (saler == null) {
        throw new InputErrorException("销售员不存在！");
      }

      order.setSalerId(vo.getSalerId());
    }

    int totalNum = 0;
    int giftNum = 0;
    BigDecimal totalAmount = BigDecimal.ZERO;
    int orderNo = 1;
    for (SaleProductVo productVo : vo.getProducts()) {

      boolean isGift = productVo.getTaxPrice().doubleValue() == 0D;

      if (isGift) {
        giftNum += productVo.getOrderNum();
      } else {
        totalNum += productVo.getOrderNum();
      }

      totalAmount = NumberUtil.add(totalAmount,
          NumberUtil.mul(productVo.getTaxPrice(), productVo.getOrderNum()));

      SaleOrderDetail orderDetail = new SaleOrderDetail();
      orderDetail.setId(IdUtil.getId());
      orderDetail.setOrderId(order.getId());

      ProductDto product = productService.findById(productVo.getProductId());
      if (product == null) {
        throw new InputErrorException("第" + orderNo + "行商品不存在！");
      }

      if (!NumberUtil.isNumberPrecision(productVo.getTaxPrice(), 2)) {
        throw new InputErrorException("第" + orderNo + "行商品价格最多允许2位小数！");
      }

      orderDetail.setProductId(productVo.getProductId());
      orderDetail.setOrderNum(productVo.getOrderNum());
      orderDetail.setOriPrice(productVo.getOriPrice());
      orderDetail.setDiscountRate(productVo.getDiscountRate());
      orderDetail.setTaxPrice(productVo.getTaxPrice());
      orderDetail.setIsGift(isGift);
      orderDetail.setTaxRate(product.getPoly().getSaleTaxRate());
      orderDetail.setDescription(
          StringUtil.isBlank(productVo.getDescription()) ? StringPool.EMPTY_STR
              : productVo.getDescription());
      orderDetail.setOrderNo(orderNo);

      saleOrderDetailService.save(orderDetail);
      orderNo++;
    }
    order.setTotalNum(totalNum);
    order.setTotalGiftNum(giftNum);
    order.setTotalAmount(totalAmount);
    order.setDescription(
        StringUtil.isBlank(vo.getDescription()) ? StringPool.EMPTY_STR : vo.getDescription());
  }

  private void sendApprovePassEvent(SaleOrder order) {

    ApprovePassSaleOrderEvent event = new ApprovePassSaleOrderEvent(this);
    event.setId(order.getId());
    event.setTotalAmount(order.getTotalAmount());
    event.setApproveTime(order.getApproveTime());

    ApplicationUtil.publishEvent(event);
  }
}
