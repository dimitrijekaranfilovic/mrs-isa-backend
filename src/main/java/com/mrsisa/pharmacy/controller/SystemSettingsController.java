package com.mrsisa.pharmacy.controller;


import com.mrsisa.pharmacy.domain.valueobjects.SystemSettings;
import com.mrsisa.pharmacy.service.ISystemSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/system-settings")
public class SystemSettingsController {

    private final ISystemSettingsService systemSettingsService;

    @Autowired
    public SystemSettingsController(ISystemSettingsService systemSettingsService) {
        this.systemSettingsService = systemSettingsService;
    }


    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    @PutMapping("/employee-appointment-points")
    public Map<String, Integer> updateEmployeeAppointmentPoints(@RequestBody Map<String, Integer> requestMap){
        if(!requestMap.containsKey("pharmacistPoints"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pharmacist appointment points not specified.");
        if(!requestMap.containsKey("dermatologistPoints"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dermatologist appointment points not specified.");
        SystemSettings systemSettings = this.systemSettingsService.updateSystemSettings(requestMap.get("dermatologistPoints"), requestMap.get("pharmacistPoints"));
        Map<String, Integer> map = new HashMap<>();
        map.put("pharmacistPoints", systemSettings.getPharmacistAppointmentPoints());
        map.put("dermatologistPoints", systemSettings.getDermatologistAppointmentPoints());
        return map;

    }

    @PreAuthorize("hasRole('ROLE_SYSTEM_ADMIN')")
    @GetMapping(value = "/employee-appointment-points")
    public Map<String, Integer> getEmployeeAppointmentPoints(){
        SystemSettings systemSettings = this.systemSettingsService.findById(1L);
        if(systemSettings == null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No setting configuration found.");
        Map<String, Integer> map = new HashMap<>();
        map.put("pharmacistPoints", systemSettings.getPharmacistAppointmentPoints());
        map.put("dermatologistPoints", systemSettings.getDermatologistAppointmentPoints());
        return map;
    }


}
