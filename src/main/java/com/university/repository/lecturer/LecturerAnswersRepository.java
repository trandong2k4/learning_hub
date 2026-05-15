package com.university.repository.lecturer;

import com.university.entity.Answers;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LecturerAnswersRepository extends JpaRepository<Answers, UUID> {
    List<Answers> findByQuestions_Id(UUID questionsId);
    void deleteByQuestions_Id(UUID questionsId);
}
