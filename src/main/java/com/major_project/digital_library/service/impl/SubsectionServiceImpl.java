package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Subsection;
import com.major_project.digital_library.model.response_model.SubsectionResponseModel;
import com.major_project.digital_library.repository.ISubsectionRepository;
import com.major_project.digital_library.service.ISubsectionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubsectionServiceImpl implements ISubsectionService {
    private final ISubsectionRepository subsectionRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public SubsectionServiceImpl(ISubsectionRepository subsectionRepository, ModelMapper modelMapper) {
        this.subsectionRepository = subsectionRepository;
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

    private SubsectionResponseModel convertToSubsectionResponseModel(Subsection subsection) {
        SubsectionResponseModel subsectionResponseModel = modelMapper.map(subsection, SubsectionResponseModel.class);

//        int totalPosts = subsection.getPosts().size();
//        int totalReplies = (int) subsection.getPosts()
//                .stream()
//                .flatMap(post -> post.getReplies().stream())
//                .count();
//        Post latestPost = subsection.getPosts()
//                .stream()
//                .max(Comparator.comparing(Post::getCreatedAt)).orElse(null);
//
//        subsectionResponseModel.setTotalPosts(totalPosts);
//        subsectionResponseModel.setTotalReplies(totalReplies);

        return subsectionResponseModel;
    }
}
