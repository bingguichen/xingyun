package com.lframework.xingyun.api.bo.stock.take.sheet;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lframework.common.constants.StringPool;
import com.lframework.common.utils.StringUtil;
import com.lframework.starter.web.bo.BaseBo;
import com.lframework.starter.web.service.IUserService;
import com.lframework.starter.web.utils.ApplicationUtil;
import com.lframework.xingyun.basedata.dto.product.brand.ProductBrandDto;
import com.lframework.xingyun.basedata.dto.product.category.ProductCategoryDto;
import com.lframework.xingyun.basedata.dto.product.info.ProductDto;
import com.lframework.xingyun.basedata.dto.storecenter.StoreCenterDto;
import com.lframework.xingyun.basedata.service.product.IProductBrandService;
import com.lframework.xingyun.basedata.service.product.IProductCategoryService;
import com.lframework.xingyun.basedata.service.product.IProductService;
import com.lframework.xingyun.basedata.service.storecenter.IStoreCenterService;
import com.lframework.xingyun.sc.dto.stock.take.config.TakeStockConfigDto;
import com.lframework.xingyun.sc.dto.stock.take.plan.GetTakeStockPlanDetailProductDto;
import com.lframework.xingyun.sc.dto.stock.take.plan.TakeStockPlanDto;
import com.lframework.xingyun.sc.dto.stock.take.pre.PreTakeStockSheetDto;
import com.lframework.xingyun.sc.dto.stock.take.sheet.TakeStockSheetFullDto;
import com.lframework.xingyun.sc.enums.TakeStockPlanType;
import com.lframework.xingyun.sc.service.stock.take.IPreTakeStockSheetService;
import com.lframework.xingyun.sc.service.stock.take.ITakeStockConfigService;
import com.lframework.xingyun.sc.service.stock.take.ITakeStockPlanDetailService;
import com.lframework.xingyun.sc.service.stock.take.ITakeStockPlanService;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 盘点单详情 Bo
 * </p>
 *
 * @author zmj
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TakeStockSheetFullBo extends BaseBo<TakeStockSheetFullDto> {

    /**
     * ID
     */
    private String id;

    /**
     * 业务单据号
     */
    private String code;

    /**
     * 盘点任务ID
     */
    private String planId;

    /**
     * 盘点任务号
     */
    private String planCode;

    /**
     * 预先盘点单ID
     */
    private String preSheetId;

    /**
     * 预先盘点单号
     */
    private String preSheetCode;

    /**
     * 仓库名称
     */
    private String scName;

    /**
     * 盘点任务-盘点类别
     */
    private Integer takeType;

    /**
     * 业务名称
     */
    private String bizName;

    /**
     * 盘点任务-盘点状态
     */
    private Integer takeStatus;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 备注
     */
    private String description;

    /**
     * 拒绝理由
     */
    private String refuseReason;

    /**
     * 修改人
     */
    private String updateBy;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = StringPool.DATE_TIME_PATTERN)
    private LocalDateTime updateTime;

    /**
     * 审核人
     */
    private String approveBy;

    /**
     * 审核时间
     */
    @JsonFormat(pattern = StringPool.DATE_TIME_PATTERN)
    private LocalDateTime approveTime;

    /**
     * 明细
     */
    private List<SheetDetailBo> details;

    public TakeStockSheetFullBo(TakeStockSheetFullDto dto) {

        super(dto);
    }

    @Override
    public <A> BaseBo<TakeStockSheetFullDto> convert(TakeStockSheetFullDto dto) {
        return super.convert(dto, TakeStockSheetFullBo::getTakeStatus, TakeStockSheetFullBo::getStatus);
    }

    @Override
    protected void afterInit(TakeStockSheetFullDto dto) {

        this.status = dto.getStatus().getCode();

        ITakeStockPlanService takeStockPlanService = ApplicationUtil.getBean(ITakeStockPlanService.class);
        TakeStockPlanDto plan = takeStockPlanService.getById(dto.getPlanId());
        this.planCode = plan.getCode();
        this.takeType = plan.getTakeType().getCode();
        this.takeStatus = plan.getTakeStatus().getCode();

        String bizId = plan.getBizId();
        if (plan.getTakeType() == TakeStockPlanType.CATEGORY) {
            IProductCategoryService productCategoryService = ApplicationUtil.getBean(IProductCategoryService.class);
            String[] categoryIds = bizId.split(",");
            StringBuilder builder = new StringBuilder();
            for (String categoryId : categoryIds) {
                ProductCategoryDto productCategory = productCategoryService.getById(categoryId);
                builder.append(productCategory.getName()).append(StringPool.STR_SPLIT_CN);
            }

            if (builder.length() > 0) {
                builder.setLength(builder.length() - 1);
            }

            this.bizName = builder.toString();
        } else if (plan.getTakeType() == TakeStockPlanType.BRAND) {
            IProductBrandService productBrandService = ApplicationUtil.getBean(IProductBrandService.class);
            String[] brandIds = bizId.split(",");
            StringBuilder builder = new StringBuilder();
            for (String brandId : brandIds) {
                ProductBrandDto productBrand = productBrandService.getById(brandId);
                builder.append(productBrand.getName()).append(StringPool.STR_SPLIT_CN);
            }

            if (builder.length() > 0) {
                builder.setLength(builder.length() - 1);
            }

            this.bizName = builder.toString();
        }

        IPreTakeStockSheetService preTakeStockSheetService = ApplicationUtil.getBean(IPreTakeStockSheetService.class);
        PreTakeStockSheetDto preSheet =preTakeStockSheetService.getById(dto.getPreSheetId());
        this.preSheetCode = preSheet.getCode();

        IStoreCenterService storeCenterService = ApplicationUtil.getBean(IStoreCenterService.class);
        StoreCenterDto sc = storeCenterService.getById(dto.getScId());
        this.scName = sc.getName();

        IUserService userService = ApplicationUtil.getBean(IUserService.class);
        this.updateBy = userService.getById(this.updateBy).getName();
        if (!StringUtil.isBlank(this.approveBy)) {
            this.approveBy = userService.getById(this.approveBy).getName();
        }

        this.details = dto.getDetails().stream().map(t -> new SheetDetailBo(t, this.planId)).collect(Collectors.toList());
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class SheetDetailBo extends BaseBo<TakeStockSheetFullDto.SheetDetailDto> {
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
         * 盘点数量
         */
        private Integer takeNum;

        /**
         * 备注
         */
        private String description;

        /**
         * 盘点任务ID
         */
        private String planId;

        public SheetDetailBo(TakeStockSheetFullDto.SheetDetailDto dto, String planId) {
            this.planId = planId;

            if (dto != null) {
                this.convert(dto);

                this.afterInit(dto);
            }
        }

        @Override
        protected void afterInit(TakeStockSheetFullDto.SheetDetailDto dto) {
            IProductService productService = ApplicationUtil.getBean(IProductService.class);
            ProductDto product = productService.getById(dto.getProductId());
            this.productId = product.getId();
            this.productCode = product.getCode();
            this.productName = product.getName();
            this.categoryName = product.getPoly().getCategoryName();
            this.brandName = product.getPoly().getBrandName();
            this.skuCode = product.getSkuCode();
            this.externalCode = product.getExternalCode();
            this.spec = product.getSpec();
            this.unit = product.getUnit();

            ITakeStockConfigService takeStockConfigService = ApplicationUtil.getBean(ITakeStockConfigService.class);
            TakeStockConfigDto config = takeStockConfigService.get();
            if (config.getShowStock()) {
                ITakeStockPlanDetailService takeStockPlanDetailService = ApplicationUtil.getBean(ITakeStockPlanDetailService.class);
                GetTakeStockPlanDetailProductDto planDetail = takeStockPlanDetailService.getByPlanIdAndProductId(this.planId, this.productId);
                this.stockNum = planDetail.getStockNum();
            }
        }
    }
}