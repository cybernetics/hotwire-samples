package com.grahamis.hotwire.config

import com.grahamis.CustomMediaType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.thymeleaf.spring5.SpringWebFluxTemplateEngine
import org.thymeleaf.spring5.view.reactive.ThymeleafReactiveViewResolver
import java.nio.charset.StandardCharsets

@Configuration
class ThymeleafConfig {
    @Bean
    fun viewResolver(templateEngine: SpringWebFluxTemplateEngine?): ThymeleafReactiveViewResolver {
        val viewResolver = ThymeleafReactiveViewResolver()
        viewResolver.supportedMediaTypes = listOf(CustomMediaType.TURBO_STREAM)
        viewResolver.defaultCharset = StandardCharsets.UTF_8
        viewResolver.order = 0
        viewResolver.viewNames = arrayOf("*.turbo-stream") // assumes additional filename suffix of `.html` as these template files are "mostly" HTML
        viewResolver.templateEngine = templateEngine
        return viewResolver
    }
}

