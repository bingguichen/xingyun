package com.lframework.xingyun.api.controller.basedata.customer;

import com.lframework.common.exceptions.impl.DefaultClientException;
import com.lframework.common.utils.CollectionUtil;
import com.lframework.starter.mybatis.resp.PageResult;
import com.lframework.starter.mybatis.utils.PageResultUtil;
import com.lframework.starter.security.controller.DefaultBaseController;
import com.lframework.starter.web.resp.InvokeResult;
import com.lframework.starter.web.resp.InvokeResultBuilder;
import com.lframework.xingyun.api.bo.basedata.customer.GetCustomerBo;
import com.lframework.xingyun.api.bo.basedata.customer.QueryCustomerBo;
import com.lframework.xingyun.basedata.entity.Customer;
import com.lframework.xingyun.basedata.service.customer.ICustomerService;
import com.lframework.xingyun.basedata.vo.customer.CreateCustomerVo;
import com.lframework.xingyun.basedata.vo.customer.QueryCustomerVo;
import com.lframework.xingyun.basedata.vo.customer.UpdateCustomerVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 客户管理
 *
 * @author zmj
 */
@Api(tags = "客户管理")
@Validated
@RestController
@RequestMapping("/basedata/customer")
public class CustomerController extends DefaultBaseController {

    @Autowired
    private ICustomerService customerService;

    /**
     * 客户列表
     */
    @ApiOperation("客户列表")
    @PreAuthorize("@permission.valid('base-data:customer:query','base-data:customer:add','base-data:customer:modify')")
    @GetMapping("/query")
    public InvokeResult<PageResult<QueryCustomerBo>> query(@Valid QueryCustomerVo vo) {

        PageResult<Customer> pageResult = customerService.query(getPageIndex(vo), getPageSize(vo), vo);

        List<Customer> datas = pageResult.getDatas();
        List<QueryCustomerBo> results = null;

        if (!CollectionUtil.isEmpty(datas)) {
            results = datas.stream().map(QueryCustomerBo::new).collect(Collectors.toList());
        }

        return InvokeResultBuilder.success(PageResultUtil.rebuild(pageResult, results));
    }

    /**
     * 查询客户
     */
    @ApiOperation("查询客户")
    @ApiImplicitParam(value = "ID", name = "id", paramType = "query", required = true)
    @PreAuthorize("@permission.valid('base-data:customer:query','base-data:customer:add','base-data:customer:modify')")
    @GetMapping
    public InvokeResult<GetCustomerBo> get(@NotBlank(message = "ID不能为空！") String id) {

        Customer data = customerService.findById(id);
        if (data == null) {
            throw new DefaultClientException("客户不存在！");
        }

        GetCustomerBo result = new GetCustomerBo(data);

        return InvokeResultBuilder.success(result);
    }

    /**
     * 批量停用客户
     */
    @ApiOperation("批量停用客户")
    @PreAuthorize("@permission.valid('base-data:customer:modify')")
    @PatchMapping("/unable/batch")
    public InvokeResult<Void> batchUnable(
            @ApiParam(value = "ID", required = true) @NotEmpty(message = "请选择需要停用的客户！") @RequestBody List<String> ids) {

        customerService.batchUnable(ids);

        for (String id : ids) {
            customerService.cleanCacheByKey(id);
        }

        return InvokeResultBuilder.success();
    }

    /**
     * 批量启用客户
     */
    @ApiOperation("批量启用客户")
    @PreAuthorize("@permission.valid('base-data:customer:modify')")
    @PatchMapping("/enable/batch")
    public InvokeResult<Void> batchEnable(
            @ApiParam(value = "ID", required = true) @NotEmpty(message = "请选择需要启用的客户！") @RequestBody List<String> ids) {

        customerService.batchEnable(ids);

        for (String id : ids) {
            customerService.cleanCacheByKey(id);
        }

        return InvokeResultBuilder.success();
    }

    /**
     * 新增客户
     */
    @ApiOperation("新增客户")
    @PreAuthorize("@permission.valid('base-data:customer:add')")
    @PostMapping
    public InvokeResult<Void> create(@Valid CreateCustomerVo vo) {

        customerService.create(vo);

        return InvokeResultBuilder.success();
    }

    /**
     * 修改客户
     */
    @ApiOperation("修改客户")
    @PreAuthorize("@permission.valid('base-data:customer:modify')")
    @PutMapping
    public InvokeResult<Void> update(@Valid UpdateCustomerVo vo) {

        customerService.update(vo);

        customerService.cleanCacheByKey(vo.getId());

        return InvokeResultBuilder.success();
    }
}
