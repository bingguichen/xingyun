package com.lframework.xingyun.api.bo.stock.take.sheet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lframework.common.utils.StringUtil;
import com.lframework.starter.web.bo.BaseBo;
import com.lframework.starter.web.utils.ApplicationUtil;
import com.lframework.xingyun.basedata.dto.product.info.TakeStockSheetProductDto;
import com.lframework.xingyun.sc.dto.stock.ProductStockDto;
import com.lframework.xingyun.sc.dto.stock.take.config.TakeStockConfigDto;
import com.lframework.xingyun.sc.dto.stock.take.plan.GetTakeStockPlanDetailProductDto;
import com.lframework.xingyun.sc.service.stock.IProductStockService;
import com.lframework.xingyun.sc.service.stock.take.ITakeStockConfigService;
import com.lframework.xingyun.sc.service.stock.take.ITakeStockPlanDetailService;
import com.lframework.xingyun.sc.service.stock.take.ITakeStockPlanService;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TakeStockSheetProductBo extends BaseBo<TakeStockSheetProductDto> {

    /**
     * ID
     */
    private String productId;

    /**
     * 编号
     */
    private String productCode;

    /**
     * 名称
     */
    private String productName;

    /**
     * 类目名称
     */
    private String categoryName;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * SKU
     */
    private String skuCode;

    /**
     * 外部编号
     */
    private String externalCode;

    /**
     * 规格
     */
    private String spec;

    /**
     * 单位
     */
    private String unit;

    /**
     * 库存数量
     */
    private Integer stockNum;

    /**
     * 盘点任务ID
     */
    @JsonIgnore
    private String planId;

    /**
     * 仓库ID
     */
    @JsonIgnore
    private String scId;

    public TakeStockSheetProductBo(TakeStockSheetProductDto dto, String planId, String scId) {
        this.planId = planId;
        this.scId = scId;

        if (dto != null) {
            this.convert(dto);

            this.afterInit(dto);
        }
    }

    @Override
    protected void afterInit(TakeStockSheetProductDto dto) {

        this.productId = dto.getId();
        this.productCode = dto.getCode();
        this.productName = dto.getName();

        ITakeStockConfigService takeStockConfigService = ApplicationUtil.getBean(ITakeStockConfigService.class);
        TakeStockConfigDto config = takeStockConfigService.get();
        if (config.getShowStock()) {
            if (!StringUtil.isBlank(this.planId)) {
                ITakeStockPlanDetailService takeStockPlanDetailService = ApplicationUtil.getBean(ITakeStockPlanDetailService.class);
                GetTakeStockPlanDetailProductDto product = takeStockPlanDetailService.getByPlanIdAndProductId(this.planId, this.productId);
                this.stockNum = product.getStockNum();
            } else {
                IProductStockService productStockService = ApplicationUtil.getBean(IProductStockService.class);
                ProductStockDto productStock = productStockService.getByProductIdAndScId(this.productId, this.scId);
                this.stockNum = productStock == null ? 0 : productStock.getStockNum();
            }
        }
    }
}
