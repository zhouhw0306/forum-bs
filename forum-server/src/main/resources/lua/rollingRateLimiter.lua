-- 滑动窗口限流器
-- 限流器名称  一般是 前缀:请求IP 例如： rate:limiter:192.168.0.1
local zSetName = KEYS[1]
-- uuid
local uuid = KEYS[2]
-- 当前时间戳, 精确到毫秒
local now = tonumber(KEYS[3])
-- 速率限制器区间的长度，以毫秒为单位。例如，如果您希望用户能够每分钟执行5个操作，则应为60000。
local interval = tonumber(ARGV[1])
-- 每个间隔允许的操作数量。例如，在上述场景中，这将是5
local maxInInterval = tonumber(ARGV[2])

-- 1 删除集合中距离当前时间超过interval的数据
redis.call('ZREMRANGEBYSCORE', zSetName, 0, now - interval)
-- 2 获取集合中元素的数量
local count = redis.call('ZCARD', zSetName)

-- 3 判断 count 是否大于 maxInInterval
if count >= maxInInterval then
    -- 设置过期时间
    redis.call('EXPIRE', zSetName, math.ceil(interval/1000))
    -- 请求数量大于阈值 返回 -1
    return -1
end

-- 4 将当前请求存入集合
redis.call('ZADD', zSetName, now, uuid)
-- 设置过期时间
redis.call('EXPIRE', zSetName, math.ceil(interval/1000))
-- 返回成功
return 1
