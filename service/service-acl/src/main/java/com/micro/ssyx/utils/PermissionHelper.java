package com.micro.ssyx.utils;

import com.micro.ssyx.model.acl.Permission;

import java.util.ArrayList;
import java.util.List;

public class PermissionHelper
{
    public static List<Permission> buildPermissions(final List<Permission> allPermissionList)
    {
        // 1 创建最终数据返回list集合
        final ArrayList<Permission> treeNodeList = new ArrayList<>();

        // 遍历所有list集合,得到第一层数据,pid=0
        allPermissionList.forEach(permission -> {
            if (permission.getPid() == 0) {
                permission.setLevel(1);
                treeNodeList.add(findChildren(permission, allPermissionList));
            }
        });
        return treeNodeList;
    }

    private static Permission findChildren(final Permission permission, final List<Permission> allPermissionList)
    {
        permission.setChildren(new ArrayList<>());
        allPermissionList.forEach(p -> {
            if (permission.getId().longValue() == p.getPid().longValue()) {
                p.setLevel(permission.getLevel() + 1);
                if (permission.getChildren() == null) {
                    permission.setChildren(new ArrayList<>());
                }
                permission.getChildren().add(findChildren(p, allPermissionList));
            }
        });
        return permission;
    }
}
