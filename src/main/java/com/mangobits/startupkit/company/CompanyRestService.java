package com.mangobits.startupkit.company;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mangobits.startupkit.admin.userb.UserB;
import com.mangobits.startupkit.admin.userb.UserBService;
import com.mangobits.startupkit.admin.util.AdminBaseRestService;
import com.mangobits.startupkit.admin.util.SecuredAdmin;
import com.mangobits.startupkit.core.configuration.Configuration;
import com.mangobits.startupkit.core.configuration.ConfigurationEnum;
import com.mangobits.startupkit.core.configuration.ConfigurationService;
import com.mangobits.startupkit.core.exception.BusinessException;
import com.mangobits.startupkit.core.photo.GalleryItem;
import com.mangobits.startupkit.core.photo.PhotoUpload;
import com.mangobits.startupkit.core.photo.PhotoUtils;
import com.mangobits.startupkit.core.utils.FileUtil;
import com.mangobits.startupkit.notification.email.EmailService;
import com.mangobits.startupkit.user.UserService;
import com.mangobits.startupkit.ws.JsonContainer;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
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
	private UserBService userBService;

	@EJB
	private UserService userService;

	@Context
	private HttpServletRequest requestB;

	@EJB
	private ConfigurationService configurationService;

	@EJB
	private EmailService emailService;
	

	@SecuredAdmin
	@GET
	@Path("/listAll")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public List<Company> listAll() throws Exception {
		return companyService.listAll();
	}
	
	
	@SecuredAdmin
	@GET
	@Path("/listActiveCards")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public List<CompanyCard> listActiveCards() throws Exception {
		return companyService.listActiveCards();
	}
	

	@SecuredAdmin
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Path("/save")
	public Company save(Company company)  throws Exception{
		companyService.saveCompany(company);
		return company;
	}
	

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Path("/saveMobile")
	public Company saveMobile(Company company)  throws Exception{
		companyService.saveCompany(company);
		return company;
	}


	@GET
	@Path("/load/{id}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Company load(@PathParam("id") String id) throws Exception {
		if(id == null || id.equals("null")){
			UserB userB = getUserTokenSession();
			id = userB.getInfo().get("idCompany");
		}
		return companyService.retrieve(id);
	}
	

	@GET
	@Path("/retrieveByCode/{code}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Company retrieveByCode(@PathParam("code") String code) throws Exception {
		return companyService.retrieveByCode(code);
	}	
	

	@GET
	@Path("/companyImage/{idCompany}/{imageType}")
	@Produces("image/jpeg")
	public StreamingOutput companyImage(final @PathParam("idCompany") String idCompany, final @PathParam("imageType") String imageType) throws Exception {
		
		return out -> {

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
		};
	}
	
	
	@GET
	@Path("/companyImage/{idCompany}")
	@Produces("image/jpeg")
	public StreamingOutput companyImage(final @PathParam("idCompany") String idCompany) throws Exception {
		
		return out -> {

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
		};
	}

	
	
	
	@PUT
	@Path("/saveCompanyImage")
	@Consumes(MediaType.APPLICATION_JSON)
	public void saveCompanyImage(PhotoUpload photoUpload) throws Exception{

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
	}


	@POST
	@Path("/uploadCompanyImage")
	@Consumes("multipart/form-data")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
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
	

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Path("/changeStatus")
	public void changeStatus(Company company)  throws Exception{
		companyService.changeStatus(company.getId());
	}


	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Path("/search")
	public List<CompanyCard> search(CompanySearch search)  throws Exception{
		String authorizationHeader = this.requestB.getHeader("Authorization");
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String token = authorizationHeader.substring("Bearer".length()).trim();
			search.setIdParent(this.userService.retrieveByToken(token).getCode());
		}
		return companyService.search(search);
	}
	

	@GET
	@Path("/loadCompany/{id}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Company loadCompany(@PathParam("id") String id) throws Exception {
		return companyService.retrieve(id);
	}


	@GET
	@Path("/listByIdParent/{idParent}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public List<CompanyCard> listByIdParent(@PathParam("idParent") String idParent) throws Exception {
		return companyService.listByIdParent(idParent);
	}


	@POST
	@SecuredAdmin
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Path("/searchAdmin")
	public CompanyResultSearch searchAdmin(CompanySearch search)  throws Exception{
		String authorizationHeader = this.requestB.getHeader("Authorization");
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String token = authorizationHeader.substring("Bearer".length()).trim();
			search.setIdParent(this.userBService.retrieveByToken(token).getIdObj());
		}
		return companyService.searchAdmin(search);
	}
}