package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.entity.DocumentNote;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.request_model.DocumentNoteRequestModel;
import com.major_project.digital_library.model.response_model.DocumentNoteResponseModel;
import com.major_project.digital_library.repository.IDocumentNoteRepository;
import com.major_project.digital_library.repository.IDocumentRepository;
import com.major_project.digital_library.service.IDocumentNoteService;
import com.major_project.digital_library.service.IUserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DocumentNoteServiceImpl implements IDocumentNoteService {
    private final IDocumentNoteRepository documentNoteRepository;
    private final IDocumentRepository documentRepository;
    private final IUserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public DocumentNoteServiceImpl(IDocumentNoteRepository documentNoteRepository, IDocumentRepository documentRepository, IUserService userService, ModelMapper modelMapper) {
        this.documentNoteRepository = documentNoteRepository;
        this.documentRepository = documentRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Override
    public DocumentNoteResponseModel saveNote(String slug, DocumentNoteRequestModel documentNoteRequestModel) {
        Document document = documentRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Document not found"));
        User user = userService.findLoggedInUser();

        Optional<DocumentNote> documentNoteOptional = documentNoteRepository.findByUserAndDocumentAndPage(user, document, documentNoteRequestModel.getPage());
        if (documentNoteOptional.isPresent()) {
            DocumentNote documentNote = documentNoteOptional.get();
            documentNote.setContent(documentNoteRequestModel.getContent());
            documentNoteRepository.save(documentNote);

            DocumentNoteResponseModel documentNoteResponseModel = modelMapper.map(documentNote, DocumentNoteResponseModel.class);
            return documentNoteResponseModel;
        } else {
            DocumentNote documentNote = new DocumentNote();
            documentNote.setDocument(document);
            documentNote.setUser(user);
            documentNote.setPage(documentNoteRequestModel.getPage());
            documentNote.setContent(documentNoteRequestModel.getContent());
            documentNoteRepository.save(documentNote);

            DocumentNoteResponseModel documentNoteResponseModel = modelMapper.map(documentNote, DocumentNoteResponseModel.class);
            return documentNoteResponseModel;
        }
    }

    @Override
    public void deleteNote(String slug, int page) {
        Document document = documentRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Document not found"));
        User user = userService.findLoggedInUser();

        DocumentNote documentNote = documentNoteRepository.findByUserAndDocumentAndPage(user, document, page).orElseThrow(() -> new RuntimeException("Note not found"));
        documentNoteRepository.delete(documentNote);
    }

    @Override
    public DocumentNoteResponseModel getNote(String slug, int page) {
        Document document = documentRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Document not found"));
        User user = userService.findLoggedInUser();

        Optional<DocumentNote> documentNote = documentNoteRepository.findByUserAndDocumentAndPage(user, document, page);

        if (documentNote.isPresent()) {
            DocumentNoteResponseModel documentNoteResponseModel = modelMapper.map(documentNote, DocumentNoteResponseModel.class);
            return documentNoteResponseModel;
        } else {
            return null;
        }
    }

    @Override
    public Page<DocumentNoteResponseModel> getAllNotesOfDocument(String slug) {
        Document document = documentRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Document not found"));
        User user = userService.findLoggedInUser();

        Pageable pageable = PageRequest.of(0, 1000);
        Page<DocumentNote> documentNotes = documentNoteRepository.findByUserAndDocument(user, document, pageable);

        Page<DocumentNoteResponseModel> documentNoteResponseModels = documentNotes.map(this::convertToDocumentNoteModel);

        return documentNoteResponseModels;
    }

    public DocumentNoteResponseModel convertToDocumentNoteModel(DocumentNote documentNote) {
        DocumentNoteResponseModel documentNoteResponseModel = modelMapper.map(documentNote, DocumentNoteResponseModel.class);

        return documentNoteResponseModel;
    }
}
