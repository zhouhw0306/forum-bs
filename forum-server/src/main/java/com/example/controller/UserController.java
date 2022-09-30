package com.example.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.constant.Result;
import com.example.constant.ResultCode;
import com.example.domain.User;
import com.example.service.UserService;
import com.example.utils.EmailUtils;
import com.example.utils.JWTUtil;
import com.example.utils.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author zhw
 */
@RestController
@RequestMapping("/api")
public class UserController {

    @Resource
    UserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    //登录
    @RequestMapping(value = "/login/status",method = RequestMethod.POST)
    public Result login(String username, String password){
        Result result = new Result();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        queryWrapper.eq("password",MD5Util.MD5Lower(password));
        List<User> list = userService.list(queryWrapper);
        if (list.size()==0){
            result.setResultCode(ResultCode.USER_LOGIN_ERROR);
            return result;
        }
        User user = list.get(0);
        // 生成token
        String token = JWTUtil.sign(String.valueOf(user.getId()),user.getPassword());
        user.setPassword("it's a secret");
        user.setToken(token);
        //缓存到Redis
        Map<String,Object> userMap = BeanUtil.beanToMap(user);
        /**
         * 使用Jackson2JsonRedisSerialize 替换默认序列化
         */
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        stringRedisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        stringRedisTemplate.opsForHash().putAll(token,userMap);
        stringRedisTemplate.expire(token,30,TimeUnit.MINUTES);

        result.setResultCode(ResultCode.SUCCESS);
        result.setData(user);
        return result;
    }


    //注册用户
    @RequestMapping(value = "/user/add", method = RequestMethod.POST)
    public Result addUser(HttpServletRequest req){

        String username = req.getParameter("username").trim();
        String password = req.getParameter("password").trim();
        String sex = req.getParameter("sex").trim();
        String email = req.getParameter("email").trim();
        String userCode = req.getParameter("checkCode").trim();
        String birth = req.getParameter("birth").trim();
        String location = req.getParameter("location").trim();
        String Avatar = req.getParameter("avatar").trim();

        // 验证是否为空
        if (username.equals("") || username == null){
            return Result.error(ResultCode.USER_Register_ERROR);
        }
        if (password.equals("") || password == null){
            return Result.error(ResultCode.USER_Register_ERROR);
        }
        // 验证码
        String checkCode = stringRedisTemplate.opsForValue().get(email);
        System.out.println("正确的验证码"+checkCode);
        if (!StringUtils.equalsIgnoreCase(userCode,checkCode)){
            return Result.error(ResultCode.USER_CHECK_CODE_ERROR);
        }

        // 验证用户是否已存在
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", username);
        User one = userService.getOne(queryWrapper);
        if (one != null){
            return Result.error(ResultCode.USER_HAS_EXISTED);
        }

        User user = new User();
        if (birth!=""){
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date myBirth = new Date();
            try {
                myBirth = dateFormat.parse(birth);
            } catch (Exception e){
                e.printStackTrace();
            }
            user.setBirth(myBirth);
        }

        user.setUsername(username);
        user.setPassword(MD5Util.MD5Lower(password));
        user.setSex(Integer.parseInt(sex));
        user.setEmail(email);
        user.setIntroduction("这个家伙很懒,什么都没有写...");
        user.setAvatar(Avatar);
        user.setLocation(location);

        boolean res = userService.save(user);
        if (res) {
            return Result.success();
        } else {
            return Result.error(ResultCode.ERROR);
        }
    }

    @Resource
    JavaMailSender jms;

    //发送验证码
    @PostMapping("/sigIn/code")
    public Result sendCode(HttpServletRequest req){
        String email = req.getParameter("email").trim();
        // 验证邮箱是否已存在
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("email", email);
        User one = userService.getOne(queryWrapper);
        if (one != null){
            return new Result(20008,"邮箱已被注册");
        }
        // 新邮箱注册  4位随机字符
        String checkCode = RandomUtil.randomString(4);
        System.out.println("验证码"+checkCode);
        stringRedisTemplate.opsForValue().set(email,checkCode,1, TimeUnit.MINUTES);
        EmailUtils.sendSimpleEmail(checkCode,email,jms);
        return Result.success();
    }

    //根据id获得user对象
    @GetMapping("/getById")
    public Result getById(String id){
        User user = userService.getById(id);
        return Result.success(user);
    }

}
