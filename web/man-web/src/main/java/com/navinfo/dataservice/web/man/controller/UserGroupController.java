package com.navinfo.dataservice.web.man.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.navinfo.dataservice.api.man.model.UserGroup;
import com.navinfo.dataservice.api.man.model.UserInfo;
import com.navinfo.dataservice.commons.log.LoggerRepos;
import com.navinfo.dataservice.commons.springmvc.BaseController;
import com.navinfo.dataservice.engine.man.userGroup.UserGroupService;
import com.navinfo.navicommons.database.Page;

/** 
* @ClassName: UserGroupController 
* @author code generator 
* @date 2016年4月6日 下午6:25:24 
* @Description: TODO
*/
@Controller
public class UserGroupController extends BaseController {
	private Logger log = LoggerRepos.getLogger(this.getClass());
	@Autowired 
	private UserGroupService service;

	
	@RequestMapping(value = "/userGroup/create")
	public ModelAndView create(HttpServletRequest request){
		try{	
			String parameter = request.getParameter("parameter");
			if (StringUtils.isEmpty(parameter)){
				throw new IllegalArgumentException("parameter参数不能为空。");
			}		
			JSONObject dataJson = JSONObject.fromObject(URLDecode(parameter));			
			if(dataJson==null){
				throw new IllegalArgumentException("parameter参数不能为空。");
			}
			UserGroup  bean = (UserGroup)JSONObject.toBean(dataJson, UserGroup.class);	
			service.create(bean);			
			return new ModelAndView("jsonView", success("创建成功"));
		}catch(Exception e){
			log.error("创建失败，原因："+e.getMessage(), e);
			return new ModelAndView("jsonView",exception(e));
		}
	}
	@RequestMapping(value = "/userGroup/update")
	public ModelAndView update(HttpServletRequest request){
		try{			
			JSONObject dataJson = JSONObject.fromObject(URLDecode(request.getParameter("parameter")));			
			if(dataJson==null){
				throw new IllegalArgumentException("parameter参数不能为空。");
			}
			UserGroup  bean = (UserGroup)JSONObject.toBean(dataJson, UserGroup.class);
			service.update(bean);			
			return new ModelAndView("jsonView", success("修改成功"));
		}catch(Exception e){
			log.error("修改失败，原因："+e.getMessage(), e);
			return new ModelAndView("jsonView",exception(e));
		}
	}
	@RequestMapping(value = "/userGroup/delete")
	public ModelAndView delete(HttpServletRequest request){
		try{			
			JSONObject dataJson = JSONObject.fromObject(URLDecode(request.getParameter("parameter")));			
			if(dataJson==null){
				throw new IllegalArgumentException("parameter参数不能为空。");
			}
			UserGroup  bean = (UserGroup)JSONObject.toBean(dataJson, UserGroup.class);
			service.delete(bean);			
			return new ModelAndView("jsonView", success("删除成功"));
		}catch(Exception e){
			log.error("删除失败，原因："+e.getMessage(), e);
			return new ModelAndView("jsonView",exception(e));
		}
	}
	@RequestMapping(value = "/userGroup/list")
	public ModelAndView list(HttpServletRequest request){
		try{			
			JSONObject dataJson = JSONObject.fromObject(URLDecode(request.getParameter("parameter")));			
			if(dataJson==null){
				throw new IllegalArgumentException("parameter参数不能为空。");
			}
			int curPageNum= 1;//默认为第一页
			String curPage= request.getParameter("page");
			if (StringUtils.isNotEmpty(curPage)){
				curPageNum = Integer.parseInt(curPage);
			}
			UserGroup  bean = (UserGroup)JSONObject.toBean(dataJson, UserGroup.class);
			Page data = service.list(bean,curPageNum);			
			return new ModelAndView("jsonView", success(data));
		}catch(Exception e){
			log.error("获取列表失败，原因："+e.getMessage(), e);
			return new ModelAndView("jsonView",exception(e));
		}
	}
	
	@RequestMapping(value = "/userGroup/query")
	public ModelAndView query(HttpServletRequest request){
		try{
			JSONObject dataJson = JSONObject.fromObject(URLDecode(request.getParameter("parameter")));			
			if(dataJson==null){
				throw new IllegalArgumentException("parameter参数不能为空。");
			}

			UserGroup  bean = (UserGroup)JSONObject.toBean(dataJson, UserGroup.class);
			UserGroup  data = service.query(bean);			
			return new ModelAndView("jsonView", success(data));
		}catch(Exception e){
			log.error("获取明细失败，原因："+e.getMessage(), e);
			return new ModelAndView("jsonView",exception(e));
		}
	}
	
	
	@RequestMapping(value = "/userGroup/listByUser")
	public ModelAndView listByUser(HttpServletRequest request){
		try{			
			JSONObject dataJson = JSONObject.fromObject(URLDecode(request.getParameter("parameter")));			
			if(dataJson==null){
				throw new IllegalArgumentException("parameter参数不能为空。");
			}
			
			if(dataJson.isEmpty()){
				return new ModelAndView("jsonView", success("无用户信息"));
			}

			UserInfo  bean = (UserInfo)JSONObject.toBean(dataJson, UserInfo.class);
			
			List<UserGroup> userGroupList = service.listByUser(bean);
			
			List<HashMap<?,?>> data = new ArrayList<HashMap<?,?>>();
			
			for(int i = 0;i<userGroupList.size();i++){
				HashMap<String, Comparable> userGroup = new HashMap<String, Comparable>();
				userGroup.put("groupId", userGroupList.get(i).getGroupId());
				userGroup.put("groupName", userGroupList.get(i).getGroupName());
				data.add(userGroup);
			}
			
			return new ModelAndView("jsonView", success(data));
		}catch(Exception e){
			log.error("获取列表失败，原因："+e.getMessage(), e);
			return new ModelAndView("jsonView",exception(e));
		}
	}
	
	@RequestMapping(value = "/userGroup/listByType")
	public ModelAndView listByType(HttpServletRequest request){
		try{			
			JSONObject dataJson = JSONObject.fromObject(URLDecode(request.getParameter("parameter")));			
			if(dataJson==null){
				throw new IllegalArgumentException("parameter参数不能为空。");
			}
			
			if(dataJson.isEmpty()){
				return new ModelAndView("jsonView", success("无组类型信息"));
			}

			UserGroup  bean = (UserGroup)JSONObject.toBean(dataJson, UserGroup.class);
			
			List<UserGroup> userGroupList = service.listByType(bean);
			
			List<HashMap<?,?>> data = new ArrayList<HashMap<?,?>>();
			
			for(int i = 0;i<userGroupList.size();i++){
				HashMap<String, Comparable> userGroup = new HashMap<String, Comparable>();
				userGroup.put("groupId", userGroupList.get(i).getGroupId());
				userGroup.put("groupName", userGroupList.get(i).getGroupName());
				data.add(userGroup);
			}
			
			return new ModelAndView("jsonView", success(data));
		}catch(Exception e){
			log.error("获取列表失败，原因："+e.getMessage(), e);
			return new ModelAndView("jsonView",exception(e));
		}
	}
}
