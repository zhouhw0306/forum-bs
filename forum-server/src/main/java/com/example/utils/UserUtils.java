package com.example.utils;



import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author 24668
 */
@Component
public class UserUtils {


    @Resource
    StringRedisTemplate stringRedisTemplate;

    static UserUtils userUtils;

    @PostConstruct
    public void init(){
        userUtils=this;
        userUtils.stringRedisTemplate = this.stringRedisTemplate;
    }
    //获得当前用户
    public static String getCurrentUser() {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
//        if (StringUtils.isBlank(token)) {
//            return null;
//        }
//        Map<Object, Object> userMap = userUtils.stringRedisTemplate.opsForHash().entries(token);
//        if (userMap.isEmpty()){
//            return null;
//        }
//        User user = BeanUtil.fillBeanWithMap(userMap,new User(),false);
        String userId = JWTUtil.getUserId(token);
        return userId;
    }

}
