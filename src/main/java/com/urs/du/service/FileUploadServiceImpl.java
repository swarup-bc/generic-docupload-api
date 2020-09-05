package com.urs.du.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.OperationContext;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.BlobRequestOptions;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.urs.du.util.ApiGatewayService;

@Service
public class FileUploadServiceImpl implements FileUploadService{
	
	public static final Logger logger=Logger.getLogger(FileUploadServiceImpl.class);
	
	@Value("${storageConnectionString}")
	private String storageConnectionString;
	
	@Value("${uploadStatusReportAPI}")
	private String uploadStatusReportAPI;
	
	@Autowired
	ApiGatewayService apiGatewayService;
	
	public void saveDocument(byte[] bs, String fileName,String clientId, String docUploadRequestId) {
		
		boolean fileUploadStatusFlag = true;
		CloudStorageAccount storageAccount;
		CloudBlobClient blobClient = null;
		CloudBlobContainer container=null;

		try {    
			// Parse the connection string and create a blob client to interact with Blob storage
			storageAccount = CloudStorageAccount.parse(storageConnectionString);
			blobClient = storageAccount.createCloudBlobClient();
			container = blobClient.getContainerReference("document-upload");

			// Create the container if it does not exist with public access.
			logger.info("Creating container: " + container.getName());
			container.createIfNotExists(BlobContainerPublicAccessType.CONTAINER, new BlobRequestOptions(), new OperationContext());		    

			Date now = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String dateFolder = dateFormat.format(now);
			
			//Getting a blob reference
			CloudBlockBlob blob = container.getBlockBlobReference(clientId+"/"+dateFolder+"/"+fileName);

			//Creating blob and uploading file to it
			logger.info("Uploading the file ");
			blob.uploadFromByteArray(bs, 0, bs.length);;

			//Listing contents of container
//			for (ListBlobItem blobItem : container.listBlobs()) {
//				System.out.println("URI of blob is: " + blobItem.getUri());
//			}

		// Download blob. In most cases, you would have to retrieve the reference
		// to cloudBlockBlob here. However, we created that reference earlier, and 
		// haven't changed the blob we're interested in, so we can reuse it. 
		// Here we are creating a new file to download to. Alternatively you can also pass in the path as a string into downloadToFile method: blob.downloadToFile("/path/to/new/file").
			//		downloadedFile = new File(sourceFile.getParentFile(), "downloadedFile.txt");
			//		blob.downloadToFile(downloadedFile.getAbsolutePath());
		} 
		catch (StorageException ex)
		{
			ex.printStackTrace();
			fileUploadStatusFlag = false;
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
			fileUploadStatusFlag = false;
		}
		finally 
		{
			logger.info("Calling fileUploadStatus API For File - "+fileName+", docUploadRequestId - "+docUploadRequestId+", Flag is - "+fileUploadStatusFlag);
			//Call API
			callAPI(fileName, docUploadRequestId, fileUploadStatusFlag);
		}
	}

	private void callAPI(String fileName, String docUploadRequestId, boolean fileUploadStatusFlag){
		try {
			List<NameValuePair> params = new ArrayList<NameValuePair>(3);
			params.add(new BasicNameValuePair("docUploadRequestId", docUploadRequestId));
			params.add(new BasicNameValuePair("uploadStatus", fileUploadStatusFlag?"Success":"Fail"));
			params.add(new BasicNameValuePair("fileName", fileName));
			
			HttpPost post = new HttpPost(uploadStatusReportAPI);
			post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			if(apiGatewayService.callPostApi(post))
				logger.info("Success Calling fileUploadStatus API For File - "+fileName+", docUploadRequestId - "+docUploadRequestId+", Flag is - "+fileUploadStatusFlag);
			else
				logger.info("ERROR Calling fileUploadStatus API For File - "+fileName+", docUploadRequestId - "+docUploadRequestId+", Flag is - "+fileUploadStatusFlag);
		}catch(Exception e) {
			logger.info("ERROR Calling fileUploadStatus API For File - "+fileName+", docUploadRequestId - "+docUploadRequestId+", Flag is - "+fileUploadStatusFlag);
			e.printStackTrace();
		}
	}
	
	
}
