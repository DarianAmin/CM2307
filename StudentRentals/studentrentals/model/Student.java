package studentrentals.model;

// Represents a student user in the system
public class Student extends User {

    // University the student is enrolled at
    private final String university;

    // University-issued student identifier
    private final String studentId;

    // Creates a new student with required details
    public Student(String id, String name, String email,
                   String passwordHash, String university, String studentId) {

        // Initialise shared user fields
        super(id, name, email, passwordHash);

        // Validate student-specific fields
        if (university == null || university.isBlank())
            throw new IllegalArgumentException("university required");
        if (studentId == null || studentId.isBlank())
            throw new IllegalArgumentException("studentId required");

        this.university = university;
        this.studentId = studentId;
    }

    // Returns the role for access control
    @Override
    public Role getRole() {
        return Role.STUDENT;
    }

    // Returns the student's university
    public String getUniversity() { return university; }

    // Returns the student's ID
    public String getStudentId() { return studentId; }
}
