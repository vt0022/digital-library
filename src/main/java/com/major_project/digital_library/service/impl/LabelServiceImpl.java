package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Label;
import com.major_project.digital_library.model.response_model.LabelResponseModel;
import com.major_project.digital_library.repository.ILabelRepository;
import com.major_project.digital_library.service.ILabelService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelServiceImpl implements ILabelService {
    private final ILabelRepository labelRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public LabelServiceImpl(ILabelRepository labelRepository, ModelMapper modelMapper) {
        this.labelRepository = labelRepository;
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
}
