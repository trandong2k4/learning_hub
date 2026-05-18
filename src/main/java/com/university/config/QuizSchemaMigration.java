package com.university.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuizSchemaMigration implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        addQuizColumns();
        addQuizAttemptColumns();
        addAttemptAnswerColumns();
        addAttemptAnswerLogColumns();
        addQuizAttemptLogColumns();
    }

    private void addQuizColumns() {
        execute("ALTER TABLE quiz ADD COLUMN IF NOT EXISTS quiz_type VARCHAR(40) DEFAULT 'manual_question'");
        execute("ALTER TABLE quiz ADD COLUMN IF NOT EXISTS random_question_count INTEGER");
        execute("ALTER TABLE quiz ADD COLUMN IF NOT EXISTS random_question_types VARCHAR(40)");
        execute("ALTER TABLE quiz ADD COLUMN IF NOT EXISTS shuffle_questions BOOLEAN DEFAULT FALSE");
        execute("ALTER TABLE quiz ADD COLUMN IF NOT EXISTS shuffle_answers BOOLEAN DEFAULT FALSE");
        execute("ALTER TABLE quiz ADD COLUMN IF NOT EXISTS show_result VARCHAR(40) DEFAULT 'immediately'");
        execute("ALTER TABLE quiz ADD COLUMN IF NOT EXISTS pass_score REAL");
    }

    private void addQuizAttemptColumns() {
        execute("ALTER TABLE quiz_attempt ADD COLUMN IF NOT EXISTS attempt_number INTEGER");
        execute("ALTER TABLE quiz_attempt ADD COLUMN IF NOT EXISTS is_passed BOOLEAN");
        execute("ALTER TABLE quiz_attempt ADD COLUMN IF NOT EXISTS ip_address VARCHAR(255)");
        execute("ALTER TABLE quiz_attempt ADD COLUMN IF NOT EXISTS user_agent TEXT");
        execute("ALTER TABLE quiz_attempt ADD COLUMN IF NOT EXISTS used_time INTEGER");
        execute("ALTER TABLE quiz_attempt ADD COLUMN IF NOT EXISTS remaining_time INTEGER");
    }

    private void addAttemptAnswerColumns() {
        execute("ALTER TABLE attempt_answers ALTER COLUMN answers_id DROP NOT NULL");
        execute("ALTER TABLE attempt_answers ADD COLUMN IF NOT EXISTS text_answer TEXT");
        execute("ALTER TABLE attempt_answers ADD COLUMN IF NOT EXISTS is_correct BOOLEAN");
        execute("ALTER TABLE attempt_answers ADD COLUMN IF NOT EXISTS score_received NUMERIC(5,2)");
        execute("ALTER TABLE attempt_answers ADD COLUMN IF NOT EXISTS answered_at TIMESTAMP");
        execute("ALTER TABLE attempt_answers ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP");
    }

    private void addAttemptAnswerLogColumns() {
        execute("CREATE TABLE IF NOT EXISTS attempt_answers_log (id UUID PRIMARY KEY)");
        execute("ALTER TABLE attempt_answers_log ADD COLUMN IF NOT EXISTS quiz_attempt_id UUID NOT NULL");
        execute("ALTER TABLE attempt_answers_log ADD COLUMN IF NOT EXISTS questions_id UUID NOT NULL");
        execute("ALTER TABLE attempt_answers_log ADD COLUMN IF NOT EXISTS old_answers_id UUID");
        execute("ALTER TABLE attempt_answers_log ADD COLUMN IF NOT EXISTS new_answers_id UUID");
        execute("ALTER TABLE attempt_answers_log ADD COLUMN IF NOT EXISTS old_text_answer TEXT");
        execute("ALTER TABLE attempt_answers_log ADD COLUMN IF NOT EXISTS new_text_answer TEXT");
        execute("ALTER TABLE attempt_answers_log ADD COLUMN IF NOT EXISTS time_on_question INTEGER");
        execute("ALTER TABLE attempt_answers_log ADD COLUMN IF NOT EXISTS changed_at TIMESTAMP");
    }

    private void addQuizAttemptLogColumns() {
        execute("CREATE TABLE IF NOT EXISTS quiz_attempt_log (id UUID PRIMARY KEY)");
        execute("ALTER TABLE quiz_attempt_log ADD COLUMN IF NOT EXISTS action VARCHAR(255)");
        execute("ALTER TABLE quiz_attempt_log ADD COLUMN IF NOT EXISTS value TEXT");
        execute("ALTER TABLE quiz_attempt_log ADD COLUMN IF NOT EXISTS event_data TEXT");
        execute("ALTER TABLE quiz_attempt_log ADD COLUMN IF NOT EXISTS ip_address VARCHAR(255)");
        execute("ALTER TABLE quiz_attempt_log ADD COLUMN IF NOT EXISTS created_at TIMESTAMP");
        execute("ALTER TABLE quiz_attempt_log ADD COLUMN IF NOT EXISTS questions_id UUID");
        execute("ALTER TABLE quiz_attempt_log ALTER COLUMN questions_id DROP NOT NULL");
        execute("ALTER TABLE quiz_attempt_log ADD COLUMN IF NOT EXISTS quiz_attempt_id UUID NOT NULL");
        execute("ALTER TABLE quiz_attempt_log DROP CONSTRAINT IF EXISTS quiz_attempt_log_action_check");
        execute("""
                ALTER TABLE quiz_attempt_log
                ADD CONSTRAINT quiz_attempt_log_action_check
                CHECK (action IN (
                    'START',
                    'RESUME',
                    'PAUSE',
                    'TAB_SWITCH',
                    'SUBMIT',
                    'TIMEOUT',
                    'AUTO_SAVE',
                    'NAVIGATE_TO',
                    'SELECT_OPTION',
                    'CLEAR_ANSWER',
                    'FLAG_QUESTION'
                ))
                """);
    }

    private void execute(String sql) {
        jdbcTemplate.execute(sql);
    }
}
