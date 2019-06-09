package com.rahmed.redhat.demo;

import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@CsvRecord(separator = ",")
public class BerberZaakGegevens {
	@DataField(pos = 1)
	private String id;

	@DataField(pos = 2)
	private String zaakIdentificatie;

	@DataField(pos = 3)
	private String betrokkeneBarnummer;

	@DataField(pos = 4)
	private String betrokkeneNaam;

	@DataField(pos = 5)
	private String regio;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getZaakIdentificatie() {
		return zaakIdentificatie;
	}

	public void setZaakIdentificatie(String zaakIdentificatie) {
		this.zaakIdentificatie = zaakIdentificatie;
	}

	public String getBetrokkeneBarnummer() {
		return betrokkeneBarnummer;
	}

	public void setBetrokkeneBarnummer(String betrokkeneBarnummer) {
		this.betrokkeneBarnummer = betrokkeneBarnummer;
	}

	public String getBetrokkeneNaam() {
		return betrokkeneNaam;
	}

	public void setBetrokkeneNaam(String betrokkeneNaam) {
		this.betrokkeneNaam = betrokkeneNaam;
	}

	public String getRegio() {
		return regio;
	}

	public void setRegio(String regio) {
		this.regio = regio;
	}

	@Override
	public String toString() {
		return "BerberZaakGegevens [id=" + id + ", zaakIdentificatie=" + zaakIdentificatie + ", betrokkeneBarnummer="
				+ betrokkeneBarnummer + ", betrokkeneNaam=" + betrokkeneNaam + ", regio=" + regio + "]";
	}
}