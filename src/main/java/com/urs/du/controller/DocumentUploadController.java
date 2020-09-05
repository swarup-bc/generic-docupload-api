package com.urs.du.controller;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.urs.du.service.FileUploadService;

import io.swagger.annotations.ApiOperation;

@Controller
public class DocumentUploadController {
	
	private static Logger logger = LogManager.getLogger(DocumentUploadController.class);
	
	@Autowired
	private FileUploadService fileUploadService;
	
	@ApiOperation(value = "accepts documents")
	@RequestMapping(method = RequestMethod.POST, value = "/document-upload", produces = "application/json")
	public ResponseEntity<String> uploadFile(@RequestHeader("client_key") String client_key,
			@RequestHeader("client_secret") String client_secret,
			@RequestParam("file") MultipartFile document, @RequestParam(value="clientId",required=true) String clientId) throws IOException {
		logger.info("File upload API started for file:: "+document.getOriginalFilename()+", For Client "+clientId);
	    ResponseEntity<String> responseEntity = null;
	    if(!client_key.equalsIgnoreCase("379aa805-f75b-4c22-96b0-4f079cd7d383") || !client_secret.equalsIgnoreCase("fceba168-c84c-4dec-ae30-fb49586ed782")) {
	    	logger.error("Client key/secret provided is not matching.");
			return new ResponseEntity<String>("Client key/secret provided is not matching.", HttpStatus.UNAUTHORIZED);
	    }
	    ExecutorService  executorService = Executors.newSingleThreadExecutor();
	    try {
	    	String docUploadRequestId = getUuidBatchId();
	    	byte[] ba = document.getBytes();
	    	executorService.execute( () -> {
	    		logger.info("Start document upload for docUploadRequestId="+docUploadRequestId);
	    		fileUploadService.saveDocument(ba,document.getOriginalFilename(), clientId,docUploadRequestId);    	
	    		logger.info("End document upload for docUploadRequestId="+docUploadRequestId);
	    	});
	    	logger.info("Imidiate response for file - "+document.getOriginalFilename()+" and docUploadRequestId - "+docUploadRequestId);
	    	responseEntity = new ResponseEntity<String>("File Upload request received for File-"+document.getOriginalFilename()+". docUploadRequestId - "+docUploadRequestId, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Error in file upload process", e);
			responseEntity = new ResponseEntity<String>("Exception in uploading file", HttpStatus.INTERNAL_SERVER_ERROR);
		}finally {
			executorService.shutdown();
		}
	    return responseEntity;
	}
	
	public static String getUuidBatchId() {
	    UUID uuid = UUID.randomUUID();
	    return uuid.toString();
	}
}
