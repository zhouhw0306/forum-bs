package com.example.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.jwt.JWTException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.annotation.Authentication;
import com.example.constant.AuthConstant;
import com.example.constant.Result;
import com.example.constant.ResultCode;
import com.example.domain.Article;
import com.example.domain.Subscribe;
import com.example.domain.User;
import com.example.service.ArticleService;
import com.example.service.SubscribeService;
import com.example.service.UserService;
import com.example.utils.EmailUtils;
import com.example.utils.JWTUtil;
import com.example.utils.MD5Util;
import com.example.utils.UserUtils;
import com.example.vo.Personal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import static com.example.constant.UserLockState.LOCK_DOWN;
import static com.example.constant.UserLockState.UN_LOCK_DOWN;
import static com.example.utils.RedisConstants.*;

/**
 * 用户接口
 * @author zhw
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class UserController {

    @Resource
    UserService userService;

    @Resource
    SubscribeService subscribeService;

    @Resource
    ArticleService articleService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Value("${me.avatar.path}")
    private String avatarPath;

    //登录
    @RequestMapping(value = "/login/status",method = RequestMethod.POST)
    public Result login(String username, String password){
        return userService.login(username,password);
    }


    //注册用户
    @RequestMapping(value = "/user/add", method = RequestMethod.POST)
    public Result addUser(HttpServletRequest req){
        return userService.signUp(req);
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
        log.info("生成的验证码:{}",checkCode);
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + email,checkCode,LOGIN_CODE_TTL, TimeUnit.MINUTES);
        EmailUtils.sendSimpleEmail(checkCode,email,jms);
        return Result.success();
    }

    //获得所登录用户的信息
    @GetMapping("/getUser")
    public Result getById(){
        String currentUser = UserUtils.getCurrentUser();
        if (currentUser == null){
            return Result.error(ResultCode.USER_NOT_LOGGED_IN);
        }
        User user = userService.getById(currentUser);
        user.setPassword("it's a secret");
        return Result.success(user);
    }

    //获得指定id用户部分信息
    @GetMapping("/getUserById/{id}")
    public Result getUserById(@PathVariable String id){
        User user = userService.query().select("id","username","avatar","score","sex","introduction","birth").eq("id",id).one();
        return Result.success(user);
    }

    //根据积分排行
    @GetMapping("/getByScore")
    public Result getByScore(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id","username","avatar","score","sex","introduction","birth");
        queryWrapper.last("order by score desc limit 6");
        List<User> list = userService.list(queryWrapper);
        return Result.success(list);
    }

    //更新用户信息
    @PostMapping("/updateUser")
    public Result updateUser(@RequestBody User user){
        User newUser = new User();
        newUser.setUpdateTime(DateUtil.date());
        newUser.setBirth(user.getBirth());
        newUser.setUsername(user.getUsername());
        newUser.setSex(user.getSex());
        newUser.setPhoneNum(user.getPhoneNum());
        newUser.setEmail(user.getEmail());
        newUser.setIntroduction(user.getIntroduction());
        newUser.setLocation(user.getLocation());
        newUser.setId(user.getId());
        boolean b = userService.updateById(newUser);
        return b?Result.success():Result.error(ResultCode.ERROR);
    }

    //更新头像
    @PostMapping("/avatar/update")
    public Result updateAvatar(@RequestParam("file") MultipartFile avatarFile, @RequestParam("id") String id){
        if (avatarFile.isEmpty()) {
            return Result.error(ResultCode.UPLOAD_ERROR);
        }
        String fileName = System.currentTimeMillis()+avatarFile.getOriginalFilename();
        File file = new File(avatarPath);
        if (!file.exists()){
            file.mkdir();
        }
        File dest = new File(avatarPath + fileName);
        String storeAvatarPath = "/avatarImages/"+fileName;
        try {
            avatarFile.transferTo(dest);
            User user = new User();
            user.setId(id);
            user.setAvatar(storeAvatarPath);
            boolean res = userService.updateById(user);
            if (res){
                return Result.success(storeAvatarPath);
            }else {
                return Result.error(ResultCode.UPLOAD_ERROR);
            }
        }catch (IOException e){
            log.info("修改失败{}",avatarPath+fileName);
            log.info(String.valueOf(e));
            return Result.error(ResultCode.INTERFACE_INNER_INVOKE_ERROR);
        }
    }

    //获得个人数据
    @GetMapping("getPersonal")
    public Result getPersonal(){
        String userId = UserUtils.getCurrentUser();
        User user = userService.getById(userId);
        Personal personal = new Personal();
        personal.setScore(user.getScore());
        List<Article> articles = articleService.list(new QueryWrapper<Article>().eq("user_id", userId));
        personal.setWriteNum(articles.size());
        List<Subscribe> subscribes = subscribeService.list(new QueryWrapper<Subscribe>().eq("subscribe", userId));
        List<Subscribe> be_subscribes = subscribeService.list(new QueryWrapper<Subscribe>().eq("be_subscribe", userId));
        personal.setIdol(subscribes.size());
        personal.setFollowers(be_subscribes.size());
        return Result.success(personal);
    }

    //所有用户
    @Authentication
    @PostMapping("getAllUser")
    public Result getAllUser(){
        QueryWrapper<User> wrapper= new QueryWrapper<>();
        wrapper.ne("role",AuthConstant.ADMIN.toString())
                .select(User.class,info -> !"password".equals(info.getColumn()));
        List<User> users = userService.list(wrapper);
        return Result.success(users);
    }

    //lockOrUnlock 用户
    @Authentication
    @PostMapping("lockOrUnlock/{id}")
    public Result lockOrUnlock(@PathVariable String id){
        User user = userService.getById(id);
        String lockState = user.getLockState();
        //解封
        if (LOCK_DOWN.equals(lockState)){
            User upUser = new User();
            upUser.setId(id);
            upUser.setLockState(UN_LOCK_DOWN);
            boolean flag = userService.updateById(upUser);
            return flag ? Result.success(1) : Result.error();
        }
        //封禁
        User upUser = new User();
        upUser.setId(id);
        upUser.setLockState(LOCK_DOWN);
        boolean flag = userService.updateById(upUser);
        return flag ? Result.success(0) : Result.error();
    }

}
