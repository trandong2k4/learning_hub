package com.university.repository.student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;
import com.university.entity.Answers;
public interface AnswersRepository extends JpaRepository<Answers, UUID> {
    List<Answers> findByQuestions_Id(UUID questionsId);

    @Query("""
            SELECT a
            FROM Answers a
            JOIN FETCH a.questions
            WHERE a.id IN :ids
            """)
    List<Answers> findAllByIdWithQuestion(@Param("ids") List<UUID> ids);
}
