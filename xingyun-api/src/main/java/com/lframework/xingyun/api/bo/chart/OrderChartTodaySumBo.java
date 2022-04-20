package com.lframework.xingyun.api.bo.chart;

import com.lframework.starter.web.bo.BaseBo;
import com.lframework.xingyun.chart.dto.OrderChartSumDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderChartTodaySumBo extends BaseBo<OrderChartSumDto> {

    /**
     * 单据总金额
     */
    @ApiModelProperty("单据总金额")
    private BigDecimal totalAmount;

    /**
     * 单据数量
     */
    @ApiModelProperty("单据数量")
    private Long totalNum;

    /**
     * 图表数据
     */
    @ApiModelProperty("图表数据")
    private List<OrderChartTodayBo> charts;

    public OrderChartTodaySumBo() {

    }

    public OrderChartTodaySumBo(OrderChartSumDto dto) {

        super(dto);
    }
}
