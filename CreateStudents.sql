CREATE TABLE StudentGrades (
                               id INTEGER PRIMARY KEY AUTO_INCREMENT,
                               student_id INTEGER,
                               first_name TEXT,
                               last_name TEXT,
                               course_id INTEGER,
                               course_name TEXT,
                               grade FLOAT,
                               semester TEXT
);


INSERT INTO `StudentGrades` (`id`, `student_id`, `first_name`, `last_name`, `course_id`, `course_name`, `grade`, `semester`) VALUES
(1, 100, 'Andrew', 'Smalls', 301, 'Computer Science', 40.5, 'Semester 1'),
(2, 101, 'Jane', 'Doe', 302, 'Nursing', 70.2, 'Semester 1'),
(3, 102, 'John', 'Doe', 303, 'Engineering', 60.32, 'Semester 1'),
(4, 103, 'Andrea', 'Kelleher', 302, 'Nursing', 90.32, 'Semester 1'),
(5, 104, 'Anthony', 'Hopkins', 301, 'Computer Science', 70.44, 'Semester 1'),
(6, 105, 'Jonathan', 'Reeves', 304, 'Science', 66.33, 'Semester 1'),
(7, 106, 'Will', 'Smith', 303, 'Engineering', 22, 'Semester 1'),
(8, 107, 'Andrew', 'Porter', 303, 'Engineering', 50, 'Semester 1'),
(9, 108, 'Kate', 'Middleton', 301, 'Computer Science', 98.1, 'Semester 1'),
(10, 109, 'Shane', 'Grennan', 303, 'Engineering', 37.87, 'Semester 1');
