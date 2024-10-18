import javax.xml.crypto.Data;
import javax.xml.transform.Result;
import java.beans.ExceptionListener;
import java.rmi.server.RemoteStub;
import java.sql.*;

public class School {
    // Add course
    public void addCourse(String courseName) throws Exception {
        String query = "INSERT INTO Courses (course_name) VALUES (?)";
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, courseName);
            stmt.executeUpdate();
            System.out.println("Course '" + courseName + "' added.");
        } catch (SQLException e) {
            System.out.println("Error adding course: " + e.getMessage());
        }
    }

    // Enroll student
    // Enroll student and ensure the student exists in the Students table
    public void enrollStudent(int studentId, String studentName, String courseName) throws Exception{
        String courseQuery = "SELECT course_id FROM Courses WHERE course_name = ?";
        String studentQuery = "SELECT student_id FROM Students WHERE student_id = ?";
        String addStudentQuery = "INSERT INTO Students (student_id, student_name) VALUES (?, ?)";
        String enrollQuery = "INSERT INTO Enrollments (course_id, student_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement courseStmt = conn.prepareStatement(courseQuery);
             PreparedStatement studentStmt = conn.prepareStatement(studentQuery);
             PreparedStatement addStudentStmt = conn.prepareStatement(addStudentQuery);
             PreparedStatement enrollStmt = conn.prepareStatement(enrollQuery)) {

            // Step 1: Check if the course exists
            courseStmt.setString(1, courseName);
            ResultSet courseRs = courseStmt.executeQuery();

            if (!courseRs.next()) {
                System.out.println("Error: Course '" + courseName + "' does not exist.");
                return;
            }
            int courseId = courseRs.getInt("course_id");

            // Step 2: Check if the student exists in the Students table
            studentStmt.setInt(1, studentId);
            ResultSet studentRs = studentStmt.executeQuery();

            if (!studentRs.next()) {
                // Step 3: If the student does not exist, add the student
                addStudentStmt.setInt(1, studentId);
                addStudentStmt.setString(2, studentName);
                addStudentStmt.executeUpdate();
                System.out.println("Student '" + studentName + "' (ID: " + studentId + ") added to the Students table.");
            }

            // Step 4: Enroll the student in the course
            enrollStmt.setInt(1, courseId);
            enrollStmt.setInt(2, studentId);
            enrollStmt.executeUpdate();

            System.out.println("Student '" + studentId + "' enrolled in course '" + courseName + "'.");

        } catch (SQLException e) {
            System.out.println("Error enrolling student: " + e.getMessage());
        }
    }

    // Assign grade
    public void assignGrade(int studentId, String courseName, double grade) throws Exception {
        String courseQuery = "SELECT course_id FROM Courses WHERE course_name = ?";
        String gradeQuery = "INSERT INTO Grades (course_id, student_id, grade) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement courseStmt = conn.prepareStatement(courseQuery);
             PreparedStatement gradeStmt = conn.prepareStatement(gradeQuery)) {

            // Fetch course ID
            courseStmt.setString(1, courseName);
            ResultSet rs = courseStmt.executeQuery();
            if (rs.next()) {
                int courseId = rs.getInt("course_id");


                // Assign grade
                gradeStmt.setInt(1, courseId);
                gradeStmt.setInt(2, studentId);
                gradeStmt.setDouble(3, grade);
                gradeStmt.executeUpdate();
                System.out.println("Grade '" + grade + "' assigned to student '" + studentId + "' in course '" + courseName + "'.");
            } else {
                System.out.println("Error: Course '" + courseName + "' does not exist.");
            }
        } catch (SQLException e) {
            System.out.println("Error assigning grade: " + e.getMessage());
        }
    }

    //List All Courses
    public void listAllCourses() throws Exception {
        String query = "select course_name from Courses";

        try(Connection conn = DatabaseConnectionPool.getConnection();
            PreparedStatement psQuery = conn.prepareStatement(query))
        {
            ResultSet queryRs = psQuery.executeQuery();

            if (!queryRs.next()){
                System.out.println("No Courses available in the School Yet.");
                return;
            }

            do{
                System.out.println(queryRs.getString("course_name"));
            }while(queryRs.next());

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    //List all Grades
    public void listAllGrades() throws Exception{
        String query = """
                            Select
                            Courses.course_name,
                            Students.student_name,
                            Grades.grade
                        From Grades
                        join Students on Students.student_id=Grades.student_id
                        join Courses on Courses.course_id=Grades.course_id
                        """;

        try(Connection conn = DatabaseConnectionPool.getConnection();
            PreparedStatement psQuery = conn.prepareStatement(query)){

            ResultSet rsQuery = psQuery.executeQuery();

            if(!rsQuery.next()){
                System.out.println("No Grades Assigned Yet!");
                return;
            }
            do {
                System.out.println("Course: "+ rsQuery.getString("course_name") +
                                " - Student Name: "+ rsQuery.getString("student_name") +
                                " - Grade: " + rsQuery.getDouble("grade"));
            } while (rsQuery.next());

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }

    }

    //List All Students
    public void listAllStudents() throws Exception{
        String query = "Select student_id, student_name from Students";

        try(Connection conn = DatabaseConnectionPool.getConnection();
            PreparedStatement psQuery = conn.prepareStatement(query)){

            ResultSet rsQuery = psQuery.executeQuery();

            if(!rsQuery.next()){
                System.out.println("No Students enrolled in the school yet.");
            }

            do {
                System.out.println("Student ID: "+rsQuery.getInt("student_id")+
                        "; Student name: "+rsQuery.getString("student_name"));
            } while (rsQuery.next());

        }
    }

    //Give the Merit List
    public void getMeritList(String courseName) throws Exception{
        String query = """
                Select student_name
                from Students
                Join Grades on Grades.student_id=Students.student_id
                Join Courses on Courses.course_id=Grades.course_id
                where Courses.course_name = ? AND Grades.grade > 80;
                """;

        try(Connection conn = DatabaseConnectionPool.getConnection();
            PreparedStatement psQuery = conn.prepareStatement(query))  {

            psQuery.setString(1, courseName);
            ResultSet rsQuery = psQuery.executeQuery();

            if(!rsQuery.next()){
                System.out.println("No Students in the Course "+ courseName +" above 80%");
                return;
            }

            do {
                System.out.println(rsQuery.getString("student_name"));
            } while (rsQuery.next());

        }
    }

    public void getMeritList() throws Exception{
        String query = "Select * from Students_Averages where average > 80";

        try(Connection conn = DatabaseConnectionPool.getConnection();
            PreparedStatement psQuery = conn.prepareStatement(query))  {

            ResultSet rsQuery = psQuery.executeQuery();

            if(!rsQuery.next()){
                System.out.println("No Students in the School above 80%");
                return;
            }

            do {
                System.out.println(rsQuery.getString("student_name"));
            } while (rsQuery.next());

        }
    }

    //Get CGPA of Student


    //Calculate Class Average
    public void calculateCourseAvg(String courseName) throws Exception{

        String query = """
                Select avg(grade) as class_average
                from Grades
                Join Courses on Courses.course_id=Grades.course_id
                Where Courses.course_name = ?;
                """;

        try(Connection conn = DatabaseConnectionPool.getConnection();
            PreparedStatement psQuery = conn.prepareStatement(query)){

            psQuery.setString(1, courseName);
            ResultSet rsQuery = psQuery.executeQuery();

            if(!rsQuery.next()){
                System.out.println("No Students Enrolled in the Course.");
                return;
            }

            double classAverage = rsQuery.getDouble("class_average");

            System.out.println(courseName+ " Average: " + classAverage);
        }

    }

    //List Students in a given course
    public void listStudentsInCourse(String courseName) throws Exception{
        String query = """
                select Students.student_id, Students.student_name
                from Enrollments
                Join Students on Students.student_id=Enrollments.student_id
                Join Courses on Courses.course_id=Enrollments.course_id
                Where Courses.course_name = ?;
                """;

        try( Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement psQuery = conn.prepareStatement(query);
                ){

            psQuery.setString(1, courseName);
            ResultSet rsQuery = psQuery.executeQuery();

            if(!rsQuery.next()){
                System.out.println("No student enrolled within the course.");
                return;
            }

            do{
                System.out.println("Student ID: " + rsQuery.getInt("student_id")+
                        " Student Name: "+rsQuery.getString("student_name"));
            }while(rsQuery.next());
        }

    }

    //List the courses a particular student is enrolled in
    public void listEnrolledCourses (int studentID) throws Exception{
        String query = """
                Select Courses.course_name
                from Enrollments
                join Courses on Courses.course_id=Enrollments.course_id
                Join Students on Students.Student_id=Enrollments.student_id
                Where Students.student_id = ?;
                """;

        try(Connection conn = DatabaseConnectionPool.getConnection();
            PreparedStatement psQuery = conn.prepareStatement(query)){

            psQuery.setInt(1, studentID);
            ResultSet rsQuery = psQuery.executeQuery();

            if(!rsQuery.next()){
                System.out.println("Student does not exist.");
                return;
            }

            do{
                System.out.println(rsQuery.getString("course_name"));
            }while(rsQuery.next());

        }

    }

}