package com.lframework.xingyun.api.bo.stock.lot;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lframework.common.constants.StringPool;
import com.lframework.starter.web.bo.BaseBo;
import com.lframework.starter.web.utils.ApplicationUtil;
import com.lframework.xingyun.basedata.dto.product.info.ProductDto;
import com.lframework.xingyun.basedata.dto.product.saleprop.item.SalePropItemByProductDto;
import com.lframework.xingyun.basedata.entity.StoreCenter;
import com.lframework.xingyun.basedata.entity.Supplier;
import com.lframework.xingyun.basedata.service.product.IProductSalePropItemRelationService;
import com.lframework.xingyun.basedata.service.product.IProductService;
import com.lframework.xingyun.basedata.service.storecenter.IStoreCenterService;
import com.lframework.xingyun.basedata.service.supplier.ISupplierService;
import com.lframework.xingyun.sc.dto.stock.ProductLotWithStockDto;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class QueryProductLotBo extends BaseBo<ProductLotWithStockDto> {

  /**
   * ID
   */
  @ApiModelProperty("ID")
  private String id;

  /**
   * 批次号
   */
  @ApiModelProperty("批次号")
  private String lotCode;

  /**
   * 仓库ID
   */
  @ApiModelProperty("仓库ID")
  private String scId;

  /**
   * 仓库编号
   */
  @ApiModelProperty("仓库编号")
  private String scCode;

  /**
   * 仓库名称
   */
  @ApiModelProperty("仓库名称")
  private String scName;

  /**
   * 商品ID
   */
  @ApiModelProperty("商品ID")
  private String productId;

  /**
   * 商品编号
   */
  @ApiModelProperty("商品编号")
  private String productCode;

  /**
   * 商品名称
   */
  @ApiModelProperty("商品名称")
  private String productName;

  /**
   * 商品类目
   */
  @ApiModelProperty("商品类目")
  private String categoryName;

  /**
   * 商品品牌
   */
  @ApiModelProperty("商品品牌")
  private String brandName;

  /**
   * 销售属性1
   */
  @ApiModelProperty("销售属性1")
  private String salePropItem1;

  /**
   * 销售属性2
   */
  @ApiModelProperty("销售属性2")
  private String salePropItem2;

  /**
   * 供应商ID
   */
  @ApiModelProperty("供应商ID")
  private String supplierId;

  /**
   * 供应商编号
   */
  @ApiModelProperty("供应商编号")
  private String supplierCode;

  /**
   * 供应商名称
   */
  @ApiModelProperty("供应商名称")
  private String supplierName;

  /**
   * 库存数量
   */
  @ApiModelProperty("库存数量")
  private Integer stockNum;

  /**
   * 税率（%）
   */
  @ApiModelProperty("税率（%）")
  private BigDecimal taxRate;

  /**
   * 创建时间
   */
  @ApiModelProperty("创建时间")
  @JsonFormat(pattern = StringPool.DATE_TIME_PATTERN)
  private LocalDateTime createTime;

  public QueryProductLotBo() {

  }

  public QueryProductLotBo(ProductLotWithStockDto dto) {

    super(dto);
  }

  @Override
  protected void afterInit(ProductLotWithStockDto dto) {

    IStoreCenterService storeCenterService = ApplicationUtil.getBean(IStoreCenterService.class);
    StoreCenter sc = storeCenterService.findById(dto.getScId());
    this.scCode = sc.getCode();
    this.scName = sc.getName();

    ISupplierService supplierService = ApplicationUtil.getBean(ISupplierService.class);
    Supplier supplier = supplierService.findById(dto.getSupplierId());
    this.supplierCode = supplier.getCode();
    this.supplierName = supplier.getName();

    IProductService productService = ApplicationUtil.getBean(IProductService.class);
    ProductDto product = productService.findById(dto.getProductId());
    this.productCode = product.getCode();
    this.productName = product.getName();
    this.categoryName = product.getPoly().getCategoryName();
    this.brandName = product.getPoly().getBrandName();
    if (product.getPoly().getMultiSaleProp()) {
      IProductSalePropItemRelationService productSalePropItemRelationService = ApplicationUtil.getBean(
          IProductSalePropItemRelationService.class);
      SalePropItemByProductDto saleProps = productSalePropItemRelationService.getByProductId(
          product.getId());
      this.salePropItem1 = saleProps.getItemName1();
      this.salePropItem2 = saleProps.getItemName2();
    }
    this.stockNum = dto.getStockNum();
  }
}
