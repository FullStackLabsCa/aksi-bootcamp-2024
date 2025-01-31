package org.reactivestax.canada_active_life.controller;

import org.reactivestax.canada_active_life.dto.OfferedCourseDTO;
import org.reactivestax.canada_active_life.service.OfferedCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/CanadaActiveLife/v1/offeredCourses")
public class OfferedCourseController {

    @Autowired
    private OfferedCourseService offeredCourseService;

    @PostMapping
    public ResponseEntity<String> createNewOfferedCourse(@RequestBody OfferedCourseDTO offeredCourseDTO){

        boolean isOfferedCourseCreated = offeredCourseService.createNewOfferedCourse(offeredCourseDTO);

        if(isOfferedCourseCreated) return ResponseEntity.ok("Offered Course created.");
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Offered Course could not be created.");
    }

    @GetMapping
    public ResponseEntity<OfferedCourseDTO> getOfferedCourseDetails(@RequestParam String offeredCourseId){
        OfferedCourseDTO offeredCourseDTO = offeredCourseService.getOfferedCourse(Integer.parseInt(offeredCourseId));
        return ResponseEntity.ok(offeredCourseDTO);
    }

    @PatchMapping
    public ResponseEntity<OfferedCourseDTO> updateOfferedCourse(@RequestBody OfferedCourseDTO offeredCourseDTO){
        OfferedCourseDTO updatedOfferedCourseDTO = offeredCourseService.updateOfferedCourseInfo(offeredCourseDTO);

        if(updatedOfferedCourseDTO != null) return ResponseEntity.ok(updatedOfferedCourseDTO);
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @DeleteMapping
    public ResponseEntity<String> cancelOfferedCourse(@RequestParam String offeredCourseId){
        boolean isDeactivated = offeredCourseService.cancelOfferedCourse(offeredCourseId);

        if(isDeactivated) return ResponseEntity.ok("OfferedCourse no more available!");
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("OfferedCourse could not be cancelled.");
    }
}
