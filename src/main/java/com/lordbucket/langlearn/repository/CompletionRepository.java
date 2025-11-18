package com.lordbucket.langlearn.repository;

import com.lordbucket.langlearn.model.Course;
import com.lordbucket.langlearn.model.Lesson;
import com.lordbucket.langlearn.model.User;
import com.lordbucket.langlearn.model.UserLessonCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CompletionRepository
        extends JpaRepository<UserLessonCompletion, Long> {

    /**
     * Use this to determine if user have completed the lesson.
     */
    boolean existsByUserAndLesson(User user, Lesson lesson);

    /**
     * Get last lesson user have completed.
     */
    Optional<UserLessonCompletion> findTopByUserOrderByCompletedAtDesc(User user);

    /**
     * Use to check if user is enrolled in a course.
     * */
    @Query("SELECT CASE WHEN COUNT(ulc) > 0 THEN true ELSE false END " +
            "FROM UserLessonCompletion ulc " +
            "JOIN ulc.lesson l " +
            "JOIN l.section s " +
            "WHERE ulc.user = :user AND s.course = :course")
    boolean hasUserCompletedLessonInCourse(@Param("user") User user, @Param("course") Course course);

    /**
     * Get list of courses user is enrolled in.
     * */
    @Query("SELECT DISTINCT s.course FROM UserLessonCompletion ulc " +
            "JOIN ulc.lesson l " +
            "JOIN l.section s " +
            "WHERE ulc.user = :user")
    List<Course> findEnrolledCoursesByUser(@Param("user") User user);

    /**
     * Get count fo lessons user have completed among the list.
     */
    @Query("SELECT COUNT(ulc) FROM UserLessonCompletion ulc " +
            "WHERE ulc.user = :user AND ulc.lesson IN :lessons")
    int countCompletedLessonsForUser(@Param("user") User user, @Param("lessons") List<Lesson> lessons);

    List<UserLessonCompletion> getAllByUser(User user);
}
