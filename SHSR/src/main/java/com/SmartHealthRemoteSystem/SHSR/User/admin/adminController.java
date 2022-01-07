package com.SmartHealthRemoteSystem.SHSR.User.admin;

import com.SmartHealthRemoteSystem.SHSR.User.Doctor.Doctor;
import com.SmartHealthRemoteSystem.SHSR.User.Doctor.DoctorService;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.Patient;
import com.SmartHealthRemoteSystem.SHSR.User.Patient.PatientService;
import com.SmartHealthRemoteSystem.SHSR.User.User;
import com.SmartHealthRemoteSystem.SHSR.User.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/admin")
public class adminController {

    private final UserService userService;
    private final PatientService patientService;
    private final DoctorService doctorService;

    public adminController(UserService userService, PatientService patientService, DoctorService doctorService) {
        this.userService = userService;
        this.patientService = patientService;
        this.doctorService = doctorService;
    }

    @GetMapping
    public String getAdminDashboard(Model model) throws ExecutionException, InterruptedException {
        List<User> adminList = userService.getAdminList();
        List<Patient> patientList = patientService.getPatientList();
        List<Doctor> doctorList = doctorService.getListDoctor();
        model.addAttribute("adminList", adminList);
        model.addAttribute("patientList", patientList);
        model.addAttribute("doctorList", doctorList);
        return "adminDashboard";
    }

    @PostMapping("/adduser")
    public String saveUserInformation(@RequestParam(value = "userId") String id,
                                      @RequestParam(value = "userFullName") String name,
                                      @RequestParam(value = "userPassword") String password,
                                      @RequestParam(value = "contact") String contact,
                                      @RequestParam(value = "role") String role,
                                      @RequestParam(value = "address") String patientAddress,
                                      @RequestParam(value = "emergencyContact") String emergencyContact,
                                      @RequestParam(value = "hospital") String doctorHospital,
                                      @RequestParam(value = "doctorPosition") String doctorPosition,
                                      @RequestParam(value = "action") String action,
                                      Model model) throws ExecutionException, InterruptedException {
        String errorMessage = "";
        if(action.equals("add")){
            if(role.equals("PATIENT")){
                errorMessage +=  patientService.createPatient(new Patient(id,name,password,contact,role,patientAddress,emergencyContact));
            } else if(role.equals("DOCTOR")){
                errorMessage += doctorService.createDoctor(new Doctor(id,name,password,contact,role, doctorHospital, doctorPosition));
            } else {
                if(userService.createUser(new User(id,name,password,contact,role))== false){
                    errorMessage = "Failed to create user with id " + id +" ,please choose another Id";
                };
            }
        }else{
            if(role.equals("PATIENT")){
                patientService.updatePatient(new Patient(id,name,password,contact,role,patientAddress,emergencyContact));
            } else if(role.equals("DOCTOR")){
                doctorService.updateDoctor(new Doctor(id,name,password,contact,role, doctorHospital, doctorPosition));
            } else {
                userService.updateUser(new User(id,name,password,contact,role));
            }

        }
        List<User> adminList = userService.getAdminList();
        List<Patient> patientList = patientService.getPatientList();
        List<Doctor> doctorList = doctorService.getListDoctor();
        model.addAttribute("errorMsg", errorMessage);
        model.addAttribute("adminList", adminList);
        model.addAttribute("patientList", patientList);
        model.addAttribute("doctorList", doctorList);
        return "adminDashboard";
    }

    @DeleteMapping("/deleteuser")
    public String deleteSelectedUser(@RequestParam("userIdToBeDelete") String userId,
                                     @RequestParam("userRoleToBeDelete") String role,
                                     Model model) throws ExecutionException, InterruptedException {
        if(role.equals("PATIENT")){
            patientService.deletePatient(userId);
        } else if(role.equals("DOCTOR")){
            doctorService.deleteDoctor(userId);
        } else {
            userService.deleteUser(userId);
        }

        List<User> adminList = userService.getAdminList();
        List<Patient> patientList = patientService.getPatientList();
        List<Doctor> doctorList = doctorService.getListDoctor();
        model.addAttribute("adminList", adminList);
        model.addAttribute("patientList", patientList);
        model.addAttribute("doctorList", doctorList);
        return "adminDashboard";
    }
}