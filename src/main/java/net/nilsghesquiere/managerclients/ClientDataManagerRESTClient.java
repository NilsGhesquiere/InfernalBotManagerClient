package net.nilsghesquiere.managerclients;

import net.nilsghesquiere.util.wrappers.ClientDataMap;
import net.nilsghesquiere.util.wrappers.ClientDataWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.web.client.ResourceAccessException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


public class ClientDataManagerRESTClient implements ClientDataManagerClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientDataManagerRESTClient.class);
	private final String URI_CLIENTDATA;
	private OAuth2RestTemplate restTemplate;
	private HttpHeaders headers;
	
	public ClientDataManagerRESTClient(OAuth2RestTemplate restTemplate) {
		String uriAccesToken = restTemplate.getResource().getAccessTokenUri();
		String uriServer = uriAccesToken.substring(0,uriAccesToken.indexOf("/oauth/token"));
		
		this.URI_CLIENTDATA = uriServer +"/api/clientdata";
		this.restTemplate = restTemplate;
		
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		//Temp including these two properties to ignore content and link in json 
		//TODO: find way to delete content and link
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
		converter.setObjectMapper(mapper);
		restTemplate.getMessageConverters().add(0,converter);
	}
	public boolean sendClientData(Long userid,Long clientid, ClientDataMap map){
		boolean result = true;
		HttpEntity<ClientDataMap> request = new HttpEntity<>(map, headers);
		try{ 
			//Always only 1 clientdata getting sent tbh
			HttpEntity<ClientDataWrapper> response = restTemplate.exchange(URI_CLIENTDATA + "/user/" + userid + "/client/" + clientid,  HttpMethod.POST,request, ClientDataWrapper.class);
			ClientDataWrapper clientDataWrapper = response.getBody();
			if (!clientDataWrapper.getError().equals((""))){
				result = false;
				LOGGER.error("Failure updating Client data on the server: " + clientDataWrapper.getError());
			} 
			return result;
		} catch (ResourceAccessException e){
			LOGGER.debug("Handled exception: " + e.getClass().getSimpleName());
			LOGGER.debug("Client isn't connected to the internet or server is down");
			return false;
		} catch (Exception e){
			LOGGER.debug("Unhandled exception:", e);
			return false;
		}
	}
}
