package com.example.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.annotation.Authentication;
import com.example.annotation.RateLimiter;
import com.example.annotation.RepeatSubmit;
import com.example.constant.AuthConstant;
import com.example.constant.RateLimiterType;
import com.example.constant.Result;
import com.example.constant.ResultCode;
import com.example.domain.bo.QiNiuImage;
import com.example.domain.dao.Article;
import com.example.domain.dao.Subscribe;
import com.example.domain.dao.User;
import com.example.domain.vo.Personal;
import com.example.service.ArticleService;
import com.example.service.SubscribeService;
import com.example.service.UserService;
import com.example.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.example.constant.UserLockState.LOCK_DOWN;
import static com.example.constant.UserLockState.UN_LOCK_DOWN;
import static com.example.utils.RedisConstants.*;

/**
 * @author zhw
 */
@Slf4j
@RestController
@RequestMapping("/api")
@Api(tags = "用户接口")
public class UserController implements InitializingBean {

    @Resource
    private UserService userService;

    @Resource
    private SubscribeService subscribeService;

    @Resource
    private ArticleService articleService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    QiniuServiceImpl qiniuService;

    @Resource
    JavaMailSender jms;

    @RateLimiter(time = 10, count = 5, limiterType = RateLimiterType.IP)
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public Result<List<String>> test(@RequestParam List<String> ids) {
        return Result.success(ids);
    }

    /**
     * 接入github登录所需参数
     */
    @Value("${github.client.id}")
    private String GITHUB_CLIENT_ID;
    @Value("${github.client.secret}")
    private String GITHUB_CLIENT_SECRET;
    @Value("${github.redirect.url}")
    private String GITHUB_REDIRECT_URL;

    @PostMapping(value = "/login/status")
    @ApiOperation(value = "登录")
    public Result<User> login(String username, String password) {
        return userService.login(username, password);
    }

    @GetMapping("githubLogin")
    @ApiOperation(value = "返回github授权服务器地址")
    public Result<String> githubLogin(HttpServletRequest request) {
        // Github认证服务器地址
        String url = "https://github.com/login/oauth/authorize";
        // 生成并保存state，忽略该参数有可能导致CSRF攻击
        String state = RandomUtil.randomString(4);
        stringRedisTemplate.opsForValue().set(IPUtils.getIpRequest(request), state, 30, TimeUnit.MINUTES);
        log.info("ip={}", IPUtils.getIpRequest(request));
        // 传递参数response_type、client_id、state、redirect_uri
        String param = "response_type=code&" + "client_id=" + GITHUB_CLIENT_ID + "&state=" + state
                + "&redirect_uri=" + GITHUB_REDIRECT_URL;
        return Result.success(url + "?" + param);
    }

    /**
     * @param code  授权码
     * @param state 应与发送时一致
     */
    @GetMapping("/githubCallback")
    @ApiOperation(value = "用户同意授权后的GitHub回调接口")
    public Result<User> githubCallback(String code, String state, HttpServletRequest request) throws Exception {
        // 验证state，如果不一致，可能被CSRF攻击
        String s = stringRedisTemplate.opsForValue().get(IPUtils.getIpRequest(request));
        if (!state.equals(s)) {
            throw new Exception("GitHub授权登录State验证失败");
        }
        // 2、向GitHub认证服务器申请令牌
        String url = "https://github.com/login/oauth/access_token";
        // 传递参数grant_type、code、redirect_uri、client_id
        String param = "?grant_type=authorization_code&code=" + code + "&redirect_uri=" +
                GITHUB_REDIRECT_URL + "&client_id=" + GITHUB_CLIENT_ID + "&client_secret=" + GITHUB_CLIENT_SECRET;

        // 申请令牌，注意此处为post请求
        String result = HttpUtil.createPost(url + param).execute().body();

        /*
         * result示例：
         * 失败：error=incorrect_client_credentials&error_description=The+client_id+and%2For+client_secret+passed+are+incorrect.&
         * error_uri=https%3A%2F%2Fdeveloper.github.com%2Fapps%2Fmanaging-oauth-apps%2Ftroubleshooting-oauth-app-access-token-request-errors%2F%23incorrect-client-credentials
         * 成功：access_token=7c76186067e20d6309654c2bcc1545e41bac9c61&scope=&token_type=bearer
         */
        log.info("申请令牌返回值:{}", result);
        Map<String, String> resultMap = HttpUtils.params2Map(result);
        // 如果返回的map中包含error，表示失败，错误原因存储在error_description
        if (resultMap.containsKey("error")) {
            throw new Exception(resultMap.get("error_description"));
        }

        // 如果返回结果中包含access_token，表示成功
        if (!resultMap.containsKey("access_token")) {
            throw new Exception("获取token失败");
        }

        // 得到token和token_type
        String accessToken = resultMap.get("access_token");
        String tokenType = resultMap.get("token_type");
        log.info("accessToken={},tokenType={}", accessToken, tokenType);
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
        log.info("user{}", user);
        if (ObjectUtil.isEmpty(user)) {
            user.setId(id);
            user.setUsername(login);
            user.setAvatar(avatar_url);
            user.setPassword("github-login");
            userService.save(user);
        }
        // 生成token
        String token = JWTUtil.sign(user.getId(), user.getPassword());
        user.setPassword("it's a secret");
        user.setToken(token);
        Map<String, Object> userMap = BeanUtil.beanToMap(user);
        stringRedisTemplate.opsForHash().putAll(LOGIN_TOKEN_KEY + token, userMap);
        stringRedisTemplate.expire(LOGIN_TOKEN_KEY + token, LOGIN_TOKEN_TTL, TimeUnit.MINUTES);
        return Result.success(user);
    }

