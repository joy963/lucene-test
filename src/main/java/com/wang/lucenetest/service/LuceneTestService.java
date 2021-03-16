package com.wang.lucenetest.service;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;

/**
 * @since JDK 11
 */
public interface LuceneTestService {
	Document read(String field) throws IOException, ParseException;

	Document read(Long docId) throws ParseException, IOException;

	int count() throws IOException, ParseException;

	void add(Document document) throws IOException;

	void addIndex(String index);

	void delete(Long docId);

	void deleteByQuery(String query);

	void update(Long docId, Document document);
}
