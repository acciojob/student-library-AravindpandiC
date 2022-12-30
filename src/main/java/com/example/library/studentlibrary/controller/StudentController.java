package com.example.library.studentlibrary.controller;

import com.example.library.studentlibrary.models.Card;
import com.example.library.studentlibrary.models.Student;
import com.example.library.studentlibrary.services.CardService;
import com.example.library.studentlibrary.services.StudentService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
//Add required annotations
public class StudentController {

    @Autowired
    StudentService studentService;

    @Autowired
    CardService cardService;
    //Add required annotations
    @GetMapping("/student/studentByEmail")
    public ResponseEntity getStudentByEmail(@RequestParam("email") String email){
        Student student = studentService.getDetailsByEmail(email);
        return new ResponseEntity<>(student, HttpStatus.OK);
    }

    //Add required annotations
    @GetMapping("/student/studentById")
    public ResponseEntity getStudentById(@RequestParam("id") int id){
        Student student = studentService.getDetailsById(id);
        return new ResponseEntity<>(student, HttpStatus.OK);
    }

    //Add required annotations
    @PostMapping("/student")
    public ResponseEntity createStudent(@RequestBody Student student){

        Card card = cardService.createAndReturn(student);
        studentService.createStudent(student);
        return new ResponseEntity<>("the student is successfully added to the system", HttpStatus.CREATED);
    }

    //Add required annotations
    @PutMapping("/student")
    public ResponseEntity updateStudent(@RequestBody Student student){
        studentService.updateStudent(student);
        return new ResponseEntity<>("student is updated", HttpStatus.ACCEPTED);
    }

    //Add required annotations
    @DeleteMapping("/student")
    public ResponseEntity deleteStudent(@RequestParam("id") int id){
        studentService.deleteStudent(id);
        return new ResponseEntity<>("student is deleted", HttpStatus.ACCEPTED);
    }

}
