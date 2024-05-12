package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Post;
import com.major_project.digital_library.entity.Section;
import com.major_project.digital_library.entity.Subsection;
import com.major_project.digital_library.model.lean_model.PostLeanModel;
import com.major_project.digital_library.model.request_model.SectionRequestModel;
import com.major_project.digital_library.model.response_model.DetailSectionResponseModel;
import com.major_project.digital_library.model.response_model.DetailSubsectionResponseModel;
import com.major_project.digital_library.model.response_model.SectionResponseModel;
import com.major_project.digital_library.repository.ISectionRepository;
import com.major_project.digital_library.repository.ISubsectionRepository;
import com.major_project.digital_library.service.ISectionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SectionServiceImpl implements ISectionService {
    private final ISectionRepository sectionRepository;
    private final ISubsectionRepository subsectionRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public SectionServiceImpl(ISectionRepository sectionRepository, ISubsectionRepository subsectionRepository, ModelMapper modelMapper) {
        this.sectionRepository = sectionRepository;
        this.subsectionRepository = subsectionRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<DetailSectionResponseModel> findActiveSections() {
        List<Section> sections = sectionRepository.findAllByIsDisabled(false);
        List<DetailSectionResponseModel> detailSectionResponseModels = sections
                .stream()
                .map(this::convertToDetailSectionResponseModel)
                .collect(Collectors.toList());

        return detailSectionResponseModels;
    }

    @Override
    public Page<SectionResponseModel> findAllSections(String disabled, String s, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        Boolean isDisabled = disabled.equals("all") ?
                null : Boolean.valueOf(disabled);

        Page<Section> sections = sectionRepository.searchSections(isDisabled, s, pageable);
        Page<SectionResponseModel> sectionResponseModels = sections.map(this::convertToSectionResponseModel);

        return sectionResponseModels;
    }

    @Override
    public SectionResponseModel findSection(UUID subId) {
        Section section = sectionRepository.findById(subId).orElseThrow(() -> new RuntimeException("Section not found"));

        SectionResponseModel sectionResponseModels = modelMapper.map(section, SectionResponseModel.class);

        return sectionResponseModels;
    }

    @Override
    public SectionResponseModel createSection(SectionRequestModel sectionRequestModel) {
        Optional<Section> sectionOptional = sectionRepository.findBySectionName(sectionRequestModel.getSectionName());
        if (sectionOptional.isPresent())
            throw new RuntimeException("Organization already exists");

        Section section = modelMapper.map(sectionRequestModel, Section.class);
        section = sectionRepository.save(section);

        SectionResponseModel sectionResponseModel = modelMapper.map(section, SectionResponseModel.class);

        return sectionResponseModel;
    }

    @Override
    public SectionResponseModel updateSection(UUID subId,
                                              SectionRequestModel sectionRequestModel) {
        Section section = sectionRepository.findById(subId).orElseThrow(() -> new RuntimeException("Section not found"));
        Optional<Section> sectionOptional = sectionRepository.findBySectionName(sectionRequestModel.getSectionName());
        if (sectionOptional.isPresent())
            if (sectionOptional.get().getSectionId() != section.getSectionId())
                throw new RuntimeException("Section already exists");

        section.setSectionName(sectionRequestModel.getSectionName());
        section = sectionRepository.save(section);

        SectionResponseModel sectionResponseModel = modelMapper.map(section, SectionResponseModel.class);
        return sectionResponseModel;
    }

    @Override
    public boolean deleteSection(UUID subId) {
        Section section = sectionRepository.findById(subId).orElseThrow(() -> new RuntimeException("Section not found"));

        if (section.getSubsections().isEmpty()) {
            sectionRepository.deleteById(subId);
            return true;
        } else {
            section.setDisabled(true);
            sectionRepository.save(section);
            return false;
        }
    }

    @Override
    public SectionResponseModel activateSection(UUID subId) {
        Section section = sectionRepository.findById(subId).orElseThrow(() -> new RuntimeException("Section not found"));

        section.setDisabled(false);
        section = sectionRepository.save(section);

        SectionResponseModel sectionResponseModel = modelMapper.map(section, SectionResponseModel.class);
        return sectionResponseModel;
    }

    private DetailSectionResponseModel convertToDetailSectionResponseModel(Section section) {
        DetailSectionResponseModel detailSectionResponseModel = modelMapper.map(section, DetailSectionResponseModel.class);

        List<DetailSubsectionResponseModel> subsectionsToRemove = new ArrayList<>();

        detailSectionResponseModel.getSubsections().forEach(subsection -> {
            Optional<Subsection> subsectionOptional = subsectionRepository.findBySlugAndIsDisabled(subsection.getSlug(), false);

            if (subsectionOptional.isPresent()) {
                Subsection orgSubsection = subsectionOptional.get();
                int totalPosts = orgSubsection.getPosts().size();
                int totalReplies = (int) orgSubsection.getPosts()
                        .stream()
                        .flatMap(post -> post.getReplies().stream())
                        .count();
                Post latestPost = orgSubsection.getPosts()
                        .stream()
                        .max(Comparator.comparing(Post::getCreatedAt)).orElse(null);

                subsection.setLatestPost(
                        latestPost == null
                                ? null : modelMapper.map(latestPost, PostLeanModel.class));
                subsection.setTotalPosts(totalPosts);
                subsection.setTotalReplies(totalReplies);
            } else {
                subsectionsToRemove.add(subsection);
            }
        });
        detailSectionResponseModel.getSubsections().removeAll(subsectionsToRemove);

        return detailSectionResponseModel;
    }

    private SectionResponseModel convertToSectionResponseModel(Section section) {
        SectionResponseModel sectionResponseModel = modelMapper.map(section, SectionResponseModel.class);
        sectionResponseModel.setTotalSubsections(section.getSubsections().size());

        return sectionResponseModel;
    }
}
