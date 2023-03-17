package com.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.constant.Result;
import com.example.constant.ResultCode;
import com.example.domain.User;
import com.example.service.UserService;
import com.example.mapper.UserMapper;
import com.example.utils.JWTUtil;
import com.example.utils.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.example.utils.RedisConstants.*;

/**
* @author 24668
* @description 针对表【tb_user】的数据库操作Service实现
* @createDate 2022-09-27 15:26:49
*/
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result login(String username, String password) {
        Result result = new Result();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        queryWrapper.eq("password", MD5Util.MD5Lower(password));
        List<User> list = list(queryWrapper);
        if (list.size()==0){
            result.setResultCode(ResultCode.USER_LOGIN_ERROR);
            return result;
        }
        User user = list.get(0);
        if ("0".equals(user.getLockState())){
            return Result.error(ResultCode.USER_ACCOUNT_FORBIDDEN);
        }
        // 生成token
        String token = JWTUtil.sign(user.getId(),user.getPassword());
        user.setPassword("it's a secret");
        user.setToken(token);
        //缓存到Redis
        Map<String,Object> userMap = BeanUtil.beanToMap(user);
        //使用Jackson2JsonRedisSerialize 替换默认序列化
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        stringRedisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        stringRedisTemplate.opsForHash().putAll(LOGIN_TOKEN_KEY+token,userMap);
        stringRedisTemplate.expire(LOGIN_TOKEN_KEY+token,LOGIN_TOKEN_TTL, TimeUnit.MINUTES);

        result.setResultCode(ResultCode.SUCCESS);
        result.setData(user);
        return result;
    }

    @Override
    public Result signUp(HttpServletRequest req) {
        String username = req.getParameter("username").trim();
        String password = req.getParameter("password").trim();
        String sex = req.getParameter("sex").trim();
        String email = req.getParameter("email").trim();
        String userCode = req.getParameter("checkCode").trim();
        String birth = req.getParameter("birth").trim();
        String location = req.getParameter("location").trim();
        String avatar = req.getParameter("avatar").trim();
        // 验证是否为空
        if ("".equals(username)){
            return Result.error(ResultCode.USER_Register_ERROR);
        }
        if ("".equals(password)){
            return Result.error(ResultCode.USER_Register_ERROR);
        }
        // 验证码
        String checkCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY +email);
        log.info("正确的验证码:{}",checkCode);
        if (!StringUtils.equalsIgnoreCase(userCode,checkCode)){
            return Result.error(ResultCode.USER_CHECK_CODE_ERROR);
        }

        // 验证用户是否已存在
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", username);
        User one = getOne(queryWrapper);
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
        user.setAvatar(avatar);
        user.setLocation(location);
        boolean res = save(user);
        if (res) {
            return Result.success();
        } else {
            return Result.error(ResultCode.ERROR);
        }
    }

}




