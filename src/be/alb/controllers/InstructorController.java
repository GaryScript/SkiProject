package be.alb.controllers;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import be.alb.models.*;
import be.alb.utils.RegexValidator;

public class InstructorController {
	
	public List<Instructor> getAllInstructors() {
        return Instructor.getAllInstructors();
    }
	
	
}
