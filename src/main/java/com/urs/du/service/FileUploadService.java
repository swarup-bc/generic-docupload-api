package com.urs.du.service;

public interface FileUploadService {

	public void saveDocument(byte[] bs, String fileName, String clientId, String docUploadRequestId);
}
