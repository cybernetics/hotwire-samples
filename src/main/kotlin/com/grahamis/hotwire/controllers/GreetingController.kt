package com.grahamis.hotwire.controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class GreetingController {

    @RequestMapping("/greeting")
    fun greeting(model: Model, @RequestParam params: Map<String, String>): String {
        if (params.getOrDefault("sleep", "false").toBoolean()) Thread.sleep(1000) // show Turbo Drive progress bar
        model.addAttribute("person", params.getOrDefault("person", "world"))
        return "greeting"
    }
}
