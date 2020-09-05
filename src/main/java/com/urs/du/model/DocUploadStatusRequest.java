package com.urs.du.model;

public class DocUploadStatusRequest {

	private String fileName;
	private String uploadStatus;
	private String docUploadRequestId;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getUploadStatus() {
		return uploadStatus;
	}
	public void setUploadStatus(String uploadStatus) {
		this.uploadStatus = uploadStatus;
	}
	public String getDocUploadRequestId() {
		return docUploadRequestId;
	}
	public void setDocUploadRequestId(String docUploadRequestId) {
		this.docUploadRequestId = docUploadRequestId;
	}
	
}
