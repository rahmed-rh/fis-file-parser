/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rahmed.redhat.demo;

import javax.sql.DataSource;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.processor.idempotent.jdbc.JdbcMessageIdRepository;
import org.apache.camel.spi.IdempotentRepository;
import org.apache.camel.spring.spi.SpringTransactionPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class CamelRouteConfiguration extends RouteBuilder {

	@Autowired
	private ApplicationProperties properties;

	@Autowired
	private ApplicationContext applicationContext;

	@Override
	public void configure() {
		fromF("file:%s?noop=true&moveFailed=.failed&readLock=idempotent&idempotentRepository=#fileMessageIdRepository&idempotentKey=${file:name}-${file:size}", properties.getFile().getMessagesDirectory()).routeId("fileConsumerRoute").tracing()
				.bean(ProcessorBean.class,"processFileName").unmarshal().bindy(BindyType.Csv, BerberZaakGegevens.class).log("Body is ${body}").multicast().parallelProcessing(true).to("direct:dbInsert")
				/*.to("direct:fileWrite")*/.end();

		from("direct:dbInsert").routeId("dbInserterRoute").tracing().transacted("requiredTransactionPolicy").split().body()
				.log("dbInsert ${header.BERBER_ZAAK_GEGEVENS_FILE_LOCATION}, Body = ${body}")
				// I'm using the idempotentKey as the the location_id
				.idempotentConsumer(simple("${header.BERBER_ZAAK_GEGEVENS_FILE_LOCATION}_${body.id}"),
						applicationContext.getBean("dbMessageIdRepository", IdempotentRepository.class))
				.eager(true).log("Inserting into DB...")
				.toF("sql:insert into %s values (:#${body.id},:#${body.zaakIdentificatie},:#${body.betrokkeneBarnummer},:#${body.betrokkeneNaam},:#${body.regio})?dataSource=#dataSource",
						properties.getSql().getMessagesTable())
				.log("Successfully inserted into DB.").end();

		/*from("direct:fileWrite").routeId("fileWriterRoute").tracing()
				.log("fileWrite Body is ${body}")
				.idempotentConsumer(simple("${body['id']}"),
						applicationContext.getBean("fileMessageIdRepository", IdempotentRepository.class))
				.eager(true).log("Writing file...").setHeader(Exchange.FILE_NAME, simple("${body['id']}.txt"))
				.setBody(simple("${body['message']}"))
				.toF("file:%s?fileExist=Fail", properties.getFile().getProcessedDirectory())
				.log("Successfully wrote file.").end();*/
	}

	@Bean
	SpringTransactionPolicy requiredTransactionPolicy(PlatformTransactionManager transactionManager) {
		SpringTransactionPolicy policy = new SpringTransactionPolicy(transactionManager);
		policy.setPropagationBehaviorName("PROPAGATION_REQUIRED");
		return policy;
	}

	@Bean
	JdbcMessageIdRepository dbMessageIdRepository(DataSource dataSource, TransactionTemplate transactionTemplate) {
		return new JdbcMessageIdRepository(dataSource, transactionTemplate,
				properties.getSql().getMessagesTable() + "_RECORD");
	}

	@Bean
	JdbcMessageIdRepository fileMessageIdRepository(DataSource dataSource) {
		return new JdbcMessageIdRepository(dataSource, properties.getSql().getMessagesTable() + "_FILE");
	}
	

}
