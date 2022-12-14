package com.example.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import org.springframework.beans.factory.annotation.Autowired;
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
        stringRedisTemplate.opsForHash().putAll(LOGIN_TOKEN_KEY+token,userMap);
        stringRedisTemplate.expire(LOGIN_TOKEN_KEY+token,LOGIN_TOKEN_TTL,TimeUnit.MINUTES);

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
        String avatar = req.getParameter("avatar").trim();
        String dept = req.getParameter("dept").trim();
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
        user.setAvatar(avatar);
        user.setLocation(location);
        user.setDept(dept);
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
        log.info("生成的验证码:{}",checkCode);
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + email,checkCode,LOGIN_CODE_TTL, TimeUnit.MINUTES);
        EmailUtils.sendSimpleEmail(checkCode,email,jms);
        return Result.success();
    }

    //根据id获得user对象
    @GetMapping("/getById")
    public Result getById(String id){
        User user = userService.getById(id);
        user.setPassword("it's a secret");
        return Result.success(user);
    }

    //根据积分排行
    @GetMapping("/getByScore")
    public Result getByScore(){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id","username","avatar","score");
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
        newUser.setDept(user.getDept());
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
        String filePath = System.getProperty("user.dir") + System.getProperty("file.separator") + "forum-server/data/avatarImages" ;
        File file1 = new File(filePath);
        if (!file1.exists()){
            file1.mkdir();
        }
        File dest = new File(filePath + System.getProperty("file.separator") + fileName);
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

    //获得背景图
    @GetMapping("initBg")
    public Result initBg(){
        String JsonSrc = HttpUtil.get("https://api.btstu.cn/sjbz/?lx=dongman&format=json");
        JSONObject jsonObject = JSONUtil.parseObj(JsonSrc);
        String imgurl = jsonObject.getStr("imgurl");
        return Result.success(imgurl);
    }
}
