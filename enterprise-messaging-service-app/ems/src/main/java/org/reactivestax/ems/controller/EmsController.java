package org.reactivestax.ems.controller;

import org.reactivestax.ems.dto.CallDTO;
import org.reactivestax.ems.dto.EmailDTO;
import org.reactivestax.ems.dto.SmsDTO;
import org.reactivestax.ems.service.EmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ems")
public class EmsController {

    @Autowired
    private EmsService emsService;

    @PostMapping("/sms")
    public void sendMessageViaSms(@RequestBody SmsDTO smsDTO){

    }

    @PostMapping("/call")
    public void sendMessageViaCall(@RequestBody CallDTO callDTO){

    }

    @PostMapping("/email")
    public void sendMessageViaEmail(@RequestBody EmailDTO emailDTO){

    }

}
