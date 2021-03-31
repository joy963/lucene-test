package com.wang.lucenetest.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.BinaryDocValuesField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleDocValuesField;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.FeatureField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.geo.Point;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.SortedNumericDocValues;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.BytesRef;
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

	private static volatile int count = 1;

	@Override
	public Document read(String field) throws IOException {
		// Query query = new PhraseQuery(2, "key1", "field", "text");
		Query query1 = LongPoint.newRangeQuery("country", new long[] {10, -100}, new long[] {1000, -10});
		// Query query2 = DoublePoint.newRangeQuery("isp", 100D, 1000D);
		// Query query3 = DoublePoint.newRangeQuery("asn", 200, 500);
		Query query = new BooleanQuery.Builder()
			.add(query1, BooleanClause.Occur.MUST)
			// .add(query2, BooleanClause.Occur.MUST)
			// .add(query3, BooleanClause.Occur.MUST)
			.build();
		TopDocs topDocsAll = indexSearcher.search(query, 50);
		TopDocs topDocs = indexSearcher.search(query, 10);
		System.out.println(topDocsAll.totalHits);
		System.out.println(topDocs.totalHits);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		return indexSearcher.doc(scoreDocs[0].doc);
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
	public void add(Document d) throws IOException {
		List<Document> documentList = new ArrayList<>();
		for (int i = count; i < count + 1000000; i++) {
			Document document = new Document();
			Field field4 = new TextField("key1", "has value", Field.Store.YES);
			Field field5 = new DoubleDocValuesField("key2", i);
			Field field6 = new StringField("key3", "456", Field.Store.YES);
			Field field7 = new TextField("key4", "text 4", Field.Store.YES);
			LongPoint country = new LongPoint("country", i, -i);
			DoublePoint isp = new DoublePoint("isp", i - 10.0, -i);
			DoublePoint asn = new DoublePoint("asn", i - 20.0, -i);
			FeatureField featureField;
			SortedNumericDocValues docValues = DocValues.emptySortedNumeric();
			// BytesRef bytesRef = new BytesRef(new byte[1024]);
			// Field field8 = new BinaryDocValuesField("key4", bytesRef);
			document.add(field4);
			document.add(field5);
			document.add(field6);
			document.add(field7);
			document.add(country);
			document.add(isp);
			document.add(asn);
			// document.add(field8);
			documentList.add(document);
		}
		count = count + 1000000;
		System.out.println("count: " + count);
		System.out.println(documentList.size());
		indexWriter.addDocuments(documentList);
	}

	@Override
	public void addIndex(String index) {

	}

	@Override
	public long delete(String field) throws IOException {
		// Term term = new Term("key3", field);
		// long seq = indexWriter.deleteDocuments(term);
		indexWriter.flush();
		indexWriter.commit();
		// return seq;
		return 123;
	}

	@Override
	public void deleteByQuery(String query) {

	}

	@Override
	public void update(Long docId, Document document) {

	}

	@Override
	public void optimize() throws IOException {
		indexWriter.forceMerge(3);
	}
}
