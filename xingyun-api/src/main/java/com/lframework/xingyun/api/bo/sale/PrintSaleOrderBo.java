package com.lframework.xingyun.api.bo.sale;

import com.lframework.common.constants.StringPool;
import com.lframework.common.utils.CollectionUtil;
import com.lframework.common.utils.DateUtil;
import com.lframework.common.utils.NumberUtil;
import com.lframework.common.utils.StringUtil;
import com.lframework.starter.web.bo.BaseBo;
import com.lframework.starter.web.bo.BasePrintDataBo;
import com.lframework.starter.web.service.IUserService;
import com.lframework.starter.web.utils.ApplicationUtil;
import com.lframework.xingyun.basedata.dto.customer.CustomerDto;
import com.lframework.xingyun.basedata.dto.product.info.SaleProductDto;
import com.lframework.xingyun.basedata.dto.storecenter.StoreCenterDto;
import com.lframework.xingyun.basedata.service.customer.ICustomerService;
import com.lframework.xingyun.basedata.service.product.IProductService;
import com.lframework.xingyun.basedata.service.storecenter.IStoreCenterService;
import com.lframework.xingyun.sc.dto.sale.SaleOrderFullDto;
import com.lframework.xingyun.sc.enums.SaleOrderStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true)
public class PrintSaleOrderBo extends BasePrintDataBo<SaleOrderFullDto> {

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
     * 客户编号
     */
    private String customerCode;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 销售员姓名
     */
    private String salerName;

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

    public PrintSaleOrderBo() {

    }

    public PrintSaleOrderBo(SaleOrderFullDto dto) {

        super(dto);
    }

    @Override
    public BaseBo<SaleOrderFullDto> convert(SaleOrderFullDto dto) {

        return super.convert(dto, PrintSaleOrderBo::getDetails);
    }

    @Override
    protected void afterInit(SaleOrderFullDto dto) {

        this.salerName = StringPool.EMPTY_STR;
        this.description = StringPool.EMPTY_STR;
        this.approveBy = StringPool.EMPTY_STR;
        this.approveTime = StringPool.EMPTY_STR;

        IStoreCenterService storeCenterService = ApplicationUtil.getBean(IStoreCenterService.class);
        StoreCenterDto sc = storeCenterService.getById(dto.getScId());
        this.scCode = sc.getCode();
        this.scName = sc.getName();

        ICustomerService customerService = ApplicationUtil.getBean(ICustomerService.class);
        CustomerDto customer = customerService.getById(dto.getCustomerId());
        this.customerCode = customer.getCode();
        this.customerName = customer.getName();

        IUserService userService = ApplicationUtil.getBean(IUserService.class);
        if (!StringUtil.isBlank(dto.getSalerId())) {
            this.salerName = userService.getById(dto.getSalerId()).getName();
        }

        this.createBy = userService.getById(dto.getCreateBy()).getName();
        this.createTime = DateUtil.formatDateTime(dto.getCreateTime());

        if (!StringUtil.isBlank(dto.getApproveBy()) && dto.getStatus() == SaleOrderStatus.APPROVE_PASS) {
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
    public static class OrderDetailBo extends BaseBo<SaleOrderFullDto.OrderDetailDto> {

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
         * 销售数量
         */
        private Integer orderNum;

        /**
         * 现价
         */
        private BigDecimal taxPrice;

        /**
         * 销售金额
         */
        private BigDecimal orderAmount;

        public OrderDetailBo(SaleOrderFullDto.OrderDetailDto dto) {

            super(dto);
        }

        @Override
        public BaseBo<SaleOrderFullDto.OrderDetailDto> convert(SaleOrderFullDto.OrderDetailDto dto) {

            return super.convert(dto);
        }

        @Override
        protected void afterInit(SaleOrderFullDto.OrderDetailDto dto) {

            this.orderNum = dto.getOrderNum();
            this.taxPrice = dto.getTaxPrice();
            this.orderAmount = NumberUtil.mul(dto.getOrderNum(), dto.getTaxPrice());

            IProductService productService = ApplicationUtil.getBean(IProductService.class);
            SaleProductDto product = productService.getSaleById(dto.getProductId());

            this.productCode = product.getCode();
            this.productName = product.getName();
            this.skuCode = product.getSkuCode();
            this.externalCode = product.getExternalCode();
        }
    }
}