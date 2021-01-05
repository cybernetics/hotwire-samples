package com.grahamis

import org.hamcrest.core.StringRegularExpression
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.XpathAssertions
import java.net.URI

infix fun WebTestClient.getResponse(uri: URI) = get().uri(uri).exchange()
infix fun WebTestClient.BodyContentSpec.elementOfId(id: String) = xpath("//*[@id='$id']")
infix fun WebTestClient.BodyContentSpec.hasElement(tag: String) = xpath("//$tag").exists()
infix fun XpathAssertions.matches(regex: String) = string(StringRegularExpression.matchesRegex(regex))
