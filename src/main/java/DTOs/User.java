package DTOs;
import java.util.Objects;
public class User {
    private int id;

    private int studentId;
    private String firstName;
    private String lastName;

    private int courseId;

    private String courseName;
    private float grade;
    private String semester;

    public User(int userId, int studentId, String firstName, String lastName, int courseId ,String courseName, float grade ,String semester)
    {
        this.id = userId;
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.courseId = courseId;
        this.courseName = courseName;
        this.grade = grade;
        this.semester = semester;
    }

    public User(int id, int studentId, String firstName, float grade)
    {
        this.id = id;
        this.studentId = studentId;
        this.firstName = firstName;
        this.grade = grade;
    }

    public User(int studentId, String firstName, String lastName, int courseId, String courseName, float grade, String semester)
    {
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.courseId = courseId;
        this.courseName = courseName;
        this.grade = grade;
        this.semester = semester;
    }

    public User(String firstName, String lastName, int courseId, String courseName, float grade, String semester) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.courseId = courseId;
        this.courseName = courseName;
        this.grade = grade;
        this.semester = semester;
    }


    public int getId()
    {

        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getStudentId()
    {
        return studentId;
    }

    public void setStudentId(int studentId)
    {
        this.studentId = studentId;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public int getCourseId()
    {
        return courseId;
    }

    public void setCourseId(int courseId)
    {
        this.courseId = courseId;
    }

    public String getCourseName()
    {
        return courseName;
    }

    public void setCourseName(String courseName)
    {
        this.courseName = courseName;
    }

    public float getGrade()
    {
        return grade;
    }

    public void setGrade(float grade)
    {
        this.grade = grade;
    }

    public String getSemester()
    {
        return semester;
    }

    public void setSemester(String semester)
    {
        this.semester = semester;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", courseId=" + courseId +
                ", courseName='" + courseName + '\'' +
                ", grade=" + grade +
                ", semester='" + semester + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return studentId == user.studentId &&
                courseId == user.courseId &&
                Float.compare(user.grade, grade) == 0 &&
                Objects.equals(firstName, user.firstName) &&
                Objects.equals(lastName, user.lastName) &&
                Objects.equals(courseName, user.courseName) &&
                Objects.equals(semester, user.semester);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, firstName, lastName, courseId, courseName, grade, semester);
    }
}
