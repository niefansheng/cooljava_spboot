package com.controller.tool;

import com.model.base.AjaxResult;
import com.model.tool.Quartz;
import com.model.user.User;
import com.service.tool.QuartzService;
import com.service.user.UserService;
import com.util.QuartzUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @功能说明：调度管理
 * @创建日期：2018-05-06
 */
@Controller
@RequestMapping("/quarz")
public class QuartzController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private QuartzService quarzService;

	/**
	 * 调度管理列表跳转页面
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/list")
	public String list(HttpServletRequest request, Model model) {
		//获取当前用户
		User user = (User) request.getSession().getAttribute("user");
		user = (User) userService.get(user);
		model.addAttribute("user", user);
		return "views/tool/quarzList";
	}
	
	
	/**
	 * 分页获取调度管理
	 * @param request
	 * @param model
	 * @param user
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@ResponseBody
	@RequestMapping(value = "/quarzData")
	public Map<String, Object> quarzData(HttpServletRequest request, Model model, Quartz quarz) throws UnsupportedEncodingException {
		 Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<Quartz> list = quarzService.getListByPage(quarz);
	        Long count = quarzService.getCount(quarz);
	        map.put("code", 0);
	        map.put("msg", "");
	        map.put("count", count);
	        map.put("data", list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		

        return map;
		
	}
	
	/**
	 * 删除调度管理
	 * @param request
	 * @param model
	 * @param user
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@ResponseBody
	@RequestMapping(value = "/delete")
	public String delete(HttpServletRequest request, Model model, Quartz quarz) {
		String result = "1";
		quarz = quarzService.get(quarz);
		try {
			quarzService.delete(quarz.getId());
			QuartzUtils.delScheduleJob(quarz.getJobName(), quarz.getJobGroup());// 删除任务
			result = "0";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 批量删除调度管理
	 * @param request
	 * @param model
	 * @param user
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteBatch")
	public String deleteBatch(HttpServletRequest request, Model model, String ids) {
		String result = "1";
		try {
			String[] idarr = ids.split(",");
			for(String id : idarr){
				Quartz entity = new Quartz();
				entity.setId(Integer.parseInt(id));
				entity = quarzService.get(entity);
				quarzService.delete(Integer.parseInt(id));
				QuartzUtils.delScheduleJob(entity.getJobName(), entity.getJobGroup());// 删除任务
			}
			result = "0";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 新增调度管理跳转页面
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/form")
	public String form(Quartz quarz, HttpServletRequest request, Model model) {
		return "views/tool/quarzForm";
	}
	
	/**
	 * 新增调度管理跳转页面
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/edit")
	public String edit(Quartz quarz, HttpServletRequest request, Model model) {
		quarz = quarzService.get(quarz);
		model.addAttribute("quarz", quarz);
		return "views/tool/quarzForm";
	}
	
	/**
	 * 保存调度管理
	 * @param request
	 * @param model
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@ResponseBody
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public AjaxResult save(HttpServletRequest request, Model model, Quartz quarz) throws UnsupportedEncodingException {
		AjaxResult result = new AjaxResult();
		result.setCode("0");
		ServletContext context=request.getSession().getServletContext();
		try
		{
			if(quarz.getId() == 0){
				List<Quartz> list = quarzService.getAllList(quarz);
				if(list.size() > 0 ){
					result.setCode("2");
					result.setMsg("定时器名称不能重复");
				}else{
					//设置默认值
					quarz.setState("0");//初始状态为可用
					result = quarzService.saveT(quarz, context);
				}
			}else{
				//设置默认值
				quarzService.save(quarz);
				result.setCode("0");
				result.setMsg("保存成功");
				// 重启调度器
				QuartzUtils.rescheduleJob(quarz, context);
			}
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 禁用/解禁定时任务
	 * @param request
	 * @param model
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@ResponseBody
	@RequestMapping(value = "/setUse")
	public String setUse(HttpServletRequest request, Model model, Quartz quarz) {
		String result = "1";
		quarz = quarzService.get(quarz);
		ServletContext context=request.getSession().getServletContext();
		try {
			if(quarz.getState().equals("0")){
				quarz.setState("1");
			}else{
				quarz.setState("0");
			}
			quarzService.save(quarz);
			//调度的处理
			if (quarz.getState().equals("0")) {// 运行中
				QuartzUtils.rescheduleJob(quarz, context);
			} else if (quarz.getState().equals("1")) {// 暂停
				QuartzUtils.pauseJob(quarz.getJobName(), quarz.getJobGroup());
			}
			result = "0";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
