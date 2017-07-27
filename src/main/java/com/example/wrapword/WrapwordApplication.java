package com.example.wrapword;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@SpringBootApplication
public class WrapwordApplication implements CommandLineRunner {

	public static final String LINE_SEPERATOR = System.getProperty("line.separator");
	private static final Logger logger = LoggerFactory.getLogger(WrapwordApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(WrapwordApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		String maxZeilenLänge = args[0];
		String dateiPfad = args[1];
		String output = this.brecheDateiinhaltUm(maxZeilenLänge, dateiPfad);
		System.out.println(output);
	}

	String brecheDateiinhaltUm(String maxZeilenLänge, String dateiPfad) throws IOException {
		String dateiInhalt = dateiEinlesen(dateiPfad);
		int maxZeilenLängeInt = konvertiereNachInt(maxZeilenLänge);
		List<String> umgebrocheneZeilen = brecheTextUm(dateiInhalt, maxZeilenLängeInt);
		return formatiereAusgabe(umgebrocheneZeilen);
	}

	String dateiEinlesen(String dateiPfad) throws IOException {
		return FileUtils.readFileToString(new File(dateiPfad), "UTF-8");
	}

	int konvertiereNachInt(String maxZeilenLänger){
		return Integer.valueOf(maxZeilenLänger);
	}

	List<String> brecheTextUm(String dateiInhalt, int maxZeilenLänger){
		List<String> result = new ArrayList<>();
		List<String> zeilen = zerlegeZeilen(dateiInhalt);
		for (String zeile: zeilen) {
			result.addAll(brecheTextInZeilenUm(zeile, maxZeilenLänger));
		}
		return result;
	}

	List<String> zerlegeZeilen(String dateiInhalt){
		return Arrays.asList(dateiInhalt.split(LINE_SEPERATOR));
	}

	List<String> brecheTextInZeilenUm(String text, int maxZeilenLänger){
		List<String> zeilen = new ArrayList<>();

		if(text.length()<=maxZeilenLänger){
			//kein Umbruch nötig
			zeilen.add(text);
		} else if (text.substring(0, maxZeilenLänger+1).lastIndexOf(" ")==maxZeilenLänger){
			//optimale ausgeschöpfte Zeile mit leerzeichen am Ende
			String fertigeZeile = text.substring(0, maxZeilenLänger);
			String restlicherText = text.substring(maxZeilenLänger+1);
			zeilen.add(fertigeZeile);
			zeilen.addAll(brecheTextInZeilenUm(restlicherText, maxZeilenLänger));

		} else if (text.substring(0, maxZeilenLänger).lastIndexOf(" ")>-1){
			//umbruch beim letztem Leerzeichen
			int schneidePosition = text.substring(0, maxZeilenLänger).lastIndexOf(" ")+1;

			String fertigeZeile = text.substring(0, schneidePosition-1);
			String restlicherText = text.substring(schneidePosition);

			zeilen.add(fertigeZeile);
			zeilen.addAll(brecheTextInZeilenUm(restlicherText, maxZeilenLänger));
		} else{
			//mitten im Wort umbrechen, bei zu langem Wort
			String fertigeZeile = text.substring(0, maxZeilenLänger);
			String restlicherText = text.substring(maxZeilenLänger);

			zeilen.add(fertigeZeile);
			zeilen.addAll(brecheTextInZeilenUm(restlicherText, maxZeilenLänger));
		}
		
		return zeilen;
	}

	List<String> entfernerneLeerZeilen(List<String>  umgebrocheneZeilen){
		for(int i=umgebrocheneZeilen.size()-1; i>=0; i--){
			if(umgebrocheneZeilen.get(i).isEmpty()){
				umgebrocheneZeilen.remove(i);
			}
		}
		return umgebrocheneZeilen;
	}

	String formatiereAusgabe(List<String>  umgebrocheneZeilen){
		StringBuffer ausgabe = new StringBuffer();
		for (int i=0; i<umgebrocheneZeilen.size(); i++) {
			if(i>0){
				ausgabe.append(LINE_SEPERATOR);
			}
			ausgabe.append(umgebrocheneZeilen.get(i));
		}
		return ausgabe.toString();
	}


}