package org.jhe.swagger.interceptor.config;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.spring.SpringCacheManager;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.plugin.segmentation.SegmentationPolicy;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.expiry.Duration;
import javax.cache.expiry.TouchedExpiryPolicy;

@Configuration
@EnableCaching
public class IgniteCacheConfiguration {
	private static final Logger LOGGER = LoggerFactory.getLogger(IgniteCacheConfiguration.class);

	@Value("${ignite.ttl}")
	private int ttl;
	@Value("${ignite.instance}")
	private String instance;
	@Value("${ignite.multicast.group:}")
	private String multicastGroup;
	@Value("${ignite.addresses}")
	private String addresses;

	@Bean
	public SpringCacheManager springCacheManager() {
		SpringCacheManager springCacheManager = new SpringCacheManager();
		springCacheManager.setDynamicCacheConfiguration(getCacheConfiguration());
		springCacheManager.setConfiguration(getIgniteConfiguration());
		// FIXED : fixing a problem with actuator & ignite : https://stackoverflow.com/questions/55481331/spring-boot-cache-apache-ignite-spring-boot-actuator-application-fail-to-s
		springCacheManager.onApplicationEvent(null);

		return springCacheManager;
	}

	private CacheConfiguration<Object, Object> getCacheConfiguration() {
		return new CacheConfiguration<>()
//				.setCacheMode(CacheMode.PARTITIONED)
				.setCacheMode(CacheMode.REPLICATED)
				.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL)
				.setExpiryPolicyFactory(TouchedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, ttl)))
				.setEagerTtl(true)
				.setBackups(0)
				.setStatisticsEnabled(true); // --> creates in memory h2 db with stats.

		//.setRebalanceBatchSize(1024 * 1024)
				//.setRebalanceThrottle(50);

	}

	private IgniteConfiguration getIgniteConfiguration() {
		return new IgniteConfiguration()
				.setIgniteInstanceName(instance)
				.setDiscoverySpi(getTcpDiscoverySpi())
				.setSegmentationPolicy(SegmentationPolicy.RESTART_JVM)
				.setCommunicationSpi(getTcpCommunicationSpi());
		/*
		setPeerClassLoadingEnabled(false)
				.setRebalanceThreadPoolSize(4)
				.setPublicThreadPoolSize(8)
				.setSystemThreadPoolSize(4)
				*/
	}


	private TcpCommunicationSpi getTcpCommunicationSpi(){
		return new TcpCommunicationSpi().setMessageQueueLimit(16384).setSlowClientQueueLimit(8096);
	}

	private TcpDiscoverySpi getTcpDiscoverySpi() {
		return new TcpDiscoverySpi()
				.setIpFinder(getTcpDiscoveryMulticastIpFinder()
				);
	}

	private TcpDiscoveryVmIpFinder getTcpDiscoveryMulticastIpFinder() {
		if (StringUtils.isBlank(multicastGroup)) {
			LOGGER.warn("no multicast group set ! this should happen only in test mode");

			return new TcpDiscoveryVmIpFinder()
					.setAddresses(Collections.singletonList(addresses));
		}

		return new TcpDiscoveryMulticastIpFinder()
				.setMulticastGroup(multicastGroup)
				.setAddresses(Collections.singletonList(addresses));
	}
}
