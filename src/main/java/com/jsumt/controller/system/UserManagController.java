/**
 * Project Name:ECRMS
 * File Name:UserManagController.java
 * Package Name:com.jsumt.controller.user
 * Date:2018年7月31日上午9:53:42
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 */

package com.jsumt.controller.system;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.jsumt.common.BaseController;
import com.google.common.collect.Maps;
import com.jsumt.service.system.RoleService;
import com.jsumt.service.system.UserManageService;
import com.jsumt.util.Base64Helper;
import com.jsumt.util.BeanUtil;
import com.jsumt.util.ConfigUtil;
import com.jsumt.util.DateUtil;
import com.jsumt.util.JsonHelper;
import com.jsumt.util.MD5Util;
import com.jsumt.util.PageInfoUtiil;
import com.jsumt.util.RedisUtil;
import com.jsumt.util.StringHelper;
import com.jsumt.util.UUIDHexGenerator;
import com.jsumt.vo.system.RoleBean;
import com.jsumt.vo.system.UserBean;

/**
 * 用户管理模块
 * ClassName:UserManagController <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年7月31日 上午9:53:42 <br/>
 * 
 * @author txm
 * @version
 * @since JDK 1.6
 * @see
 */
@Controller
public class UserManagController extends BaseController
{
    @Autowired
    private UserManageService umService;
    @Autowired
    private RoleService roleService;

    private static Logger LOGGER = LoggerFactory.getLogger(UserManagController.class);

    // 查询用户（关联组织机构）
    @RequestMapping("queryUsers")
    public @ResponseBody Map<String, Object> queryUsers()
    {
        PageInfo<Map<String,Object>> pageInfo = new PageInfo<Map<String,Object>>();
        Map<String, Object> queryMap = new HashMap<String, Object>();
        Map<String, Object> retMap = new HashMap<String, Object>();
        List<UserBean> userList = new ArrayList<UserBean>();
        List<Map<String,Object>> returnList = new ArrayList<Map<String,Object>>();
        try
        {
            // 接受页面所传参数
            String pageNumPa = this.getParameter("pageNum");
            String rowCountPa = this.getParameter("rowCount");
            String UserCodePa = this.getParameter("usercode");
            String UserNamePa = this.getParameter("username");
            String organiz_id = this.getParameter("organiz");
            queryMap.put("USER_CODE", UserCodePa);
            queryMap.put("USER_NAME", UserNamePa);
            queryMap.put("organiz_id", organiz_id);
            if(StringHelper.isNullAndEmpty(organiz_id))
            {//如果组织结构为空，说明是第一次加载，第一次加载不显示数据，只给表头
                Map<String, Object> dataMap = new HashMap<String, Object>();
                dataMap.put("userList", Lists.newArrayList());
                dataMap.put("totalCount", 0);// 总共页数
                retMap = this.generateMsg(dataMap, true, "查询用户成功!");
                return retMap;
            }
            // 设置当前页
            Integer pageNum = Integer.parseInt(pageNumPa) <= 0 ? 1 : Integer.parseInt(pageNumPa);
            // 设置每页显示的数量
            Integer rowCount = Integer.parseInt(rowCountPa) <= 0 ? 10 : Integer.parseInt(rowCountPa);
            // 封装分页信息
            pageInfo.setPageNum(pageNum);
            pageInfo.setPageSize(rowCount);
            Page<Map<String,Object>> returnPageList = new Page<Map<String,Object>>();
            // 查询用户对象
            userList = umService.queryUsers(queryMap);
            for(UserBean user:userList)
            {
                Map<String,Object> map=BeanUtil.toMap(user);
                map.put("birthday", DateUtil.getDateFormat(user.getBirthday(), DateUtil.DATE_DEFAULT_FORMAT));
                map.put("work_date", DateUtil.getDateFormat(user.getWork_date(), DateUtil.DATE_DEFAULT_FORMAT));
                returnList.add(map);
            }

            returnPageList = new PageInfoUtiil<Map<String,Object>>().generatePageList(pageInfo, returnList);
            pageInfo = new PageInfo<Map<String,Object>>(returnPageList);

        }
        catch (UnsupportedEncodingException e)
        {
            retMap = this.generateMsg("", false, "查询用户失败!");
            e.printStackTrace();
            return retMap;
        }

        // data:data.msg.userList,data.data.organizlist,html = '';
        // 封装map结果集
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("userList", pageInfo.getList());
        dataMap.put("totalCount", pageInfo.getTotal());// 总共页数
        retMap = this.generateMsg(dataMap, true, "查询用户成功!");
        return retMap;
    }

