package com.mangobits.startupkit.company;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mangobits.startupkit.admin.user.UserB;
import com.mangobits.startupkit.catalogue.saleoff.SaleOff;
import com.mangobits.startupkit.catalogue.service.Service;
import com.mangobits.startupkit.catalogue.service.ServiceService;
import com.mangobits.startupkit.core.configuration.Configuration;
import com.mangobits.startupkit.core.configuration.ConfigurationEnum;
import com.mangobits.startupkit.core.configuration.ConfigurationService;
import com.mangobits.startupkit.core.exception.BusinessException;
import com.mangobits.startupkit.core.photo.PhotoUpload;
import com.mangobits.startupkit.core.photo.PhotoUtils;
import com.mangobits.startupkit.core.utils.FileUtil;
import com.mangobits.startupkit.notification.email.EmailService;
import com.mangobits.startupkit.service.admin.util.BaseRestService;
import com.mangobits.startupkit.service.admin.util.SecuredAdmin;
import com.mangobits.startupkit.user.UserCard;
import com.mangobits.startupkit.ws.JsonContainer;


@Stateless
@Path("/company")
public class CompanyRestService extends BaseRestService{
	
	
	@EJB
	private CompanyService companyService;
	
	
	
	@EJB
	private ConfigurationService configurationService;
	
	
	
	@EJB
	private EmailService emailService;
	
	
	@EJB
	private ServiceService serviceService;
	
	
		
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
	
	
	
	//@SecuredAdmin//Comentado dia 29/09/2017. O Diegao ficou de implementar nova anotação de segurança
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
    @Path("/uploadCompanyImage")
    @Consumes("multipart/form-data")
    public String uploadCompanyImage(MultipartFormDataInput input) throws IOException {
          
        try {
        	
        	Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
            
            //get the object id
            InputPart inputPartsId = uploadForm.get("photo_id").get(0);
            String photoId = inputPartsId.getBody(String.class, null);
            
            InputPart inputPartsIdItem = uploadForm.get("item_id").get(0);
            String itemId = inputPartsIdItem.getBody(String.class, null);
            
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
            
            //get the final size
            int finalWidth = configurationService.loadByCode("SIZE_DETAIL_MOBILE").getValueAsInt();
            photoUpload.setFinalWidth(finalWidth);
            
            String path = companyService.pathFilesCompany(photoId);
            
            new PhotoUtils().saveImage(photoUpload, path, itemId);
            
            companyService.addPhoto(photoId, itemId);
            
            return "{\"state\": 200}";
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
          
        return null;
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
				throw new BusinessException("Company with id  '" + photoUpload.getIdObject() + "' not found to attach photo");
			}
			
			//get the final size
            int finalWidth = configurationService.loadByCode("SIZE_DETAIL_MOBILE").getValueAsInt();
            photoUpload.setFinalWidth(finalWidth);
            
            GalleryItem gi = new GalleryItem();
            gi.setId(UUID.randomUUID().toString());
            
            if(company.getGallery() == null){
            	company.setGallery(new ArrayList<GalleryItem>());
            }
			
            company.getGallery().add(gi);
            
			String path = companyService.pathFilesCompany(company.getId());
			
			String mostUsedColor = new PhotoUtils().saveImage(photoUpload, path, gi.getId());
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
	
	
	
	
	@SecuredAdmin
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
	
	
	
	
	@SecuredAdmin
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Path("/saveService")
	public String saveService(Service service)  throws Exception{ 
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		UserB userB = getUserTokenSession();;
		
		try { 
			
			if(service.getIdCompany() == null || service.getIdCompany().equals("null")){
				String idCompany = userB.getInfo().get("idCompany");
				service.setIdCompany(idCompany);
			}
			
			serviceService.save(service);
			
			companyService.processService(service);
			
			if(configurationService.loadByCode("PRODUCTION").getValueAsBoolean() &&  userB != null && userB.getRole().getFgAdmin() == null || !userB.getRole().getFgAdmin()){
				
				Company company = companyService.retrieve(service.getIdCompany());
				
				StringBuilder msg = new StringBuilder();
				
				msg.append(userB.getName());
				msg.append(" do salão ");
				msg.append(company.getFantasyName());
				msg.append(" adicionou/modificou o servico ");
				msg.append(service.getName());
				
				emailService.sendEmailMessage("alteracaodados@markei.com.br", "Backoffice", msg.toString());
			}
			
			cont.setData(service);
			
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
	@Path("/changeStatusService")
	public String changeStatusService(Service service)  throws Exception{ 
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try { 
			
			companyService.changeStatusService(service.getId());
			
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
	
	
	
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	@Path("/mainSalesOff")
	public String mainSalesOff(CompanySearch search)  throws Exception{ 
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try { 
			
			List<SaleOff> list = companyService.mainSalesOff(search);
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
	@Path("/listPros/{idService}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public String listPros(@PathParam("idService") String idService) throws Exception {
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try {
			
			
			List<UserCard> listPros = companyService.listPros(idService);
			cont.setData(listPros);
			
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
		
		String resultStr = null;
		JsonContainer cont = new JsonContainer();
		
		try {
			
			CompanyApp companyApp = companyService.load(id);
			cont.setData(companyApp);
			
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