package servlets;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.logging.FileHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.jetty.http.HttpStatus;

import com.google.api.client.http.FileContent;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;


/**
 * Servlet implementation class UploadHandle
 */
public class UploadHandle extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "tokens";
	private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
	private static final String CREDENTIALS_FILE_PATH = "/client_secret.json";
	private static final String APPLICATION_NAME = "Upload files using Drive API";
	//private static final String UPLOAD_DIRECTORY = "D:/home/";
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadHandle() {
        super();
        // TODO Auto-generated constructor stub
    }


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		response.setContentType("text/plain");
	
		response.setStatus(HttpStatus.OK_200);
		// Check that we have a file upload request
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
 
		if (isMultipart) {
			// Create a factory for disk-based file items
			FileItemFactory factory = new DiskFileItemFactory();
			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);
			try {
				List<FileItem> fileItems = upload.parseRequest(request);
                                // loop for multi file
				for (FileItem item : fileItems) {
					if (!item.isFormField()) {
						final NetHttpTransport HTTP_TRANSPORT;
						HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
						Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
					               .setApplicationName(APPLICATION_NAME)
					                .build();
							File fileMetadata = new File();
							fileMetadata.setName(item.getName());
							
							
							java.io.File file = java.io.File.createTempFile(FilenameUtils.removeExtension(item.getName()), "." + FilenameUtils.getExtension(item.getName()));
							
							//write content on temp file
							FileOutputStream outputStream = new FileOutputStream(file.getPath());
							outputStream.write(item.get());
							outputStream.close();
							
							//set type of file upload --> theo định dạng file
							FileContent mediaContent = new FileContent(null, file);
							
							//execute
							File file_upload = service.files().create(fileMetadata, mediaContent)
							    .setFields("id")
							    .execute();
							String link = "https://drive.google.com/file/d/" + file_upload.getId() + "/view";
							response.getWriter().write(link);
							//remove temp file when finish
							file.delete();
					}
				}
 
			} catch (Exception e) {
				System.out.print(e);
			}
		}
		
	}

	
	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws Exception {
        // Load client secrets.
		InputStream in =
				UploadHandle.class.getResourceAsStream(CREDENTIALS_FILE_PATH);  
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

}
