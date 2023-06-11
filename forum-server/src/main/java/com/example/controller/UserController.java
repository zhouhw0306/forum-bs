package com.example.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.http.HttpUtil;
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
import com.example.utils.*;
import com.example.vo.Personal;
import lombok.extern.slf4j.Slf4j;
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
    private UserService userService;

    @Resource
    private SubscribeService subscribeService;

    @Resource
    private ArticleService articleService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 头像存放路径
     */
    @Value("${me.avatar.path}")
    private String avatarPath;

    /**
     * 接入github登录所需参数
     */
    @Value("${github.client.id}")
    private String GITHUB_CLIENT_ID;
    @Value("${github.client.secret}")
    private String GITHUB_CLIENT_SECRET;
    @Value("${github.redirect.url}")
    private String GITHUB_REDIRECT_URL;

    //登录
    @RequestMapping(value = "/login/status",method = RequestMethod.POST)
    public Result login(String username, String password){
        return userService.login(username,password);
    }

    // github登录
    @GetMapping("githubLogin")
    public Result githubLogin(HttpServletRequest request){
        // Github认证服务器地址
        String url = "https://github.com/login/oauth/authorize";
        // 生成并保存state，忽略该参数有可能导致CSRF攻击
        String state = RandomUtil.randomString(4);
        log.info("**{}",request.getRemoteAddr());
        //stringRedisTemplate.opsForValue().set(request.getRemoteAddr(),state,30, TimeUnit.MINUTES);
        // 传递参数response_type、client_id、state、redirect_uri
        String param = "response_type=code&" + "client_id=" + GITHUB_CLIENT_ID + "&state=" + state
                + "&redirect_uri=" + GITHUB_REDIRECT_URL;
        return Result.success(url + "?" + param);
    }

   /**
     * GitHub回调方法
     * @param code 授权码
     * @param state 应与发送时一致
     * @author jitwxs
     * @since 2018/5/21 15:24
    */
    @GetMapping("/githubCallback")
    public Result githubCallback(String code, String state) throws Exception {
        // 验证state，如果不一致，可能被CSRF攻击
//        String s = stringRedisTemplate.opsForValue().get(request.getRemoteAddr());
//        if(!state.equals(s)) {
//            throw new Exception("State验证失败");
//        }
        // 2、向GitHub认证服务器申请令牌
        String url = "https://github.com/login/oauth/access_token";
        // 传递参数grant_type、code、redirect_uri、client_id
        String param = "?grant_type=authorization_code&code=" + code + "&redirect_uri=" +
                GITHUB_REDIRECT_URL + "&client_id=" + GITHUB_CLIENT_ID + "&client_secret=" + GITHUB_CLIENT_SECRET;

        // 申请令牌，注意此处为post请求
        String result = HttpUtil.createPost(url+param).execute().body();

        /*
         * result示例：
         * 失败：error=incorrect_client_credentials&error_description=The+client_id+and%2For+client_secret+passed+are+incorrect.&
         * error_uri=https%3A%2F%2Fdeveloper.github.com%2Fapps%2Fmanaging-oauth-apps%2Ftroubleshooting-oauth-app-access-token-request-errors%2F%23incorrect-client-credentials
         * 成功：access_token=7c76186067e20d6309654c2bcc1545e41bac9c61&scope=&token_type=bearer
         */
        log.info("申请令牌返回值:{}",result);
        Map<String, String> resultMap = HttpUtils.params2Map(result);
        // 如果返回的map中包含error，表示失败，错误原因存储在error_description
        if(resultMap.containsKey("error")) {
            throw new Exception(resultMap.get("error_description"));
        }

        // 如果返回结果中包含access_token，表示成功
        if(!resultMap.containsKey("access_token")) {
            throw new Exception("获取token失败");
        }

        // 得到token和token_type
        String accessToken = resultMap.get("access_token");
        String tokenType = resultMap.get("token_type");
        log.info("accessToken={},tokenType={}",accessToken,tokenType);
        // 3、向资源服务器请求用户信息，携带access_token和tokenType
        String userUrl = "https://api.github.com/user";
        //String userParam = "?access_token=" + accessToken + "&token_type=" + tokenType;

        // 申请资源
        String userResult = HttpUtil.createGet(userUrl).header("Authorization", "token " + accessToken).execute().body();
        JSONObject jsonObject = JSONUtil.parseObj(userResult);
        String login = jsonObject.getStr("login");
        String id = jsonObject.getStr("id");
        String avatar_url = jsonObject.getStr("avatar_url");
        User user = userService.getById(id);
        log.info("user{}",user);
        if (ObjectUtil.isEmpty(user)){
            user.setId(id);
            user.setUsername(login);
            user.setAvatar(avatar_url);
            user.setPassword("github-login");
            userService.save(user);
        }
        // 生成token
        String token = JWTUtil.sign(user.getId(),user.getPassword());
        user.setPassword("it's a secret");
        user.setToken(token);
        Map<String,Object> userMap = BeanUtil.beanToMap(user);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        stringRedisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        stringRedisTemplate.opsForHash().putAll(LOGIN_TOKEN_KEY+token,userMap);
        stringRedisTemplate.expire(LOGIN_TOKEN_KEY+token,LOGIN_TOKEN_TTL, TimeUnit.MINUTES);
        return Result.success(user);
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
