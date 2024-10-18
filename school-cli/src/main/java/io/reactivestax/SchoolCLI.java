import java.util.Scanner;

// Command-line interface for enrolling students
public class SchoolCLI {
    private static final School school = new School();

    public static void main(String[] args) throws Exception{
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the School Management System");

        String menu = """
                add_course <course_name>
                enroll_student <student_name> <student_id> <course_name>
                assign_grade <student_id> <course_name> <grade>
                list_all_courses
                list_all_grades
                list_all_students
                list_students <course_name>
                list_enrolled_courses <student_id>
                get_merit_list
                get_merit_list_for_course <course_name>
                calculate_class_average <course_name>
                calculate_CGPA <student_id>
                """;

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            String[] command = input.split(" ");

            switch (command[0]) {
                case "add_course":
                    school.addCourse(command[1]);
                    break;
                case "enroll_student":
                    // student_name.   -     student_id.      -    course_name
                    school.enrollStudent(Integer.parseInt(command[2]), command[1], command[3]);
                    break;
                case "assign_grade":
                    school.assignGrade(Integer.parseInt(command[2]), command[1], Double.parseDouble(command[3]));
                    break;
                case "list_all_courses":
                    school.listAllCourses();
                    break;
                case "list_all_grades":
                    school.listAllGrades();
                    break;
                case "list_all_students":
                    school.listAllStudents();
                    break;
                case "list_students":
                    school.listStudentsInCourse(command[1]);
                    break;
                case "list_enrolled_courses":
                    school.listEnrolledCourses(Integer.parseInt(command[1]));
                    break;
                case "get_merit_list":
                    school.getMeritList();
                    break;
                case "get_merit_list_for_course":
                    school.getMeritList(command[1]);
                    break;
                case "calculate_class_average":
                    school.calculateCourseAvg(command[1]);
                    break;
                case "menu":
                    System.out.println(menu);
                    break;
                case "exit":
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Unknown command");
            }
        }
    }
}
