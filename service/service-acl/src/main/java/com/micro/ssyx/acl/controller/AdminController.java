package com.micro.ssyx.acl.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.micro.ssyx.acl.service.AdminService;
import com.micro.ssyx.acl.service.RoleService;
import com.micro.ssyx.common.result.ResultResponse;
import com.micro.ssyx.common.utils.MD5;
import com.micro.ssyx.model.acl.Admin;
import com.micro.ssyx.vo.acl.AdminQueryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/admin/acl/user")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    private final RoleService roleService;

    /**
     * 为用户进行角色分配
     *
     * @param roleId  角色id
     * @param adminId 用户id
     * @return
     */
    @PostMapping("doAssign")
    public ResultResponse<Void> doAssign(@RequestParam final Long[] roleId,
                                         @RequestParam final Long adminId) {
        roleService.saveUserRoleRealtionShip(roleId, adminId);
        return ResultResponse.ok(null);
    }

    /**
     * 根据用户获取角色数据
     *
     * @param adminId 用户id
     * @return 角色数据
     */
    @GetMapping("toAssign/{adminId}")
    public ResultResponse<Map<String, Object>> toAssign(@PathVariable final Long adminId) {
        final Map<String, Object> map = roleService.selectUserRoleByAdminId(adminId);
        return ResultResponse.ok(map);
    }

    /**
     * 用户列表分页查询
     *
     * @param current      当前页
     * @param limit        每页记录数
     * @param adminQueryVo 查询条件
     * @return 用户列表
     */
    @GetMapping("{current}/{limit}")
    public ResultResponse<IPage<Admin>> getPageList(@PathVariable final Long current,
                                                    @PathVariable final Long limit,
                                                    final AdminQueryVo adminQueryVo) {
        final Page<Admin> pageParam = new Page<>(current, limit);
        final IPage<Admin> pageModel = adminService.selectPageList(pageParam, adminQueryVo);
        return ResultResponse.ok(pageModel);
    }

    /**
     * 根据id查询用户
     *
     * @param id 用户id
     * @return 用户
     */
    @GetMapping("get/{id}")
    public ResultResponse<Admin> getById(@PathVariable final Long id) {
        final Admin admin = adminService.getById(id);
        return ResultResponse.ok(admin);
    }

    /**
     * 新增用户
     *
     * @param admin 用户
     * @return 新增结果
     */
    @PostMapping("save")
    public ResultResponse<String> save(@RequestBody final Admin admin) {
        final String passwordMd5 = MD5.encrypt(admin.getPassword());
        admin.setPassword(passwordMd5);
        final boolean result = adminService.save(admin);
        if (!result) {
            return ResultResponse.fail("保存失败");
        }
        return ResultResponse.ok("保存成功");
    }

    /**
     * 修改用户
     *
     * @param admin 用户
     * @return 修改结果
     */
    @PutMapping("update")
    public ResultResponse<String> updateById(@RequestBody final Admin admin) {
        final boolean result = adminService.updateById(admin);
        if (!result) {
            return ResultResponse.fail("修改失败");
        }
        return ResultResponse.ok("修改成功");
    }


    /**
     * 删除用户
     *
     * @param id 用户id
     * @return 删除结果
     */
    @DeleteMapping("remove/{id}")
    public ResultResponse<String> removeById(@PathVariable final Long id) {
        final boolean result = adminService.removeById(id);
        if (!result) {
            return ResultResponse.fail("删除失败");
        }
        return ResultResponse.ok("删除成功");
    }

    /**
     * 批量删除用户
     *
     * @param idList 用户id列表
     * @return 批量删除结果
     */
    @DeleteMapping("batchRemove")
    public ResultResponse<String> batchRemove(@RequestBody final List<Long> idList) {
        final boolean result = adminService.removeByIds(idList);
        if (!result) {
            return ResultResponse.fail("批量删除失败");
        }
        return ResultResponse.ok("批量删除成功");
    }


}
