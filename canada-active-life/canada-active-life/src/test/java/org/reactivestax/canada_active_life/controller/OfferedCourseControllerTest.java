package org.reactivestax.canada_active_life.controller;

import org.reactivestax.canada_active_life.service.OfferedCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OfferedCourseController.class)
public class OfferedCourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OfferedCourseService offeredCourseService;

}
