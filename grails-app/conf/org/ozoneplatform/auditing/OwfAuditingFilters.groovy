package org.ozoneplatform.auditing

import static ozone.owf.enums.OwfApplicationSetting.*
import grails.converters.JSON
import grails.util.Environment

import javax.servlet.http.HttpServletRequest

import org.codehaus.groovy.grails.commons.GrailsApplication
import org.ozoneplatform.appconfig.server.domain.model.ApplicationConfiguration
import org.ozoneplatform.auditing.filter.AbstractAuditingFilters
import org.ozoneplatform.auditing.format.cef.Extension
import org.springframework.web.context.request.RequestContextHolder

import ozone.owf.grails.services.AccountService
import ozone.owf.grails.services.OwfApplicationConfigurationService

class OwfAuditingFilters extends AbstractAuditingFilters {

    GrailsApplication grailsApplication
    
	AccountService accountService

	OwfApplicationConfigurationService owfApplicationConfigurationService
	
	String hostCls
	
    public String getApplicationVersion() {
        return grailsApplication.metadata['app.version']
    }

    @Override
	public boolean doCefLogging() {
		try{
			ApplicationConfiguration doCefLogging = owfApplicationConfigurationService.getApplicationConfiguration(CEF_LOGGING_ENABLED)
			if(doCefLogging)
				return Boolean.valueOf(doCefLogging.value)
		} catch (Exception e){
			return true
		}
		return true
	}

    @Override
    public String getUserName() {
        return accountService.getLoggedInUsername()
    }


    @Override
    public String getHostClassification() {
		if(!hostCls){
			def host = 'http://localhost:8080/jblocks-banner/config/getConfigs'
			try{
				hostCls = JSON.parse(new URL(host)?.text)?.hostCls ?: Extension.UNKOWN_VALUE
			} catch (Exception e){
				hostCls = Extension.UNKOWN_VALUE
			}			
		}		
		hostCls
    }

	@Override
	public HttpServletRequest getRequest()
	{
		return RequestContextHolder?.getRequestAttributes()?.getRequest()
	}
}
