package zw.co.link.FinPay.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/schedules")
@CrossOrigin
public class SchedulesController {

    @GetMapping
    public ResponseEntity<?> getAll(Pageable pageable){
        return ResponseEntity.ok(Page.empty());
    }
}
