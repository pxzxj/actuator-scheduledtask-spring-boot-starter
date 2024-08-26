package io.github.pxzxj;

import ch.qos.logback.classic.Logger;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AutoConfiguration(after = {DataSourceAutoConfiguration.class})
@ConditionalOnClass(Logger.class)
@EnableConfigurationProperties(ScheduleProperties.class)
public class ScheduleAutoConfiguration {




}
