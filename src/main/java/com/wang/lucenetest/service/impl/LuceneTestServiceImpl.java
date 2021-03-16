package com.wang.lucenetest.service.impl;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TotalHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wang.lucenetest.service.LuceneTestService;

/**
 * @since JDK 11
 */
@Service
public class LuceneTestServiceImpl implements LuceneTestService {
	@Autowired
	private IndexWriter indexWriter;
	@Autowired
	private IndexSearcher indexSearcher;

	@Override
	public Document read(String field) throws ParseException, IOException {
		Analyzer analyzer = new StandardAnalyzer();
		QueryParser queryParser = new QueryParser("key", analyzer);
		Query query = queryParser.parse("value");
		ScoreDoc[] hits = indexSearcher.search(query, 10).scoreDocs;
		indexSearcher.doc(0);
		return indexSearcher.doc(1);
	}

	@Override
	public Document read(Long docId) throws ParseException, IOException {
		Analyzer analyzer = new StandardAnalyzer();
		QueryParser queryParser = new QueryParser("key", analyzer);
		Query query = queryParser.parse("value");
		ScoreDoc[] hits = indexSearcher.search(query, 10).scoreDocs;
		return indexSearcher.doc(hits[0].doc);
	}

	@Override
	public int count() throws IOException, ParseException {
		Analyzer analyzer = new StandardAnalyzer();
		QueryParser queryParser = new QueryParser("key", analyzer);
		Query query = queryParser.parse("value");
		return indexSearcher.count(query);
	}

	@Override
	public void add(Document document) throws IOException {
		indexWriter.addDocument(document);
		indexWriter.flush();
		indexWriter.commit();
	}

	@Override
	public void addIndex(String index) {

	}

	@Override
	public void delete(Long docId) {

	}

	@Override
	public void deleteByQuery(String query) {

	}

	@Override
	public void update(Long docId, Document document) {

	}
}