    // 查询用户（关联角色）
    @RequestMapping("queryRoleInUser")
    public @ResponseBody Map<String, Object> queryRoleInUser()
    {
        Map<String, Object> queryMap = new HashMap<String, Object>();
        Map<String, Object> retMap = new HashMap<String, Object>();
        PageInfo<RoleBean> pageInfo = new PageInfo<RoleBean>();
        List<RoleBean> roleList = new ArrayList<RoleBean>();
        try
        {
            // 接受页面所传参数
            String pageNumPa = this.getParameter("pageNum");
            String rowCountPa = this.getParameter("rowCount");
            String RoleCodePa = this.getParameter("rolecode");
            String RoleNamePa = this.getParameter("rolename");
            String userId = this.getParameter("userId");
            queryMap.put("roleCode", RoleCodePa);
            queryMap.put("roleName", RoleNamePa);
            queryMap.put("userId", userId);
            // 设置当前页
            Integer pageNum = Integer.parseInt(pageNumPa) <= 0 ? 1 : Integer.parseInt(pageNumPa);
            // 设置每页显示的数量
            Integer rowCount = Integer.parseInt(rowCountPa) <= 0 ? 10 : Integer.parseInt(rowCountPa);
            // 封装分页信息
            pageInfo.setPageNum(pageNum);
            pageInfo.setPageSize(rowCount);
            Page<RoleBean> returnPageList = new Page<RoleBean>();
            // 查询用户对象
            roleList = roleService.queryRoleInUser(queryMap);
            for(RoleBean roleBean :roleList ){
                List<String> list = new ArrayList<String>();
                //获取上一级角色名称
                roleService.queryRoleById(roleBean.getRole_pid()).getRole_name();
                
            }

            returnPageList = new PageInfoUtiil<RoleBean>().generatePageList(pageInfo, roleList);
            pageInfo = new PageInfo<RoleBean>(returnPageList);

        }
        catch (UnsupportedEncodingException e)
        {
            retMap = this.generateMsg("", false, "查询用户失败!");
            e.printStackTrace();
            return retMap;
        }

        // data:data.msg.userList,data.data.organizlist,html = '';
        // 封装map结果集
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("roleList", pageInfo.getList());
        dataMap.put("totalCount", pageInfo.getTotal());// 总共页数
        retMap = this.generateMsg(dataMap, true, "查询用户成功!");
        return retMap;
    }
    
    // 查询用户（关联角色）
    @RequestMapping("queryRoleNotInUser")
    public @ResponseBody Map<String, Object> queryRoleNotInUser()
    {
        Map<String, Object> queryMap = new HashMap<String, Object>();
        Map<String, Object> retMap = new HashMap<String, Object>();
        PageInfo<RoleBean> pageInfo = new PageInfo<RoleBean>();
        List<RoleBean> roleList = new ArrayList<RoleBean>();
        try
        {
            // 接受页面所传参数
            String pageNumPa = this.getParameter("pageNum");
            String rowCountPa = this.getParameter("rowCount");
            String RoleCodePa = this.getParameter("rolecode");
            String RoleNamePa = this.getParameter("rolename");
            String userId = this.getParameter("userId");
            queryMap.put("roleCode", RoleCodePa);
            queryMap.put("roleName", RoleNamePa);
            queryMap.put("userId", userId);
            // 设置当前页
            Integer pageNum = Integer.parseInt(pageNumPa) <= 0 ? 1 : Integer.parseInt(pageNumPa);
            // 设置每页显示的数量
            Integer rowCount = Integer.parseInt(rowCountPa) <= 0 ? 10 : Integer.parseInt(rowCountPa);
            // 封装分页信息
            pageInfo.setPageNum(pageNum);
            pageInfo.setPageSize(rowCount);
            Page<RoleBean> returnPageList = new Page<RoleBean>();
            // 查询用户对象
            roleList = roleService.queryRoleNotInUser(queryMap);

            returnPageList = new PageInfoUtiil<RoleBean>().generatePageList(pageInfo, roleList);
            pageInfo = new PageInfo<RoleBean>(returnPageList);

        }
        catch (UnsupportedEncodingException e)
        {
            retMap = this.generateMsg("", false, "查询用户失败!");
            e.printStackTrace();
            return retMap;
        }

        // 封装map结果集
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("roleList", pageInfo.getList());
        dataMap.put("totalCount", pageInfo.getTotal());// 总共页数
        retMap = this.generateMsg(dataMap, true, "查询用户成功!");
        return retMap;
    }
    
