package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Post;
import com.major_project.digital_library.entity.Section;
import com.major_project.digital_library.entity.Subsection;
import com.major_project.digital_library.model.lean_model.PostLeanModel;
import com.major_project.digital_library.model.response_model.SectionResponseModel;
import com.major_project.digital_library.repository.ISectionRepository;
import com.major_project.digital_library.repository.ISubsectionRepository;
import com.major_project.digital_library.service.ISectionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
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
    public List<SectionResponseModel> findActiveSections() {
        List<Section> sections = sectionRepository.findAllByIsDisabled(false);
        List<SectionResponseModel> sectionResponseModels = sections
                .stream()
                .map(this::convertToSectionResponseModel)
                .collect(Collectors.toList());

        return sectionResponseModels;
    }

    private SectionResponseModel convertToSectionResponseModel(Section section) {
        SectionResponseModel sectionResponseModel = modelMapper.map(section, SectionResponseModel.class);

        sectionResponseModel.getSubsections().forEach(subsection -> {
            Subsection orgSubsection = subsectionRepository.findBySlug(subsection.getSlug()).orElseThrow(() -> new RuntimeException("Subsection not found"));
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
        });
        return sectionResponseModel;
    }
}
