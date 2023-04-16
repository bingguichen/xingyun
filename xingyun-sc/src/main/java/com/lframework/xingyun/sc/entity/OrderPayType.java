package com.lframework.xingyun.sc.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lframework.starter.mybatis.entity.BaseEntity;
import com.lframework.starter.web.dto.BaseDto;
import com.lframework.xingyun.basedata.enums.AddressType;
import com.lframework.xingyun.sc.enums.OrderAddressOrderType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author zmj
 * @since 2023-03-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tbl_order_pay_type")
public class OrderPayType extends BaseEntity implements BaseDto {

  public static final String CACHE_NAME = "OrderPayType";
  private static final long serialVersionUID = 1L;

  /**
   * ID
   */
  private String id;

  /**
   * 订单ID
   */
  private String orderId;

  /**
   * 支付方式ID
   */
  private String payTypeId;

  /**
   * 支付金额
   */
  private BigDecimal payAmount;

  /**
   * 支付内容
   */
  private String text;
}