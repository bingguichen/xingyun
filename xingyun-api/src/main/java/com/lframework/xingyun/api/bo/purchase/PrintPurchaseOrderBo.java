package com.lframework.xingyun.api.bo.purchase;

import com.lframework.common.constants.StringPool;
import com.lframework.common.utils.CollectionUtil;
import com.lframework.common.utils.DateUtil;
import com.lframework.common.utils.NumberUtil;
import com.lframework.common.utils.StringUtil;
import com.lframework.starter.web.bo.BaseBo;
import com.lframework.starter.web.bo.BasePrintDataBo;
import com.lframework.starter.web.service.IUserService;
import com.lframework.starter.web.utils.ApplicationUtil;
import com.lframework.xingyun.basedata.dto.product.info.PurchaseProductDto;
import com.lframework.xingyun.basedata.dto.storecenter.StoreCenterDto;
import com.lframework.xingyun.basedata.dto.supplier.SupplierDto;
import com.lframework.xingyun.basedata.service.product.IProductService;
import com.lframework.xingyun.basedata.service.storecenter.IStoreCenterService;
import com.lframework.xingyun.basedata.service.supplier.ISupplierService;
import com.lframework.xingyun.sc.dto.purchase.PurchaseOrderFullDto;
import com.lframework.xingyun.sc.enums.PurchaseOrderStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true)
public class PrintPurchaseOrderBo extends BasePrintDataBo<PurchaseOrderFullDto> {

    /**
     * 单号
     */
    private String code;

    /**
     * 仓库编号
     */
    private String scCode;

    /**
     * 仓库名称
     */
    private String scName;

    /**
     * 供应商编号
     */
    private String supplierCode;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 采购员姓名
     */
    private String purchaserName;

    /**
     * 预计到货日期
     */
    private String expectArriveDate;

    /**
     * 备注
     */
    private String description;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 审核人
     */
    private String approveBy;

    /**
     * 审核时间
     */
    private String approveTime;

    /**
     * 订单明细
     */
    private List<OrderDetailBo> details;

    public PrintPurchaseOrderBo() {

    }

    public PrintPurchaseOrderBo(PurchaseOrderFullDto dto) {

        super(dto);
    }

    @Override
    public BaseBo<PurchaseOrderFullDto> convert(PurchaseOrderFullDto dto) {

        return super.convert(dto, PrintPurchaseOrderBo::getDetails);
    }

    @Override
    protected void afterInit(PurchaseOrderFullDto dto) {

        this.purchaserName = StringPool.EMPTY_STR;
        this.expectArriveDate = StringPool.EMPTY_STR;
        this.approveBy = StringPool.EMPTY_STR;
        this.approveTime = StringPool.EMPTY_STR;

        IStoreCenterService storeCenterService = ApplicationUtil.getBean(IStoreCenterService.class);
        StoreCenterDto sc = storeCenterService.getById(dto.getScId());
        this.scCode = sc.getCode();
        this.scName = sc.getName();

        ISupplierService supplierService = ApplicationUtil.getBean(ISupplierService.class);
        SupplierDto supplier = supplierService.getById(dto.getSupplierId());
        this.supplierCode = supplier.getCode();
        this.supplierName = supplier.getName();

        IUserService userService = ApplicationUtil.getBean(IUserService.class);
        if (!StringUtil.isBlank(dto.getPurchaserId())) {
            this.purchaserName = userService.getById(dto.getPurchaserId()).getName();
        }

        if (dto.getExpectArriveDate() != null) {
            this.expectArriveDate = DateUtil.formatDate(dto.getExpectArriveDate());
        }

        this.createBy = userService.getById(dto.getCreateBy()).getName();
        this.createTime = DateUtil.formatDateTime(dto.getCreateTime());

        if (!StringUtil.isBlank(dto.getApproveBy()) && dto.getStatus() == PurchaseOrderStatus.APPROVE_PASS) {
            this.approveBy = userService.getById(dto.getApproveBy()).getName();
            this.approveTime = DateUtil.formatDateTime(dto.getApproveTime());
        }

        if (!CollectionUtil.isEmpty(dto.getDetails())) {
            this.details = dto.getDetails().stream().map(OrderDetailBo::new)
                    .collect(Collectors.toList());
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class OrderDetailBo extends BaseBo<PurchaseOrderFullDto.OrderDetailDto> {

        /**
         * 商品编号
         */
        private String productCode;

        /**
         * 商品名称
         */
        private String productName;

        /**
         * SKU编号
         */
        private String skuCode;

        /**
         * 外部编号
         */
        private String externalCode;

        /**
         * 采购数量
         */
        private Integer purchaseNum;

        /**
         * 采购价
         */
        private BigDecimal purchasePrice;

        /**
         * 采购金额
         */
        private BigDecimal purchaseAmount;

        public OrderDetailBo(PurchaseOrderFullDto.OrderDetailDto dto) {

            super(dto);
        }

        @Override
        public BaseBo<PurchaseOrderFullDto.OrderDetailDto> convert(PurchaseOrderFullDto.OrderDetailDto dto) {

            return super.convert(dto);
        }

        @Override
        protected void afterInit(PurchaseOrderFullDto.OrderDetailDto dto) {

            this.purchaseNum = dto.getOrderNum();
            this.purchasePrice = dto.getTaxPrice();
            this.purchaseAmount = NumberUtil.mul(dto.getOrderNum(), dto.getTaxPrice());

            IProductService productService = ApplicationUtil.getBean(IProductService.class);
            PurchaseProductDto product = productService.getPurchaseById(dto.getProductId());

            this.productCode = product.getCode();
            this.productName = product.getName();
            this.skuCode = product.getSkuCode();
            this.externalCode = product.getExternalCode();
        }
    }
}
