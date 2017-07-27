package com.example.wrapword;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.wrapword.WrapwordApplication.LINE_SEPERATOR;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WrapwordApplicationTests {

	@Autowired
	WrapwordApplication wrapword;

	@Test
	public void contextLoads() { }

	@Test
	public void testZerlegeZeilen() {
		List<String> result = wrapword.zerlegeZeilen("zeile1" + LINE_SEPERATOR + "zeile2" + LINE_SEPERATOR + "zeile3");
		Assert.assertEquals(3,result.size());
	}

	@Test
	public void testKonvertiereNachInt() {
		int result = wrapword.konvertiereNachInt("123");
		Assert.assertEquals(123,result);
	}

	@Test
	public void testDateiEinlesen() throws IOException {
		FileUtils.writeStringToFile(new File("test.txt"),"text123ÖÖÖ", "UTF-8");

		String result = wrapword.dateiEinlesen("test.txt");
		Assert.assertEquals("text123ÖÖÖ", result);
	}

	@Test
	public void testEntfernerneLeerZeilen(){
		List<String> testZeilen = new ArrayList<>();
		testZeilen.add("OK");
		testZeilen.add("");
		testZeilen.add("OK");
		testZeilen.add("");

		List<String> result = wrapword.entfernerneLeerZeilen(testZeilen);
		Assert.assertEquals(2, testZeilen.size());
	}

	@Test
	public void testFormatiereAusgabe() {
		List<String> testZeilen = new ArrayList<>();
		testZeilen.add("Zeile1");
		testZeilen.add("Zeile2");

		String result = wrapword.formatiereAusgabe(testZeilen);

		Assert.assertEquals("Zeile1"+LINE_SEPERATOR+"Zeile2", result);
	}

	@Test
	public void testBrecheZeileUm() {
		List<String> einfacheZeile = wrapword.brecheTextInZeilenUm("Bla", 4);
		Assert.assertEquals(1, einfacheZeile.size());
		Assert.assertEquals("Bla", einfacheZeile.get(0));

		List<String> doppelteZeile = wrapword.brecheTextInZeilenUm("Bl aCmb", 4);
		Assert.assertEquals(2, doppelteZeile.size());
		Assert.assertEquals("Bl", doppelteZeile.get(0));
		Assert.assertEquals("aCmb", doppelteZeile.get(1));

		List<String> doppelteZeile2 = wrapword.brecheTextInZeilenUm("Bla Cmb", 4);
		Assert.assertEquals(2, doppelteZeile2.size());
		Assert.assertEquals("Bla", doppelteZeile2.get(0));
		Assert.assertEquals("Cmb", doppelteZeile2.get(1));

		List<String> doppelteZeile3 = wrapword.brecheTextInZeilenUm(" BlaCmb", 4);
		Assert.assertEquals(3, doppelteZeile3.size());
		Assert.assertEquals("", doppelteZeile3.get(0));
		Assert.assertEquals("BlaC", doppelteZeile3.get(1));
		Assert.assertEquals("mb", doppelteZeile3.get(2));

		List<String> gebrochenesWord = wrapword.brecheTextInZeilenUm("BlaCmb", 4);
		Assert.assertEquals(2, gebrochenesWord.size());
		Assert.assertEquals("BlaC", gebrochenesWord.get(0));
		Assert.assertEquals("mb", gebrochenesWord.get(1));

		List<String> volleZeile = wrapword.brecheTextInZeilenUm("A BC", 4);
		Assert.assertEquals(1, volleZeile.size());
		Assert.assertEquals("A BC", volleZeile.get(0));

		List<String> zweiVolleZeilen = wrapword.brecheTextInZeilenUm("A BC DE F", 4);
		Assert.assertEquals(2, zweiVolleZeilen.size());
		Assert.assertEquals("A BC", zweiVolleZeilen.get(0));
		Assert.assertEquals("DE F", zweiVolleZeilen.get(1));

		List<String> leereZeilen = wrapword.brecheTextInZeilenUm("", 4);
		Assert.assertEquals(1, leereZeilen.size());
		Assert.assertEquals("", leereZeilen.get(0));

		List<String> eineLeerzeichenZeilen = wrapword.brecheTextInZeilenUm(" ", 4);
		Assert.assertEquals(1, eineLeerzeichenZeilen.size());
		Assert.assertEquals(" ", eineLeerzeichenZeilen.get(0));

		List<String> zweiLeerzeichenZeilen = wrapword.brecheTextInZeilenUm("A  B", 4);
		Assert.assertEquals(1, zweiLeerzeichenZeilen.size());
		Assert.assertEquals("A  B", zweiLeerzeichenZeilen.get(0));

		List<String> neunLeerzeichenZeilen = wrapword.brecheTextInZeilenUm("         ", 4);
		Assert.assertEquals(2, neunLeerzeichenZeilen.size());
		Assert.assertEquals("    ", neunLeerzeichenZeilen.get(0));
		Assert.assertEquals("    ", neunLeerzeichenZeilen.get(1));

	}


	@Test
	public void testBerecheDateiinhaltUm() throws IOException {
		String input = FileUtils.readFileToString(new File("src/test/resources/test-text.txt"), "UTF-8");
		String exeptedOutput = FileUtils.readFileToString(new File("src/test/resources/test-text-40.txt"), "UTF-8");

		String result = wrapword.brecheDateiinhaltUm("40", "src/test/resources/test-text.txt");

		Assert.assertEquals(exeptedOutput, result);
	}


}
