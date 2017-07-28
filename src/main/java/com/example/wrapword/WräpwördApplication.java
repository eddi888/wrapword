package com.example.wrapword;

import org.apache.commons.cli.*;
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
import java.util.Collections;
import java.util.List;


@SpringBootApplication
public class WräpwördApplication implements CommandLineRunner {

	public static final String LINE_SEPERATOR = System.getProperty("line.separator");
	private static final Logger logger = LoggerFactory.getLogger(WräpwördApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(WräpwördApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Options options = new Options();
		options.addRequiredOption("l", "length", true, "max row lenght");
		options.addRequiredOption("f","file", true, "file path location");

		CommandLineParser parser = new DefaultParser();

		try {
			CommandLine cmd = parser.parse(options, args);
			String output = this.brecheDateiinhaltUm(cmd.getOptionValue("l"), cmd.getOptionValue("f"));
			System.out.println(output);
		}catch (MissingOptionException e){
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "java -jar wordwrap.jar", options );
		}

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
			result.addAll(brecheTextInZeilenUmWennNötig(zeile, maxZeilenLänger));
		}
		return result;
	}

	List<String> zerlegeZeilen(String dateiInhalt){
		return Arrays.asList(dateiInhalt.split(LINE_SEPERATOR));
	}

	List<String> brecheTextInZeilenUmWennNötig(String text, int maxZeilenLänger){

		if ( isUmbruchNötig(text, maxZeilenLänger) ) {
			return brecheTextInZeilenUm(text, maxZeilenLänger);
		} else {
			return Collections.singletonList(text);
		}

	}

	private List<String> brecheTextInZeilenUm(String text, int maxZeilenLänger) {
		int schnittPosition;
		boolean letztesZeichenEntfernen = true;
		if ( isOptimaleAusgeschöpfteZeileMitLeerzeichenAmEnde(text, maxZeilenLänger) ) {
            schnittPosition = maxZeilenLänger;
        } else if ( isUmbruchBeimLetztenLeerzeichen(text, maxZeilenLänger) ) {
            schnittPosition = text.substring(0, maxZeilenLänger).lastIndexOf(" ");
        } else {
            //mitten im Wort umbrechen, bei zu langem Wort
            schnittPosition = maxZeilenLänger;
			letztesZeichenEntfernen = false;
        }
		return schneideTextUndBrecheRestlichenTextUm(text, maxZeilenLänger, schnittPosition, letztesZeichenEntfernen);
	}

	private List<String> schneideTextUndBrecheRestlichenTextUm(String text, int maxZeilenLänger, int schneidePosition,
							   boolean letztesZeichenEntfernen) {
		List<String> zeilen = new ArrayList<>();

		String fertigeZeile = text.substring(0, schneidePosition);
		String restlicherText ;
		if (letztesZeichenEntfernen) {
			restlicherText = text.substring(schneidePosition + 1);
		} else {
			restlicherText = text.substring(schneidePosition);
		}
		zeilen.add(fertigeZeile);
		zeilen.addAll(brecheTextInZeilenUmWennNötig(restlicherText, maxZeilenLänger));
		return zeilen;
	}

	private boolean isUmbruchNötig(String text, int maxZeilenLänger){
		return text.length()>maxZeilenLänger;
	}

	private boolean isOptimaleAusgeschöpfteZeileMitLeerzeichenAmEnde(String text, int maxZeilenLänger){
		return text.substring(0, maxZeilenLänger+1).lastIndexOf(" ")==maxZeilenLänger;
	}

	private boolean isUmbruchBeimLetztenLeerzeichen(String text, int maxZeilenLänger){
		return text.substring(0, maxZeilenLänger).lastIndexOf(" ")>-1;
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