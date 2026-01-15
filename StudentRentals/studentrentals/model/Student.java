package studentrentals.model;

public class Student extends User {
    private final String university;
    private final String studentId;

    public Student(String id, String name, String email, String passwordHash, String university, String studentId) {
        super(id, name, email, passwordHash);
        if (university == null || university.isBlank()) throw new IllegalArgumentException("university required");
        if (studentId == null || studentId.isBlank()) throw new IllegalArgumentException("studentId required");
        this.university = university;
        this.studentId = studentId;
    }

    @Override
    public Role getRole() { return Role.STUDENT; }

    public String getUniversity() { return university; }
    public String getStudentId() { return studentId; }
}
