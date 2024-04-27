package com.major_project.digital_library.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionDocumentKey implements Serializable {
    private static final long serialVersionUID = 1L;

    private Collection collection;

    private Document document;
}
