package com.talnova.tesp.surveyservice.repository;

import com.talnova.tesp.surveyservice.domain.model.QuestionLibraryDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for QuestionLibraryDocument entities.
 */
@Repository
public interface QuestionLibraryRepository extends MongoRepository<QuestionLibraryDocument, String> {

    Optional<QuestionLibraryDocument> findByLibraryIdAndIsDeletedFalse(String libraryId);

    List<QuestionLibraryDocument> findByCategoryIgnoreCaseAndIsDeletedFalse(String category);

    List<QuestionLibraryDocument> findByIsDeletedFalse();

    boolean existsByLibraryIdAndIsDeletedFalse(String libraryId);
}
