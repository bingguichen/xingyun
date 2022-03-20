package com.lframework.xingyun.api.bo.purchase.receive;

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
import com.lframework.xingyun.sc.dto.purchase.PurchaseOrderDto;
import com.lframework.xingyun.sc.dto.purchase.receive.ReceiveSheetFullDto;
import com.lframework.xingyun.sc.enums.ReceiveSheetStatus;
import com.lframework.xingyun.sc.service.purchase.IPurchaseOrderService;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true)
public class PrintReceiveSheetBo extends BasePrintDataBo<ReceiveSheetFullDto> {

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
     * 付款日期
     */
    private String paymentDate;

    /**
     * 到货日期
     */
    private String receiveDate;

    /**
     * 采购订单号
     */
    private String purchaseOrderCode;

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

    public PrintReceiveSheetBo() {

    }

    public PrintReceiveSheetBo(ReceiveSheetFullDto dto) {

        super(dto);
    }

    @Override
    public BaseBo<ReceiveSheetFullDto> convert(ReceiveSheetFullDto dto) {

        return super.convert(dto, PrintReceiveSheetBo::getDetails);
    }

    @Override
    protected void afterInit(ReceiveSheetFullDto dto) {
        this.purchaserName = StringPool.EMPTY_STR;
        this.paymentDate = StringPool.EMPTY_STR;
        this.receiveDate = StringPool.EMPTY_STR;
        this.purchaseOrderCode = StringPool.EMPTY_STR;
        this.description = StringPool.EMPTY_STR;
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

        IPurchaseOrderService purchaseOrderService = ApplicationUtil.getBean(IPurchaseOrderService.class);
        if (!StringUtil.isBlank(dto.getPurchaseOrderId())) {
            PurchaseOrderDto purchaseOrder = purchaseOrderService.getById(dto.getPurchaseOrderId());
            this.purchaseOrderCode = purchaseOrder.getCode();
        }

        if (dto.getPaymentDate() != null) {
            this.paymentDate = DateUtil.formatDate(dto.getPaymentDate());
        }

        if (dto.getReceiveDate() != null) {
            this.receiveDate = DateUtil.formatDate(dto.getReceiveDate());
        }

        this.createBy = userService.getById(dto.getCreateBy()).getName();
        this.createTime = DateUtil.formatDateTime(dto.getCreateTime());

        if (!StringUtil.isBlank(dto.getApproveBy()) && dto.getStatus() == ReceiveSheetStatus.APPROVE_PASS) {
            this.approveBy = userService.getById(dto.getApproveBy()).getName();
            this.approveTime = DateUtil.formatDateTime(dto.getApproveTime());
        }

        if (!CollectionUtil.isEmpty(dto.getDetails())) {
            this.details = dto.getDetails().stream().map(OrderDetailBo::new).collect(Collectors.toList());
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class OrderDetailBo extends BaseBo<ReceiveSheetFullDto.OrderDetailDto> {

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
         * 收货数量
         */
        private Integer receiveNum;

        /**
         * 采购价
         */
        private BigDecimal purchasePrice;

        /**
         * 收货金额
         */
        private BigDecimal receiveAmount;

        public OrderDetailBo(ReceiveSheetFullDto.OrderDetailDto dto) {

            if (dto != null) {
                this.convert(dto);

                this.afterInit(dto);
            }
        }

        @Override
        public BaseBo<ReceiveSheetFullDto.OrderDetailDto> convert(ReceiveSheetFullDto.OrderDetailDto dto) {

            return super.convert(dto);
        }

        @Override
        protected void afterInit(ReceiveSheetFullDto.OrderDetailDto dto) {

            this.receiveNum = dto.getOrderNum();
            this.purchasePrice = dto.getTaxPrice();
            this.receiveAmount = NumberUtil.mul(dto.getOrderNum(), dto.getTaxPrice());

            IProductService productService = ApplicationUtil.getBean(IProductService.class);
            PurchaseProductDto product = productService.getPurchaseById(dto.getProductId());

            this.productCode = product.getCode();
            this.productName = product.getName();
            this.skuCode = product.getSkuCode();
            this.externalCode = product.getExternalCode();
        }
    }
}
