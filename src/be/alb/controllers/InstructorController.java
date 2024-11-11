package be.alb.controllers;
import java.util.List;

import be.alb.models.*;

public class InstructorController {
	
	public List<Instructor> getAllInstructors() {
        return Instructor.getAllInstructors();
    }
}