    @RequestMapping("addUser")
    public @ResponseBody Map<String, Object> addUser(UserBean bean, String org_id)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        // 封装主primary key
        bean.setUser_id(UUIDHexGenerator.generator());
        bean.setUser_pwd(MD5Util.MD5(bean.getUser_pwd()));
        // 用户表插入对象数据的同时向用户组织表（中间表）插入数据
        try
        {
            umService.createUser(bean, org_id);
            retMap = this.generateMsg("", true, "增加用户成功!");
            return retMap;
        }
        catch (Exception e)
        {
            retMap = this.generateMsg("", false, "增加用户失败!");
            return retMap;
        }

    }

    // 批量删除用户
    @RequestMapping("delUsers")
    public @ResponseBody Map<String, Object> delUsers(String delList)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        List<String> delelteList = JsonHelper.fromJsonWithGson(delList, List.class);
        try
        {   
            umService.deleteUsers(delelteList);
            retMap = this.generateMsg("", true, "删除用户成功!");
            return retMap;
        }
        catch (Exception e)
        {
            retMap = this.generateMsg("", false, "删除用户失败!");
            return retMap;
        }
    }

    // 修改用户信息
    @RequestMapping("editUser")
    public @ResponseBody Map<String, Object> editUser(UserBean bean)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        if (StringHelper.isNotNullAndEmpty(bean.getUser_pwd()))
        {   
            bean.setUser_pwd(MD5Util.MD5(bean.getUser_pwd()));
        }

        try
        {
            String sessionId=request.getParameter("sessionId");
            String userCode = RedisUtil.getRedisUtil().getStringValue(sessionId);
            umService.editUser(bean);
            UserBean userBean= umService.queryUserByCode(userCode);
            retMap = this.generateMsg(userBean, true, "修改用户成功!");//返回最新的
            
            return retMap;
        }
        catch (Exception e)
        {
            retMap = this.generateMsg("", false, "修改用户失败!");
            return retMap;
        }
    }
    
    @RequestMapping("updateUserPwd")
    public @ResponseBody Map<String, Object> updateUserPwd()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        String oldPwd=request.getParameter("oldPwd");
        String newPwd=request.getParameter("newPwd");
        try
        {
            String sessionId=request.getParameter("sessionId");
            String userCode = RedisUtil.getRedisUtil().getStringValue(sessionId);
            UserBean userBean = umService.queryUserByCode(userCode);
            if(MD5Util.MD5(oldPwd).equals(userBean.getUser_pwd()))
            {
                UserBean updateBean=new UserBean();
                updateBean.setUser_id(userBean.getUser_id());
                updateBean.setUser_pwd(MD5Util.MD5(newPwd));
                umService.editUser(updateBean);
                retMap = this.generateMsg("", true, "用户密码修改成功!");
                return retMap;
            }
            else
            {
                retMap = this.generateMsg("", false, "当前密码错误!");
                return retMap;
            }
        }
        catch (Exception e)
        {
            retMap = this.generateMsg("", false, "用户密码修改失败!");
            return retMap;
        }
    }

    @RequestMapping("queryUserInfo")
    public @ResponseBody Map<String, Object> queryUserInfo()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();

        try
        {   
            String sessionId=request.getParameter("sessionId");
            String userCode = RedisUtil.getRedisUtil().getStringValue(sessionId);
            //查看最新用户信息
            UserBean user = umService.queryUserByCode(userCode);
            Map<String, Object> userMap = BeanUtil.toMap(user);
            userMap.put("loginTime", DateUtil.getDateFormat(user.getLoginTime(), DateUtil.DATETIME_DEFAULT_FORMAT));
            retMap = this.generateMsg(userMap, true, "查询用户成功!");
            return retMap;
        }
        catch (Exception e)
        {
            retMap = this.generateMsg("", false, "查询用户失败!");
            return retMap;
        }
    }

    @RequestMapping("updateUserImage")
    public @ResponseBody Map<String, Object> updateUserImage()
    {
        Map<String, Object> retMap = new HashMap<String, Object>();
        try
        {
            String sessionId = this.getParameter("sessionId");
            String userCode = RedisUtil.getRedisUtil().getStringValue(sessionId);
            UserBean userBean = umService.queryUserByCode(userCode);
            UserBean vv=new UserBean();
            vv.setUser_id(userBean.getUser_id());
            // 处理文件
            // 文件上传的请求
            MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
            // 获取请求的参数
            Map<String, MultipartFile> fileMap = mRequest.getFileMap();
            Iterator<Map.Entry<String, MultipartFile>> it = fileMap.entrySet().iterator();
            // 用hasNext() 判断是否有值，用next()方法把元素取出。
            while (it.hasNext())
            {
                Map.Entry<String, MultipartFile> entry = it.next();
                MultipartFile mFile = entry.getValue();
                if (mFile.getSize() != 0 && !"".equals(mFile.getName()))
                {
                    String base64=Base64Helper.encryptBASE64(mFile.getBytes());
                    LOGGER.info(""+base64.getBytes().length);
                    vv.setUserImage(mFile.getBytes());
                    vv.setImageName(mFile.getOriginalFilename());
                    break;
                }
            }
            // 更新用户图片
            umService.editUser(vv);
        }
        catch (Exception e)
        {
            retMap = this.generateMsg("", false, "修改信息失败!");
            return retMap;

        }

        retMap = this.generateMsg("", true, "修改信息成功!");
        return retMap;
    }

    @RequestMapping("getUserImage")
    public void getUserImage() throws Exception
    {
        String sessionId=request.getParameter("sessionId");
        String userCode = RedisUtil.getRedisUtil().getStringValue(sessionId);
        UserBean userBean = umService.queryUserByCode(userCode);
        if(userBean.getUserImage()!=null)
        {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(userBean.getUserImage());
            response.setContentType("image/png");
            int len = 0;
            byte[] b = new byte[1024];

            while ((len = inputStream.read(b, 0, 1024)) != -1)
            {
                response.getOutputStream().write(b, 0, len);
            }
            inputStream.close();
        }
        
    }

    @RequestMapping("login")
    public @ResponseBody Map<String, Object> login(UserBean bean)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if (StringHelper.isNullAndEmpty(bean.getUser_code()))
        {
            retMap = this.generateMsg("", false, "userCode不能为空!");
            return retMap;
        }
        try
        {
            UserBean user = umService.queryUserByCode(bean.getUser_code());
            System.out.println(bean.getUser_pwd());
            String pa = MD5Util.MD5(bean.getUser_pwd());
            System.out.println(user.getUser_pwd());
            if(user.getUser_pwd().equals(MD5Util.MD5(bean.getUser_pwd())))
            {
                // 更新系统登录时间
                user.setLoginTime(new Date());
                umService.editUser(user);
                // 创建会话 只保存UserCode,因为用户其他信息可能会变。所以只保存userCode
                String sessionId=UUIDHexGenerator.generator();
                RedisUtil.getRedisUtil().setStringValue(sessionId,
                        user.getUser_code(),
                        Integer.valueOf(ConfigUtil.getValueByKey("sessionTime")));
                Map<String, Object> userMap = BeanUtil.toMap(user);
                userMap.put("sessionId", sessionId);
                retMap = this.generateMsg(userMap, true, "登录成功!");
                return retMap;
            }
            else
            {
                retMap = this.generateMsg("", false, "登录失败!");
                return retMap;
                
            }
        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage());
            retMap = this.generateMsg("", false, "登录失败!");
            return retMap;
        }
    }
   
    @RequestMapping("qryOaUser")
    public @ResponseBody Map<String, Object> qryOaUser(String workNo)
    {
        Map<String, Object> retMap = new HashMap<String, Object>();

        try
        {   
            //查看最新用户信息
            Map<String, Object> queryMap = Maps.newHashMap();
            queryMap.put("work_no", workNo);
            List<UserBean> userList= umService.queryUsers(queryMap);
            if(userList.isEmpty())
            {
                retMap = this.generateMsg("", false, "登录失败!");
                return retMap;
            }
            else
            {//工号验证通过，登录
                UserBean user = userList.get(0);
                user.setLoginTime(new Date());
                umService.editUser(user);
                // 创建会话 只保存UserCode,因为用户其他信息可能会变。所以只保存userCode
                String sessionId=UUIDHexGenerator.generator();
                RedisUtil.getRedisUtil().setStringValue(sessionId,
                        user.getUser_code(),
                        Integer.valueOf(ConfigUtil.getValueByKey("sessionTime")));
                Map<String, Object> userMap = BeanUtil.toMap(user);
                userMap.put("sessionId", sessionId);
                retMap = this.generateMsg(userMap, true, "登录成功!");
                return retMap;
            }
        }
        catch (Exception e)
        {
            retMap = this.generateMsg("", false, "登录失败!");
            return retMap;
        }
    }


}
