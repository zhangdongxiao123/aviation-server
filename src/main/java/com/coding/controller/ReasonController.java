package com.coding.controller;


import com.coding.common.Const;
import com.coding.domain.Airdetail;
import com.coding.domain.Reason;
import com.coding.mapper.AirdetailMapper;
import com.coding.mapper.ReasonMapper;
import com.coding.pojo.param.ReasonParam;
import com.guanweiming.common.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import springfox.documentation.annotations.ApiIgnore;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.WeekendSqls;

import java.util.List;
import java.util.UUID;


@Slf4j
@Api(tags = "事故原因接口")
@AllArgsConstructor
@RestController
@RequestMapping(Const.API + "reason")
public class ReasonController {
    private final ReasonMapper reasonMapper;
    private final AirdetailMapper airdetailMapper;


    @ApiOperation("添加事故原因")
    @PostMapping("add")
    public Result<String> addReason(ReasonParam param) {
        if (StringUtils.isBlank(param.getReaname())) {
            return Result.createByErrorMessage("事故原因名称不能为空");
        }
        if (checkReasonExist(param.getReaname())) {
            return Result.createByErrorMessage("事故原因已存在");
        }
        Reason reason = new Reason();
        BeanUtils.copyProperties(param, reason);
        reason.setReaid(UUID.randomUUID().toString());
        reasonMapper.insertSelective(reason);
        return Result.createBySuccess();
    }

    private boolean checkReasonExist(String reaname) {
        Reason record = new Reason();
        record.setReaname(reaname);
        return reasonMapper.selectCount(record) > 0;
    }

    @ApiOperation("更新事故原因")
    @PostMapping("update")
    public Result<String> updateReason(Reason param) {
        Reason reason = reasonMapper.selectByPrimaryKey(param.getReaid());
        if (reason == null) {
            return Result.createByErrorMessage("事故原因不存在2");
        }
        if (StringUtils.isBlank(param.getReaname())) {
            return Result.createByErrorMessage("事故原因不能为空");
        }
        if (checkReasonExist(param.getReaname())) {
            return Result.createByErrorMessage("事故原因已存在");
        }

        Airdetail record = new Airdetail();
        record.setAirwhy(reason.getReaname());
        List<Airdetail> select = airdetailMapper.select(record);
        for (Airdetail item : select) {
            item.setAirwhy(param.getReaname());
            airdetailMapper.updateByPrimaryKeySelective(item);
        }
        reasonMapper.updateByPrimaryKeySelective(param);
        return Result.createBySuccess();
    }

    @ApiOperation("删除事故原因")
    @PostMapping("delete")
    public Result<String> deleteReason(@RequestParam String reaid) {
        Reason reason = reasonMapper.selectByPrimaryKey(reaid);
        if (reason == null) {
            return Result.createByErrorMessage("事故原因不存在");
        }

        Airdetail record = new Airdetail();
        record.setAirwhy(reason.getReaname());
        List<Airdetail> select = airdetailMapper.select(record);
        for (Airdetail item : select) {
            item.setAirwhy("待修改");
            airdetailMapper.updateByPrimaryKeySelective(item);
        }

        reasonMapper.deleteByPrimaryKey(reaid);
        return Result.createBySuccess();
    }

    @ApiOperation(value = "单条件查询", notes = "只需要传入关键字，会匹配事故详情的所有的数据，找出能匹配上的")
    @GetMapping("singleSelect")
    public Result<List<Reason>> singleSelect(String keyword) {
        if (StringUtils.isBlank(keyword)) {
            List<Reason> list = reasonMapper.selectAll();
            return Result.createBySuccess(list);
        }
        Example record = Example.builder(Reason.class)
                .where(WeekendSqls.<Reason>custom()
                        .orLike(Reason::getReaid, "%" + keyword + "%")
                        .orLike(Reason::getReaname, "%" + keyword + "%")
                        .orLike(Reason::getReahow, "%" + keyword + "%")
                )
                .build();

        List<Reason> list = reasonMapper.selectByExample(record);
        return Result.createBySuccess(list);
    }

    @ApiOperation(value = "事故原因详情")
    @GetMapping("detail")
    public Result<Reason> detail(@RequestParam String reasonId) {
        Reason reason = reasonMapper.selectByPrimaryKey(reasonId);
        if (reason == null) {
            return Result.createByErrorMessage("查询失败");
        }
        return Result.createBySuccess(reason);
    }

    @ApiOperation(value = "查询全部详情")
    @GetMapping("all")
    public Result<List<Reason>> all() {
        return Result.createBySuccess(reasonMapper.selectAll());
    }


}