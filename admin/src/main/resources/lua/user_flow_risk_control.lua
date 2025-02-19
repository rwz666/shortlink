-- 设置用户范文频率限制的参数
local username = KEYS[1]
local timeWindow = tonumber(ARGV[1]) -- 时间窗口，单位：秒

-- 构造 Redis 中触怒住用户访问次数的 Key
local accessKey = "short-link:user-flow-risk-control:" ..username

-- 原子递增访问次数，并获取递增后的值
local currentAccessCount = redis.call("INCR", accessKey)

if currentAccessCount == 1 then
    -- 第一次请求过来，设置键的过期时间
    redis.call("EXPIRE", accessKey, timeWindow)
end
-- 返回当前访问次数
return currentAccessCount