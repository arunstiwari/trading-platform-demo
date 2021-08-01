package com.tekmentor.stockservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
public class StockController {

    @GetMapping("/stocks")
    @PreAuthorize("hasAuthority('SCOPE_stocks/read')")
    public ResponseEntity getStocks(Principal user){
        Object details = SecurityContextHolder.getContext().getAuthentication();
        log.info("--details---{}",details);

        log.info("user : {}",user.getName());

        return ResponseEntity.ok("Stocks read");
    }

    @PostMapping ("/stocks")
    @PreAuthorize("hasAuthority('SCOPE_stocks/write')")
    public ResponseEntity addNewStock(@RequestBody String stock){
        return ResponseEntity.status(HttpStatus.CREATED).body("Stocks created");
    }

    @DeleteMapping("/stocks/{id}")
    @PreAuthorize("hasAuthority('SCOPE_stocks/delete')")
    public ResponseEntity deleteStock(@PathVariable String id){
        return ResponseEntity.status(HttpStatus.OK).body("Stocks deleted");
    }
}
