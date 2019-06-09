package com.rahmed.redhat.demo;

import org.apache.camel.Exchange;

public class ProcessorBean {
	public void processFileName(Exchange exchange) throws Exception {
        String fileLocationExtract = exchange.getIn().getHeader(Exchange.FILE_NAME_ONLY, String.class).replaceAll("^\\w*-", "").replaceAll("\\.(CSV|csv)$", "");
        exchange.getIn().setHeader("BERBER_ZAAK_GEGEVENS_FILE_LOCATION",fileLocationExtract);
    }
}
