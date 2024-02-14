package joo.project.my3d.aop;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@Aspect
@RequiredArgsConstructor
public class TimeTraceAspect {
    private final MeterRegistry meterRegistry;

    @Pointcut("@annotation(joo.project.my3d.aop.TimeTrace)")
    private void timeTracePointcut() {}

    @Around("timeTracePointcut()")
    public Object traceTime(ProceedingJoinPoint joinPoint) throws Throwable {
        Timer timer = Timer
                .builder("response_time_ms")
                .register(meterRegistry);

        StopWatch stopWatch = new StopWatch();

        try {
            stopWatch.start();
            return joinPoint.proceed(); // 실제 타겟 호출
        } finally {
            stopWatch.stop();
            timer.record(stopWatch.getTotalTimeMillis(), TimeUnit.MILLISECONDS);
            log.debug(
                    "{} - Total time = {}s = {}ms",
                    joinPoint.getSignature().toShortString(),
                    stopWatch.getTotalTimeSeconds(),
                    stopWatch.getTotalTimeMillis());
        }
    }
}
