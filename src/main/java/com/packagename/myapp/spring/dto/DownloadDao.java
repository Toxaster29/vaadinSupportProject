package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.avatar.Document;

import java.util.List;

public interface DownloadDao {
    List<Document> getAllDocumentForOperationId(String operationId);
}
