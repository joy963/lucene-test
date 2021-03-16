package com.wang.lucenetest.controller;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleDocValuesField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongRangeDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wang.lucenetest.service.LuceneTestService;

/**
 * @since JDK 11
 */
@RestController
@RequestMapping("/lucene")
public class LuceneTestController {
	@Autowired
	private LuceneTestService luceneTestService;
	@Autowired
	private ObjectMapper objectMapper;

	@GetMapping("/add")
	public String add() throws IOException {
		Document document = new Document();
		Field field1 = new TextField("key", "value", Field.Store.NO);
		Field field2 = new DoubleDocValuesField("key2", 3.14D);
		Field field3 = new StringField("long type", "123", Field.Store.NO);
		document.add(field1);
		document.add(field2);
		document.add(field3);
		Document document2 = new Document();
		Field field4 = new TextField("key", "has value", Field.Store.NO);
		Field field5 = new DoubleDocValuesField("key2", 6.28D);
		Field field6 = new StringField("long type", "456", Field.Store.NO);
		document2.add(field4);
		document2.add(field5);
		document2.add(field6);
		luceneTestService.add(document2);
		luceneTestService.add(document);
		return "ok";
	}

	@GetMapping("/search")
	public String search() throws IOException, ParseException {
		return objectMapper.writeValueAsString(luceneTestService.read(""));
	}

	@GetMapping("count")
	public int count() throws IOException, ParseException {
		return luceneTestService.count();
	}
}
