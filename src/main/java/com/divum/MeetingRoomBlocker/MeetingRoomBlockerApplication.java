package com.divum.MeetingRoomBlocker;

import com.divum.MeetingRoomBlocker.Scheduler.FeedBackScheduler;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing
@EnableAsync
@EnableEncryptableProperties
@RequiredArgsConstructor
public class MeetingRoomBlockerApplication {

    private final FeedBackScheduler feedBackScheduler;

    public static void main(String[] args){
        SpringApplication.run(MeetingRoomBlockerApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void run() throws Exception {
        feedBackScheduler.scheduleTask();
    }

    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("Mail");
        executor.initialize();
        return executor;
    }

}