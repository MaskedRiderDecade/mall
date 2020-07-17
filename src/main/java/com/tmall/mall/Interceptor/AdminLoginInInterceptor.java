package com.tmall.mall.Interceptor;

import com.tmall.mall.consts.MallConst;
import com.tmall.mall.exception.UserLoginInException;
import com.tmall.mall.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@Configuration
public class AdminLoginInInterceptor implements HandlerInterceptor {
    //true=继续流程，flase=中断
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session=request.getSession();
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        if(user==null){
            log.info("user == null");
//            return ResponseVo.error(ResponseEnum.NEED_LOGIN);
//            return false;
            throw new UserLoginInException();
        }
        else if(user.getRole()!=0){
            log.info("不是管理员账户");
            return false;
        }
        return true;
    }
}
