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
import org.springframework.web.bind.annotation.PathVariable;
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
		luceneTestService.add(null);
		return "ok";
	}

	@GetMapping("/search/{value}")
	public String search(@PathVariable String value) throws IOException, ParseException {
		return objectMapper.writeValueAsString(luceneTestService.read(value));
	}

	@GetMapping("count")
	public int count() throws IOException, ParseException {
		return luceneTestService.count();
	}

	@GetMapping("/delete/{value}")
	public long delete(@PathVariable String value) throws IOException {
		return luceneTestService.delete(value);
	}

	@GetMapping("/op")
	public void op() throws IOException {
		luceneTestService.optimize();
	}
}
