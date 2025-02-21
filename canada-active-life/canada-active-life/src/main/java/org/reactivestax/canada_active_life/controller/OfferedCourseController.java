package org.reactivestax.canada_active_life.controller;

import org.reactivestax.canada_active_life.dto.OfferedCourseDTO;
import org.reactivestax.canada_active_life.service.OfferedCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/CanadaActiveLife/v1/offeredCourses")
public class OfferedCourseController {

    @Autowired
    private OfferedCourseService offeredCourseService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
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
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<OfferedCourseDTO> updateOfferedCourse(@RequestBody OfferedCourseDTO offeredCourseDTO, @RequestParam String offeredCourseId){
        OfferedCourseDTO updatedOfferedCourseDTO = offeredCourseService.updateOfferedCourseInfo(offeredCourseDTO, Integer.parseInt(offeredCourseId));

        if(updatedOfferedCourseDTO != null) return ResponseEntity.ok(updatedOfferedCourseDTO);
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> cancelOfferedCourse(@RequestParam String offeredCourseId){
        boolean isDeactivated = offeredCourseService.cancelOfferedCourse(Integer.parseInt(offeredCourseId));

        if(isDeactivated) return ResponseEntity.ok("OfferedCourse no more available!");
        else return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("OfferedCourse could not be cancelled.");
    }
}
