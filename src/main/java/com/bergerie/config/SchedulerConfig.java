package com.bergerie.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Activation du support des tâches planifiées (@Scheduled) dans l'application Spring Boot.
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {
}
