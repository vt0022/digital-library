package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Label;
import com.major_project.digital_library.model.request_model.LabelRequestModel;
import com.major_project.digital_library.model.response_model.LabelResponseModel;
import com.major_project.digital_library.repository.ILabelRepository;
import com.major_project.digital_library.service.ILabelService;
import com.major_project.digital_library.util.SlugGenerator;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LabelServiceImpl implements ILabelService {
    private final ILabelRepository labelRepository;
    private final SlugGenerator slugGenerator;
    private final ModelMapper modelMapper;

    @Autowired
    public LabelServiceImpl(ILabelRepository labelRepository, SlugGenerator slugGenerator, ModelMapper modelMapper) {
        this.labelRepository = labelRepository;
        this.slugGenerator = slugGenerator;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<LabelResponseModel> findActiveLabels() {
        Pageable pageable = PageRequest.of(0, 100);
        List<Label> labels = labelRepository.findAllByIsDisabled(false, pageable).getContent();
        List<LabelResponseModel> labelResponseModels = modelMapper.map(labels, new TypeToken<List<LabelResponseModel>>() {
        }.getType());
        return labelResponseModels;

    }

    @Override
    public Page<LabelResponseModel> findAllLabels(String disabled, String s, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        Boolean isDisabled = disabled.equals("all") ?
                null : Boolean.valueOf(disabled);

        Page<Label> labels = labelRepository.searchLabels(isDisabled, s, pageable);
        Page<LabelResponseModel> labelResponseModels = labels.map(this::convertToLabelResponseModel);

        return labelResponseModels;
    }

    @Override
    public LabelResponseModel findLabel(UUID subId) {
        Label label = labelRepository.findById(subId).orElseThrow(() -> new RuntimeException("Label not found"));

        LabelResponseModel labelResponseModels = modelMapper.map(label, LabelResponseModel.class);

        return labelResponseModels;
    }

    @Override
    public LabelResponseModel createLabel(LabelRequestModel labelRequestModel) {
        Optional<Label> labelOptional = labelRepository.findByLabelName(labelRequestModel.getLabelName());
        if (labelOptional.isPresent())
            throw new RuntimeException("Organization already exists");

        Label label = modelMapper.map(labelRequestModel, Label.class);
        label.setSlug(slugGenerator.generateSlug(labelRequestModel.getLabelName(), false));
        label = labelRepository.save(label);

        LabelResponseModel labelResponseModel = modelMapper.map(label, LabelResponseModel.class);

        return labelResponseModel;
    }

    @Override
    public LabelResponseModel updateLabel(UUID subId,
                                          LabelRequestModel labelRequestModel) {
        Label label = labelRepository.findById(subId).orElseThrow(() -> new RuntimeException("Label not found"));
        Optional<Label> labelOptional = labelRepository.findByLabelName(labelRequestModel.getLabelName());
        if (labelOptional.isPresent())
            if (labelOptional.get().getLabelId() != label.getLabelId())
                throw new RuntimeException("Label already exists");

        label.setLabelName(labelRequestModel.getLabelName());
        label.setColor(labelRequestModel.getColor());
        label.setSlug(slugGenerator.generateSlug(labelRequestModel.getLabelName(), false));
        label = labelRepository.save(label);

        LabelResponseModel labelResponseModel = modelMapper.map(label, LabelResponseModel.class);
        return labelResponseModel;
    }

    @Override
    public boolean deleteLabel(UUID subId) {
        Label label = labelRepository.findById(subId).orElseThrow(() -> new RuntimeException("Label not found"));

        if (label.getPosts().isEmpty()) {
            labelRepository.deleteById(subId);
            return true;
        } else {
            label.setDisabled(true);
            labelRepository.save(label);
            return false;
        }
    }

    @Override
    public LabelResponseModel activateLabel(UUID subId) {
        Label label = labelRepository.findById(subId).orElseThrow(() -> new RuntimeException("Label not found"));

        label.setDisabled(false);
        label = labelRepository.save(label);

        LabelResponseModel labelResponseModel = modelMapper.map(label, LabelResponseModel.class);
        return labelResponseModel;
    }

    private LabelResponseModel convertToLabelResponseModel(Label label) {
        LabelResponseModel labelResponseModel = modelMapper.map(label, LabelResponseModel.class);
        labelResponseModel.setTotalPosts(label.getPosts().size());

        return labelResponseModel;
    }
}
