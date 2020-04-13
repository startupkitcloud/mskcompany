package com.mangobits.startupkit.company;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mangobits.startupkit.admin.userb.UserB;
import com.mangobits.startupkit.core.configuration.Configuration;
import com.mangobits.startupkit.core.configuration.ConfigurationEnum;
import com.mangobits.startupkit.core.configuration.ConfigurationService;
import com.mangobits.startupkit.core.exception.BusinessException;
import com.mangobits.startupkit.core.photo.GalleryItem;
import com.mangobits.startupkit.core.photo.PhotoUpload;
import com.mangobits.startupkit.core.photo.PhotoUtils;
import com.mangobits.startupkit.core.utils.FileUtil;
import com.mangobits.startupkit.notification.email.EmailService;
import com.mangobits.startupkit.admin.util.AdminBaseRestService;
import com.mangobits.startupkit.admin.util.SecuredAdmin;
import com.mangobits.startupkit.ws.JsonContainer;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Stateless
@Path("/company")
public class CompanyRestService extends AdminBaseRestService {
	
	
	@EJB
	private CompanyService companyService;
	
	
	
	@EJB
	private ConfigurationService configurationService;
	
	
	
	@EJB
	private EmailService emailService;
	

	@SecuredAdmin
	@GET
	@Path("/listAll")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String listAll() throws Exception {
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try {
			
			List<Company> list = companyService.listAll();
			cont.setData(list);
			
		} catch (Exception e) {
			
			if(!(e instanceof BusinessException)){
				e.printStackTrace();
			}
			
			cont.setSuccess(false);
			cont.setDesc(e.getMessage());
			
			emailService.sendEmailError(e);
		}
		
		ObjectMapper mapper = new ObjectMapper();
		resultStr = mapper.writeValueAsString(cont);
		
		return resultStr;
	}
	
	
	@SecuredAdmin
	@GET
	@Path("/listActiveCards")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String listActiveCards() throws Exception {
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try {
			
			List<CompanyCard> list = companyService.listActiveCards();
			cont.setData(list);
			
		} catch (Exception e) {
			
			if(!(e instanceof BusinessException)){
				e.printStackTrace();
			}
			
			cont.setSuccess(false);
			cont.setDesc(e.getMessage());
			
			emailService.sendEmailError(e);
		}
		
		ObjectMapper mapper = new ObjectMapper();
		resultStr = mapper.writeValueAsString(cont);
		
		return resultStr;
	}
	
	
	
	@SecuredAdmin
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Path("/save")
	public String save(Company company)  throws Exception{ 
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try { 
			
			companyService.saveCompany(company);
			cont.setData(company);

		} catch (Exception e) {
			
			if(!(e instanceof BusinessException)){
				e.printStackTrace();
			}
			
			cont.setSuccess(false);
			cont.setDesc(e.getMessage());
			
			emailService.sendEmailError(e);
		}
		
		
		ObjectMapper mapper = new ObjectMapper();
		resultStr = mapper.writeValueAsString(cont);
		
		return resultStr;
	}
	
	
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Path("/saveMobile")
	public String saveMobile(Company company)  throws Exception{ 
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try { 
			
			companyService.saveCompany(company);
			cont.setData(company);
			
		} catch (Exception e) {
			
			if(!(e instanceof BusinessException)){
				e.printStackTrace();
			}
			
			cont.setSuccess(false);
			cont.setDesc(e.getMessage());
			
			emailService.sendEmailError(e);
		}
		
		
		ObjectMapper mapper = new ObjectMapper();
		resultStr = mapper.writeValueAsString(cont);
		
		return resultStr;
	}

	@GET
	@Path("/load/{id}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String load(@PathParam("id") String id) throws Exception {
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try {
			
			if(id == null || id.equals("null")){
				UserB userB = getUserTokenSession();
				id = userB.getInfo().get("idCompany");	
			}
			
			Company company = companyService.retrieve(id);
			cont.setData(company);
			
		} catch (Exception e) {
			
			if(!(e instanceof BusinessException)){
				e.printStackTrace();
			}
			
			cont.setSuccess(false);
			cont.setDesc(e.getMessage());
			
			emailService.sendEmailError(e);
		}
		
		ObjectMapper mapper = new ObjectMapper();
		resultStr = mapper.writeValueAsString(cont);
		
		return resultStr;
	}
	
	
	
