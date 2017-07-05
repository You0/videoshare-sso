package com.vedioshare.interceptor;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.videoshare.common.service.HttpApiService;
import com.videoshare.common.service.RedisService;

import net.sf.jsqlparser.parser.Token;

public class UserInterceptor implements HandlerInterceptor {

    @Autowired
    private HttpApiService apiService;
    
    @Autowired
    private RedisService redisService;
    
    @Autowired
    private CommonsMultipartResolver multipartResolver;  

//    @Autowired
//    private FeedThreadLocal feedThreadLocal;

    @Value(value = "${SSO_HOST}")
    private String SSO_HOST;

    private final static int FORBIDDEN = 403;

    /**
     * 在controller之前执行 return true 表示继续执行 retrun false 表示终止
     * 验证token 验证该用户是否是合法的登陆用户
     * 
     * 好像配置文件里配置了multipartResolver就不需要转换，直接即可拿到这些值
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 要执行publish
    	
    	System.out.println("开始拦截");
    	//return true;
    	MultipartHttpServletRequest r = null;
    	String enctype = request.getContentType(); 
    	System.out.println("contentType"+enctype);
    	if(enctype.contains("multipart/form-data")){
    		System.out.println("multipart/form-data");
        	 r = multipartResolver.resolveMultipart(request);
    	}
    	
    	
    	if(r!=null){
    		System.out.println("进入");
    		System.out.println(r.getParameter("Token"));
    		String result = redisService.get(r.getParameter("username"));
    		 
            if(result.equals(r.getParameter("Token"))){
            	System.out.println("拥有权限");
            	response.setCharacterEncoding("UTF-8");
            	return true;
            }
    	}
    	
        System.out.println(request.getParameter("Token"));
        String result = redisService.get(request.getParameter("username"));
        
        if(result.equals(request.getParameter("Token"))){
        	System.out.println("拥有权限");
        	response.setCharacterEncoding("UTF-8");
        	return true;
        }
        response.setStatus(this.FORBIDDEN);
        return false;
    }

    /**
     * 在controller执行之后 但并没有返回视图
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {

    }

    /**
     * controller执行之后,并且在返回视图后
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {

    }

}
