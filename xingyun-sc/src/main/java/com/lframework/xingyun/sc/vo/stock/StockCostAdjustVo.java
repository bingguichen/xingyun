package com.lframework.xingyun.sc.vo.stock;

import com.lframework.starter.web.vo.BaseVo;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StockCostAdjustVo implements BaseVo, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    @NotBlank(message = "商品ID不能为空！")
    private String productId;

    /**
     * 仓库ID
     */
    @NotBlank(message = "仓库ID不能为空！")
    private String scId;

    /**
     * 含税成本价
     */
    @NotNull(message = "含税成本价不能为空！")
    @Min(message = "含税成本价不能小于0！", value = 0)
    private BigDecimal taxPrice;

    /**
     * 税率（%）
     */
    @NotNull(message = "税率（%）不能为空！")
    @Min(message = "税率（%）不能小于0！", value = 0)
    private BigDecimal taxRate;

    /**
     * 出库时间
     */
    private LocalDateTime createTime = LocalDateTime.now();

    /**
     * 业务单据ID
     */
    private String bizId;

    /**
     * 业务单据明细ID
     */
    private String bizDetailId;

    /**
     * 业务单据号
     */
    private String bizCode;
}