	@GET
	@Path("/retrieveByCode/{code}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String retrieveByCode(@PathParam("code") String code) throws Exception {
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try {
			
			Company company = companyService.retrieveByCode(code);
			cont.setData(company);
			
		} catch (Exception e) {
			
			if(!(e instanceof BusinessException)){
				e.printStackTrace();
			}
			
			cont.setSuccess(false);
			cont.setDesc(e.getMessage());
			
			emailService.sendEmailError(e);
		}
		
		ObjectMapper mapper = new ObjectMapper();
		resultStr = mapper.writeValueAsString(cont);
		
		return resultStr;
	}	
	
	
	
	@GET
	@Path("/companyImage/{idCompany}/{imageType}")
	@Produces("image/jpeg")
	public StreamingOutput companyImage(final @PathParam("idCompany") String idCompany, final @PathParam("imageType") String imageType) throws Exception {
		
		return new StreamingOutput() { 
			
			@Override
			public void write(final OutputStream out) throws IOException {
				
				Configuration configuration = null;
				
				try {
					
					configuration = configurationService.loadByCode(ConfigurationEnum.PATH_BASE);
					
					String base = configuration.getValue();
					
					String path = base + "/company/" + idCompany + "/" + imageType + "_main.jpg";

					File file = new File(path);
					if (!file.exists()) {
						path = configuration.getValue() + "/company/default/placeholder_" + imageType + ".jpg";

						File placeholder = new File(path);
						if (!placeholder.exists()) {
							path = configuration.getValue() + "/company/default/placeholder.jpg";
						}
					}


					ByteArrayInputStream in =  new ByteArrayInputStream(FileUtil.readFile(path));
							
					byte[] buf = new byte[16384]; 
					
					int len = in.read(buf);
					
					while(len!=-1) { 
						
						out.write(buf,0,len); 
					
						len = in.read(buf); 
					} 
				
				} catch (Exception e) {
					e.printStackTrace();
				}	
			}
		};
	}
	
	
	@GET
	@Path("/companyImage/{idCompany}")
	@Produces("image/jpeg")
	public StreamingOutput companyImage(final @PathParam("idCompany") String idCompany) throws Exception {
		
		return new StreamingOutput() { 
			
			@Override
			public void write(final OutputStream out) throws IOException {
				
				Configuration configuration = null;
				
				try {
					
					Company company = companyService.retrieve(idCompany);
					
					if(company.getGallery() != null && company.getGallery().size() > 0){
						
						configuration = configurationService.loadByCode(ConfigurationEnum.PATH_BASE);
						
						String base = configuration.getValue();
						
						String path = base + "/company/" + idCompany + "/" + company.getGallery().get(0).getId() + "_main.jpg";

						File file = new File(path);
						if (!file.exists()) {
							path = base + "/company/default/placeholder.jpg";
						}
						
						ByteArrayInputStream in =  new ByteArrayInputStream(FileUtil.readFile(path));
								
						byte[] buf = new byte[16384]; 
						
						int len = in.read(buf);
						
						while(len!=-1) { 
							
							out.write(buf,0,len); 
						
							len = in.read(buf); 
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}	
			}
		};
	}

	
	
	
	@POST
	@Path("/saveCompanyImage")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Consumes(MediaType.APPLICATION_JSON)
	public String saveCompanyImage(PhotoUpload photoUpload) throws Exception{
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try {
			
			Company company = companyService.retrieve(photoUpload.getIdObject());
			
			if(company == null){
				throw new BusinessException("company with id  '" + photoUpload.getIdObject() + "' not found to attach photo");
			}
			
			//get the final size
            int finalWidth = configurationService.loadByCode("SIZE_DETAIL_MOBILE").getValueAsInt();
            photoUpload.setFinalWidth(finalWidth);

            //soh adiciona na galeria se nao tiver idSubObject
			String idPhoto = getImageId(company, photoUpload);

			String path = companyService.pathFilesCompany(company.getId());
			
			String mostUsedColor = new PhotoUtils().saveImage(photoUpload, path, idPhoto);
			company.setColorImage(mostUsedColor);
			companyService.saveCompany(company);

			cont.setDesc("OK");
			
		} catch (Exception e) {
			
			if(!(e instanceof BusinessException)){
				e.printStackTrace();
			}
			
			cont.setSuccess(false);
			cont.setDesc(e.getMessage());
		}
		
		ObjectMapper mapper = new ObjectMapper();
		resultStr = mapper.writeValueAsString(cont);
		
		return resultStr;
	}



	@POST
	@Path("/uploadCompanyImage")
	@Consumes("multipart/form-data")
	public String uploadCompanyImage(MultipartFormDataInput input) throws Exception {


		try {

			Map<String, List<InputPart>> uploadForm = input.getFormDataMap();

            int count = 0;

            while (true){

				List<InputPart> listFiles =  uploadForm.get("file["+count+"]");
				count++;

				if(listFiles == null) {
					return "{\n\"success\": \"false\"\n\"desc\": \"Missing file[0]\"\n}";
				}
				InputPart inputPartsFile = listFiles.get(0);

				//get the object id
				List<InputPart> inputId =  uploadForm.get("id_object");

				if(inputId == null) {
					return "{\n\"success\": \"false\"\n\"desc\": \"Missing id_object\"\n}";
				}

				InputPart inputPartsId = inputId.get(0);
                String idObject = inputPartsId.getBody(String.class, null);

				Company company = companyService.retrieve(idObject);

				if(company == null){
					return "{\n\"success\": \"false\"\n\"desc\": \"company with id  '\" + idObject + \"' not found to attach photo\"\n}";
				}

				PhotoUpload photoUpload = new PhotoUpload();
				photoUpload.setWidth(400.0);

                // Get file data to save
                InputStream inputStream = inputPartsFile.getBody(InputStream.class, null);
                byte[] bytes = IOUtils.toByteArray(inputStream);
                photoUpload.setPhotoBytes(bytes);


				//get the final size
				int finalWidth = configurationService.loadByCode("SIZE_DETAIL_MOBILE").getValueAsInt();
				photoUpload.setFinalWidth(finalWidth);
				String idPhoto;

				//soh adiciona na galeria se nao tiver idSubObject
				idPhoto = getImageId(company, photoUpload);

				String path = companyService.pathFilesCompany(company.getId());

				String mostUsedColor = new PhotoUtils().saveImage(photoUpload, path, idPhoto);

				company.setColorImage(mostUsedColor);
				companyService.saveCompany(company);

				return "{\"success\": \"true\"}";
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return null;
	}

	// para projetos antigos como o BlocodePedidos
	@POST
	@Path("/uploadStoreImage")
	@Consumes("multipart/form-data")
	public String uploadStoreImage(MultipartFormDataInput input) throws IOException {

		try {

			Map<String, List<InputPart>> uploadForm = input.getFormDataMap();

			System.out.println();

			//get the object id
			InputPart inputPartsId = uploadForm.get("photo_id").get(0);
			String photoId = inputPartsId.getBody(String.class, null);

			//get the config data to crop
			InputPart inputPartsData = uploadForm.get("avatar_data").get(0);
			String json = inputPartsData.getBody(String.class, null);
			ObjectMapper mapper = new ObjectMapper();
			PhotoUpload photoUpload = (PhotoUpload) mapper.readValue(json, PhotoUpload.class);

			// Get file data to save
			InputPart inputPartsFile = uploadForm.get("avatar_file").get(0);
			InputStream inputStream = inputPartsFile.getBody(InputStream.class, null);
			byte[] bytes = IOUtils.toByteArray(inputStream);
			photoUpload.setPhotoBytes(bytes);

			String imageType = uploadForm.get("avatar-type").get(0).getBody(String.class, null);

			String path = pathFilesStore(photoId);

			new PhotoUtils().saveImage(photoUpload, path, imageType);

			return "{\"state\": 200}";

		} catch (Exception e) {

			e.printStackTrace();
		}

		return null;
	}

	// para projetos antigos
	public String pathFilesStore(String idCompany) throws Exception {
		return configurationService.loadByCode(ConfigurationEnum.PATH_BASE).getValue() + "/company/" + idCompany;
	}


	private String getImageId(Company company, PhotoUpload photoUpload) {
		String idPhoto;
		if(photoUpload.getIdSubObject() == null){

			idPhoto = UUID.randomUUID().toString();

			GalleryItem gi = new GalleryItem();
			gi.setId(idPhoto);

			if(company.getGallery() == null){
				company.setGallery(new ArrayList<>());
			}

			company.getGallery().add(gi);
		}
		else{

			idPhoto = photoUpload.getIdSubObject();
		}
		return idPhoto;
	}


	@GET
	@Path("/removePhoto/{idCompany}/{idPhoto}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String removePhoto(@PathParam("idCompany") String idCompany, @PathParam("idPhoto") String idPhoto) throws Exception {
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try {
			
			companyService.removePhoto(idCompany, idPhoto);
			
		} catch (Exception e) {
			
			if(!(e instanceof BusinessException)){
				e.printStackTrace();
			}
			
			cont.setSuccess(false);
			cont.setDesc(e.getMessage());
			
			emailService.sendEmailError(e);
		}
		
		ObjectMapper mapper = new ObjectMapper();
		resultStr = mapper.writeValueAsString(cont);
		
		return resultStr;
	}	
	
	
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Path("/changeStatus")
	public String changeStatus(Company company)  throws Exception{ 
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try { 
			
			companyService.changeStatus(company.getId());
			
		} catch (Exception e) {
			
			if(!(e instanceof BusinessException)){
				e.printStackTrace();
			}
			
			cont.setSuccess(false);
			cont.setDesc(e.getMessage());
			
			emailService.sendEmailError(e);
		}
		
		
		ObjectMapper mapper = new ObjectMapper();
		resultStr = mapper.writeValueAsString(cont);
		
		return resultStr;
	}

	
	
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Path("/search")
	public String search(CompanySearch search)  throws Exception{ 
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try { 
			
			List<CompanyCard> list = null;
			
			list = companyService.search(search);
			
			cont.setData(list);

		} catch (Exception e) {
			
			if(!(e instanceof BusinessException)){
				e.printStackTrace();
			}
			
			cont.setSuccess(false);
			cont.setDesc(e.getMessage());
			
			emailService.sendEmailError(e);
		}
		
		
		ObjectMapper mapper = new ObjectMapper();
		resultStr = mapper.writeValueAsString(cont);
		
		return resultStr;
	}
	

	

	@GET
	@Path("/loadCompany/{id}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String loadCompany(@PathParam("id") String id) throws Exception {
		
		String resultStr;
		JsonContainer cont = new JsonContainer();
		
		try {
			
			Company company = companyService.retrieve(id);
			cont.setData(company);
			
		} catch (Exception e) {
			
			if(!(e instanceof BusinessException)){
				e.printStackTrace();
			}
			
			cont.setSuccess(false);
			cont.setDesc(e.getMessage());
			
			emailService.sendEmailError(e);
		}
		
		ObjectMapper mapper = new ObjectMapper();
		resultStr = mapper.writeValueAsString(cont);
		
		return resultStr;
	}

	@GET
	@Path("/listByIdParent/{idParent}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String listByIdParent(@PathParam("idParent") String idParent) throws Exception {

		String resultStr = null;
		JsonContainer cont = new JsonContainer();

		try {

			List<CompanyCard> list = companyService.listByIdParent(idParent);
			cont.setData(list);

		} catch (Exception e) {

			if(!(e instanceof BusinessException)){
				e.printStackTrace();
			}

			cont.setSuccess(false);
			cont.setDesc(e.getMessage());

			emailService.sendEmailError(e);
		}

		ObjectMapper mapper = new ObjectMapper();
		resultStr = mapper.writeValueAsString(cont);

		return resultStr;
	}

	@POST
	@SecuredAdmin
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Path("/searchAdmin")
	public String searchAdmin(CompanySearch search)  throws Exception{

		String resultStr = null;
		JsonContainer cont = new JsonContainer();

		try {

			CompanyResultSearch resultSearch = null;

			resultSearch = companyService.searchAdmin(search);

			cont.setData(resultSearch);

		} catch (Exception e) {

			if(!(e instanceof BusinessException)){
				e.printStackTrace();
			}

			cont.setSuccess(false);
			cont.setDesc(e.getMessage());

			emailService.sendEmailError(e);
		}


		ObjectMapper mapper = new ObjectMapper();
		resultStr = mapper.writeValueAsString(cont);

		return resultStr;
	}
}