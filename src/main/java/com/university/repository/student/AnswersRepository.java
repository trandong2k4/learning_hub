package com.university.repository.student;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import com.university.entity.Answers;
public interface AnswersRepository extends JpaRepository<Answers, UUID> {
    
}
