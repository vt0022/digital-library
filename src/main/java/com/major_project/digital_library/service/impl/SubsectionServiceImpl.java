package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Section;
import com.major_project.digital_library.entity.Subsection;
import com.major_project.digital_library.model.request_model.SubsectionRequestModel;
import com.major_project.digital_library.model.response_model.SubsectionResponseModel;
import com.major_project.digital_library.repository.ISectionRepository;
import com.major_project.digital_library.repository.ISubsectionRepository;
import com.major_project.digital_library.service.ISubsectionService;
import com.major_project.digital_library.util.SlugGenerator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SubsectionServiceImpl implements ISubsectionService {
    private final ISubsectionRepository subsectionRepository;
    private final ISectionRepository sectionRepository;
    private final SlugGenerator slugGenerator;
    private final ModelMapper modelMapper;

    @Autowired
    public SubsectionServiceImpl(ISubsectionRepository subsectionRepository, ISectionRepository sectionRepository, SlugGenerator slugGenerator, ModelMapper modelMapper) {
        this.subsectionRepository = subsectionRepository;
        this.sectionRepository = sectionRepository;
        this.slugGenerator = slugGenerator;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<SubsectionResponseModel> findEditableSubsections() {
        List<Subsection> subsections = subsectionRepository.findEditableSubsections();
        List<SubsectionResponseModel> subsectionResponseModels = subsections
                .stream()
                .map(this::convertToSubsectionResponseModel)
                .collect(Collectors.toList());

        return subsectionResponseModels;
    }

    @Override
    public Page<SubsectionResponseModel> findAllSubsections(String disabled, String editable, String s, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        Boolean isDisabled = disabled.equals("all") ?
                null : Boolean.valueOf(disabled);
        Boolean isEditable = editable.equals("all") ?
                null : Boolean.valueOf(editable);

        Page<Subsection> subsections = subsectionRepository.searchSubsections(isDisabled, isEditable, s, pageable);
        Page<SubsectionResponseModel> subsectionResponseModels = subsections.map(this::convertToSubsectionResponseModel);

        return subsectionResponseModels;
    }

    @Override
    public SubsectionResponseModel findSubsection(UUID subId) {
        Subsection subsection = subsectionRepository.findById(subId).orElseThrow(() -> new RuntimeException("Subsection not found"));

        SubsectionResponseModel subsectionResponseModels = convertToSubsectionResponseModel(subsection);

        return subsectionResponseModels;
    }

    @Override
    public SubsectionResponseModel createSubsection(SubsectionRequestModel subsectionRequestModel) {
        Optional<Subsection> subsectionOptional = subsectionRepository.findBySubName(subsectionRequestModel.getSubName());
        if (subsectionOptional.isPresent())
            throw new RuntimeException("Organization already exists");
        Section section = sectionRepository.findById(subsectionRequestModel.getSectionId()).orElseThrow(() -> new RuntimeException("Section not found"));

        Subsection subsection = modelMapper.map(subsectionRequestModel, Subsection.class);
        subsection.setSlug(slugGenerator.generateSlug(subsection.getSubName(), false));
        subsection.setSection(section);
        subsection = subsectionRepository.save(subsection);

        SubsectionResponseModel subsectionResponseModel = modelMapper.map(subsection, SubsectionResponseModel.class);

        return subsectionResponseModel;
    }

    @Override
    public SubsectionResponseModel updateSubsection(UUID subId,
                                                    SubsectionRequestModel subsectionRequestModel) {
        Subsection subsection = subsectionRepository.findById(subId).orElseThrow(() -> new RuntimeException("Subsection not found"));
        Optional<Subsection> subsectionOptional = subsectionRepository.findBySubName(subsectionRequestModel.getSubName());
        if (subsectionOptional.isPresent())
            if (subsectionOptional.get().getSubId() != subsection.getSubId())
                throw new RuntimeException("Subsection already exists");
        Section section = sectionRepository.findById(subsectionRequestModel.getSectionId()).orElseThrow(() -> new RuntimeException("Section not found"));

        subsection.setSubName(subsectionRequestModel.getSubName());
        subsection.setSlug(slugGenerator.generateSlug(subsection.getSubName(), false));
        subsection.setEditable(subsectionRequestModel.isEditable());
        subsection.setPostAcceptable(subsectionRequestModel.isPostAcceptable());
        subsection.setReplyAcceptable(subsectionRequestModel.isReplyAcceptable());
        subsection.setSection(section);
        subsection = subsectionRepository.save(subsection);

        SubsectionResponseModel subsectionResponseModel = modelMapper.map(subsection, SubsectionResponseModel.class);
        return subsectionResponseModel;
    }

    @Override
    public boolean deleteSubsection(UUID subId) {
        Subsection subsection = subsectionRepository.findById(subId).orElseThrow(() -> new RuntimeException("Subsection not found"));

        if (subsection.getPosts().isEmpty()) {
            subsectionRepository.deleteById(subId);
            return true;
        } else {
            subsection.setDisabled(true);
            subsectionRepository.save(subsection);
            return false;
        }
    }

    @Override
    public SubsectionResponseModel activateSubsection(UUID subId) {
        Subsection subsection = subsectionRepository.findById(subId).orElseThrow(() -> new RuntimeException("Subsection not found"));

        subsection.setDisabled(false);
        subsection = subsectionRepository.save(subsection);

        SubsectionResponseModel subsectionResponseModel = modelMapper.map(subsection, SubsectionResponseModel.class);
        return subsectionResponseModel;
    }

    private SubsectionResponseModel convertToSubsectionResponseModel(Subsection subsection) {
        SubsectionResponseModel subsectionResponseModel = modelMapper.map(subsection, SubsectionResponseModel.class);
        subsectionResponseModel.setTotalPosts(subsection.getPosts().size());

        return subsectionResponseModel;
    }
}
