package com.aufi.producer.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.aufi.producer.service.ProducerService;

@RestController
public class ProducerController {
    @Autowired
    private ProducerService producerService;

    @GetMapping("/send")
    public String sendMessage(@RequestParam(value = "pesan", defaultValue = "Halo Dunia") String pesan) {
        producerService.sendMassage(pesan);
        return "Pesan terkirim: " + pesan;
    }
}
