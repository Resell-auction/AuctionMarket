package com.example.auctionmarket.common.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAspect {

    private final RedissonClient redissonClient;//RLock얻기 위한 핵심 객체

    @Around("@annotation(distributedLock)")//어노테이션 생성
    public Object lock(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        String key = "lock:" + parseKey(joinPoint, distributedLock.key());
        RLock lock = redissonClient.getLock(key);
        boolean locked = false;

        try {
            log.info("🔐 [락 시도] 키: {}", key); // 로그 1 - 락 시도

            locked = lock.tryLock(3, 1, TimeUnit.SECONDS); // 3초 동안 시도, 성공 시 1초 락 유지
            if (!locked) {
                log.warn("❌ [락 실패] 키: {}", key);

                throw new RuntimeException("[락 실패] 다른 사용자가 사용 중입니다: " + key);
            }
            log.info("✅ [락 성공] 키: {}", key); // 로그 2 - 락 성공

            return joinPoint.proceed(); // 락을 가진 상태에서 원래 메서드 실행

        } finally {
            if (locked) {
                lock.unlock(); // 락을 획득했을 때만 해제
                log.info("🔓 [락 해제] 키: {}", key); // 로그 3 - 락 해제

            }
        }
    }


        private String parseKey (ProceedingJoinPoint joinPoint, String keyExpression){
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String[] parameterNames = signature.getParameterNames();
            Object[] args = joinPoint.getArgs();

            EvaluationContext context = new StandardEvaluationContext();
            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }

            ExpressionParser parser = new SpelExpressionParser();
            return parser.parseExpression(keyExpression).getValue(context, String.class);
        }
    }

