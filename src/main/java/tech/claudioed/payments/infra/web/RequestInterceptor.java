package tech.claudioed.payments.infra.web;

import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Configuration
public class RequestInterceptor extends HandlerInterceptorAdapter {

	public static final String REQUEST_ID_HEADER_NAME = "x-b3-traceid";
	
	private final Logger logger = LoggerFactory.getLogger(RequestInterceptor.class);
	
	private final ThreadLocal<StopWatch> stopWatch = new ThreadLocal<StopWatch>() {
		protected StopWatch initialValue() {
	        return new StopWatch();
	    }
	};
	
	public static String getRequestId() {
		Object reqId = MDC.get(REQUEST_ID_HEADER_NAME);
		if (reqId != null) {
			return reqId.toString();	
		}
		return null;
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, 
		HttpServletResponse response, Object handler) throws Exception {
		
		stopWatch.get().start();

		String requestId = this.getRequestId(request);
		MDC.put(REQUEST_ID_HEADER_NAME, requestId); 
		
		logger.info("Received request: {}", request.getRequestURI());
		
		response.addHeader(REQUEST_ID_HEADER_NAME, requestId);
		
		return super.preHandle(request, response, handler);
	}

	@Override
	public void postHandle(HttpServletRequest request, 
		HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, 
		HttpServletResponse response, Object handler, Exception ex) throws Exception {
		
		StopWatch sw = stopWatch.get();
		
		super.afterCompletion(request, response, handler, ex);
		
		sw.stop();
		long endTime = sw.getTotalTimeMillis();
		
		if (endTime > 5000) {
			logger.warn("Finished to process request. Time spent is very high: {}ms", endTime);
		} else {
			logger.info("Finished to process request. Time spent: {}ms", endTime);	
		}
		
		stopWatch.set(new StopWatch());
		MDC.clear();
	}
	
	private String getRequestId(HttpServletRequest request) {
		String requestId = request.getHeader(REQUEST_ID_HEADER_NAME);
		if (requestId == null) {
			requestId = UUID.randomUUID().toString();	
			logger.info("RequestId not found in request header. Generated the id {}", requestId);
		}
		
		return requestId;
	}

}