package net.nilsghesquiere.exceptionhandlers;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.nilsghesquiere.util.error.RESTAuthenticationException;
import net.nilsghesquiere.util.error.ServerInternalErrorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
	
public class RestErrorHandler implements ResponseErrorHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(RestErrorHandler.class);
	public static final List<HttpStatus> acceptedResponses = 
		    Collections.unmodifiableList(Arrays.asList(HttpStatus.OK ,HttpStatus.CREATED));
	
    @Override
    public void handleError(ClientHttpResponse clienthttpresponse) throws IOException {
    	LOGGER.debug("Entered handleError");
    	
    	  if (clienthttpresponse.getStatusCode() == HttpStatus.FORBIDDEN) {
          	LOGGER.debug(HttpStatus.FORBIDDEN + " response. Throwing authentication exception");
            throw new RESTAuthenticationException(HttpStatus.FORBIDDEN + " response");
          }
    	  
    	  if (clienthttpresponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
          	LOGGER.debug(HttpStatus.BAD_REQUEST + " response. Throwing authentication exception");
          	throw new RESTAuthenticationException(HttpStatus.BAD_REQUEST + " response");
          }
    	  
    	  if (clienthttpresponse.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
            	LOGGER.debug(HttpStatus.INTERNAL_SERVER_ERROR + " response. Throwing server internal error exception");
            	throw new ServerInternalErrorException("Internal server error occured, please send your logs to Alco");
            }
    }

    @Override
    public boolean hasError(ClientHttpResponse clienthttpresponse) throws IOException {
        if (!acceptedResponses.contains(clienthttpresponse.getStatusCode())) {
        	LOGGER.debug("Unhandled HTTP response");
        	LOGGER.debug("Status code: " + clienthttpresponse.getStatusCode());
        	LOGGER.debug("Response" + clienthttpresponse.getStatusText());
        	LOGGER.debug(clienthttpresponse.getBody().toString());

            if (clienthttpresponse.getStatusCode() == HttpStatus.FORBIDDEN) {
            	LOGGER.debug("Call returned a error 403 forbidden response.");
                return true;
            }
            if (clienthttpresponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
            	LOGGER.debug("Call returned a error 400 bad request.");
                return true;
            }
        }
        return false;
    }
}