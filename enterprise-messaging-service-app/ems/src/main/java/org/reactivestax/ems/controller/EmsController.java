package org.reactivestax.ems.controller;

import org.reactivestax.ems.service.EmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ems")
public class EmsController {

    @Autowired
    private EmsService emsService;



}
