package com.lframework.xingyun.api.model.stock.adjust;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.lframework.common.constants.StringPool;
import com.lframework.common.utils.StringUtil;
import com.lframework.starter.mybatis.service.IUserService;
import com.lframework.starter.web.bo.BaseBo;
import com.lframework.starter.web.components.excel.ExcelModel;
import com.lframework.starter.web.utils.ApplicationUtil;
import com.lframework.xingyun.basedata.dto.storecenter.StoreCenterDto;
import com.lframework.xingyun.basedata.service.storecenter.IStoreCenterService;
import com.lframework.xingyun.sc.dto.stock.adjust.StockCostAdjustSheetDto;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class StockCostAdjustSheetExportModel extends BaseBo<StockCostAdjustSheetDto> implements
    ExcelModel {

  /**
   * 单号
   */
  @ExcelProperty("业务单据号")
  private String code;

  /**
   * 仓库编号
   */
  @ExcelProperty("仓库编号")
  private String scCode;

  /**
   * 仓库名称
   */
  @ExcelProperty("仓库名称")
  private String scName;

  /**
   * 调价品种数
   */
  @ExcelProperty("调价品种数")
  private Integer productNum;

  /**
   * 库存调价差额
   */
  @ExcelProperty("库存调价差额")
  private BigDecimal diffAmount;

  /**
   * 修改时间
   */
  @DateTimeFormat(StringPool.DATE_TIME_PATTERN)
  @ExcelProperty("操作时间")
  private Date updateTime;

  /**
   * 修改人
   */
  @ExcelProperty("操作人")
  private String updateBy;

  /**
   * 状态
   */
  @ExcelProperty("状态")
  private String status;

  /**
   * 审核时间
   */
  @DateTimeFormat(StringPool.DATE_TIME_PATTERN)
  @ExcelProperty("审核时间")
  private Date approveTime;

  /**
   * 审核人
   */
  @ExcelProperty("审核人")
  private String approveBy;

  /**
   * 备注
   */
  @ExcelProperty("备注")
  private String description;

  public StockCostAdjustSheetExportModel(StockCostAdjustSheetDto dto) {

    super(dto);
  }

  @Override
  public <A> BaseBo<StockCostAdjustSheetDto> convert(StockCostAdjustSheetDto dto) {

    return super.convert(dto);
  }

  @Override
  protected void afterInit(StockCostAdjustSheetDto dto) {

    this.status = dto.getStatus().getDesc();

    IStoreCenterService storeCenterService = ApplicationUtil.getBean(IStoreCenterService.class);
    StoreCenterDto sc = storeCenterService.getById(dto.getScId());
    this.scCode = sc.getCode();
    this.scName = sc.getName();

    IUserService userService = ApplicationUtil.getBean(IUserService.class);
    this.updateBy = userService.getById(dto.getUpdateBy()).getName();
    if (!StringUtil.isBlank(dto.getApproveBy())) {
      this.approveBy = userService.getById(dto.getApproveBy()).getName();
    }
  }
}
