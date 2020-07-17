package com.tmall.mall.exception;

import com.tmall.mall.enums.ResponseEnum;
import com.tmall.mall.vo.ResponseVo;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Objects;

import static com.tmall.mall.enums.ResponseEnum.ERROR;

@ControllerAdvice
public class RuntimeExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
//    @ResponseStatus(HttpStatus.ACCEPTED) 自定义返回网络连接状态码
    public ResponseVo handle(RuntimeException e){

        return ResponseVo.error(ERROR,e.getMessage());
    }


    @ExceptionHandler(UserLoginInException.class)
    @ResponseBody
//    @ResponseStatus(HttpStatus.ACCEPTED) 自定义返回网络连接状态码
    public ResponseVo userLoginHandle(){
        //拦截异常后直接抛出
        return ResponseVo.error(ResponseEnum.NEED_LOGIN);

    }

    //接口参数不应为null时传了null
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseVo notValidExceptionHandle(MethodArgumentNotValidException e){
        BindingResult bindingResult=e.getBindingResult();
        Objects.requireNonNull(bindingResult.getFieldError());
        return ResponseVo.error(ResponseEnum.PARAM_ERROR,
                bindingResult.getFieldError().getField()+" "+
                bindingResult.getFieldError().getDefaultMessage());

    }

}
