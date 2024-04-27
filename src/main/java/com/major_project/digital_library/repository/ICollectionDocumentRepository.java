package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Collection;
import com.major_project.digital_library.entity.CollectionDocument;
import com.major_project.digital_library.entity.CollectionDocumentKey;
import com.major_project.digital_library.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ICollectionDocumentRepository extends JpaRepository<CollectionDocument, CollectionDocumentKey> {
    boolean existsByCollectionAndDocument(Collection collection, Document document);

    Optional<CollectionDocument> findByCollectionAndDocument(Collection collection, Document document);
}