    @PostMapping(value = "/user/add")
    @RepeatSubmit
    @ApiOperation(value = "用户注册")
    public Result<Void> addUser(HttpServletRequest req) {
        return userService.signUp(req);
    }

    @PostMapping("/sigIn/code")
    @RepeatSubmit
    @ApiOperation(value = "发送验证码")
    public Result<Void> sendCode(HttpServletRequest req) {
        String email = req.getParameter("email").trim();
        // 验证邮箱是否已存在
        User one = userService.lambdaQuery().eq(User::getEmail, email).one();
        if (one != null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }
        // 新邮箱注册  4位随机字符
        String checkCode = RandomUtil.randomString(4);
        log.info("生成的验证码:{}", checkCode);
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + email, checkCode, LOGIN_CODE_TTL, TimeUnit.MINUTES);
        EmailUtils.sendSimpleEmail(checkCode, email, jms);
        return Result.success();
    }

    @GetMapping("/getUser")
    @Authentication(role = AuthConstant.USER)
    @ApiOperation(value = "获得所登录用户的信息")
    public Result<User> getById() {
        String currentUser = UserUtils.getCurrentUser();
        User user = userService.getById(currentUser);
        user.setPassword("it's a secret");
        return Result.success(user);
    }

    @GetMapping("/getUserById/{id}")
    @ApiOperation(value = "获得指定id用户的可公开信息")
    public Result<User> getUserById(@PathVariable String id) {
        User user = userService.query().select("id", "username", "avatar", "score", "sex", "introduction", "birth").eq("id", id).one();
        return Result.success(user);
    }

    @GetMapping("/getByScore")
    @ApiOperation(value = "获得积分排行的用户列表")
    public Result<List<User>> getByScore() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "username", "avatar", "score", "sex", "introduction", "birth");
        queryWrapper.last("order by score desc limit 6");
        List<User> list = userService.list(queryWrapper);
        return Result.success(list);
    }

    @PostMapping("/updateUser")
    @RepeatSubmit
    @Authentication(role = AuthConstant.USER)
    @ApiOperation(value = "更新用户信息")
    public Result<Void> updateUser(@RequestBody User user) {
        User newUser = new User();
        newUser.setUpdateTime(DateUtil.date());
        newUser.setBirth(user.getBirth());
        newUser.setUsername(user.getUsername());
        newUser.setSex(user.getSex());
        newUser.setPhoneNum(user.getPhoneNum());
        newUser.setEmail(user.getEmail());
        newUser.setIntroduction(user.getIntroduction());
        newUser.setLocation(user.getLocation());
        newUser.setId(UserUtils.getCurrentUser());
        boolean b = userService.updateById(newUser);
        return b ? Result.success() : Result.error(ResultCode.ERROR);
    }

    @PostMapping("/avatar/update")
    @ApiOperation(value = "更新头像")
    public Result<String> updateAvatar(@RequestParam("file") MultipartFile avatarFile) {
        if (avatarFile.isEmpty()) {
            return Result.error(ResultCode.UPLOAD_ERROR);
        }
        try {
            QiNiuImage avatarImages = qiniuService.saveToQiNiu(avatarFile, "avatarImages");
            log.info(avatarImages.toString());
            User user = new User();
            user.setId(UserUtils.getCurrentUser());
            user.setAvatar(avatarImages.getFileName());
            boolean res = userService.updateById(user);
            if (res) {
                return Result.success(avatarImages.getFileName());
            } else {
                return Result.error(ResultCode.UPLOAD_ERROR);
            }
        } catch (Exception e) {
            log.info("更新头像失败{}", String.valueOf(e));
            return Result.error(ResultCode.INTERFACE_INNER_INVOKE_ERROR);
        }
    }

    @GetMapping("getPersonal")
    @Authentication(role = AuthConstant.USER)
    @ApiOperation(value = "获得个人数据")
    public Result<Personal> getPersonal() {
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

    @Authentication
    @PostMapping("getAllUser")
    @ApiOperation(value = "管理员获得所有用户")
    public Result<List<User>> getAllUser() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.ne("role", AuthConstant.ADMIN.toString())
                .select(User.class, info -> !"password".equals(info.getColumn()));
        List<User> users = userService.list(wrapper);
        return Result.success(users);
    }

    @Authentication
    @PostMapping("lockOrUnlock/{id}")
    @ApiOperation(value = "管理员ockOrUnlock用户")
    public Result<Integer> lockOrUnlock(@PathVariable String id) {
        User user = userService.getById(id);
        String lockState = user.getLockState();
        //解封
        if (LOCK_DOWN.equals(lockState)) {
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

    @Override
    public void afterPropertiesSet() throws Exception {
        //使用Jackson2JsonRedisSerialize 替换默认序列化
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        stringRedisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
    }
}
