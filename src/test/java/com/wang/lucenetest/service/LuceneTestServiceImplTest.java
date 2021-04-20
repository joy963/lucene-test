package com.wang.lucenetest.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleDocValuesField;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.DoubleRangeDocValuesField;
import org.apache.lucene.document.FeatureField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableFieldType;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermStates;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.suggest.DocumentValueSourceDictionary;
import org.apache.lucene.search.suggest.analyzing.AnalyzingInfixSuggester;
import org.apache.lucene.search.suggest.document.CompletionAnalyzer;
import org.apache.lucene.search.suggest.document.CompletionQuery;
import org.apache.lucene.search.suggest.document.ContextSuggestField;
import org.apache.lucene.search.suggest.document.FuzzyCompletionQuery;
import org.apache.lucene.search.suggest.document.PrefixCompletionQuery;
import org.apache.lucene.search.suggest.document.SuggestField;
import org.apache.lucene.search.suggest.document.SuggestIndexSearcher;
import org.apache.lucene.search.suggest.document.TopSuggestDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

/**
 * @since JDK 11
 */
class LuceneTestServiceImplTest {
	private static class VectorField extends Field {
		public static final FieldType fieldType = new FieldType();

		static {
			fieldType.setStored(true);
			IndexOptions indexOptions = IndexOptions.DOCS_AND_FREQS_AND_POSITIONS;
			fieldType.setIndexOptions(indexOptions);
			fieldType.setStoreTermVectors(true);
			fieldType.setStoreTermVectorPositions(true);
			fieldType.setStoreTermVectorOffsets(true);
			fieldType.setStoreTermVectorPayloads(true);
			fieldType.setTokenized(true);
			fieldType.freeze();

		}

		public VectorField(String name, CharSequence value) {
			super(name, value, fieldType);
		}
	}

	void query(IndexSearcher indexSearcher, IndexReader indexReader) throws IOException {
		StopWatch stopWatch = new StopWatch();
		/**
		 * term query
		 */
		Term term = new Term("text_field", "store");
		Query termQuery = new TermQuery(term);
		stopWatch.start("term query");
		TopDocs topDocs = indexSearcher.search(termQuery, 10000);
		ScoreDoc[] termResult = topDocs.scoreDocs;
		System.out.println(termResult.length);
		if (termResult.length != 0) {
			System.out.println("term query score:" + termResult[0].score);
		}
		stopWatch.stop();
		/**
		 * future
		 */
		stopWatch.start("future query");
		Query futureQuery = FeatureField.newSaturationQuery("future_field", "future_name");
		topDocs = indexSearcher.search(futureQuery, 10000);
		ScoreDoc[] futureResult = topDocs.scoreDocs;
		System.out.println(futureResult.length);
		stopWatch.stop();

		/**
		 * suggest
		 */
		stopWatch.start("suggest query");
		SuggestIndexSearcher suggestIndexSearcher = new SuggestIndexSearcher(indexReader);
		term = new Term("suggest_field", "suggest");
		CompletionQuery completionQuery = new PrefixCompletionQuery(new StandardAnalyzer(), term);
		TopSuggestDocs topSuggestDocs = suggestIndexSearcher.suggest(completionQuery, 10000, true);
		stopWatch.stop();
		TopSuggestDocs.SuggestScoreDoc[] suggestScoreDocs = topSuggestDocs.scoreLookupDocs();
		System.out.println(suggestScoreDocs.length);
		ScoreDoc[] scoreDocs = topSuggestDocs.scoreDocs;
		System.out.println(scoreDocs.length);

		System.out.println(stopWatch.prettyPrint());
	}

	void add(IndexWriter indexWriter) throws IOException {
		List<Document> documentList = new ArrayList<>();
		for (int i = 0; i < 1000000; i++) {
			Document document = new Document();

			/**
			 * vector field
			 */

			Field field_vector = new VectorField("field_vector", "vector value" + i);
			/**
			 * text type
			 */
			TextField text_field = new TextField("text_field", "text value with store " + Math.random(), Field.Store.YES);
			TextField text_field_no_store = new TextField("text_field_no_store", "text value no store " + Math.random(), Field.Store.NO);
			/**
			 * string type
			 */
			StringField string_field = new StringField("string_field", "string value with store" + Math.random(), Field.Store.YES);
			StringField string_field_no_store = new StringField("string_field", "string value no store" + Math.random(), Field.Store.NO);
			/**
			 * doc values: double
			 */
			DoubleDocValuesField doc_value_field = new DoubleDocValuesField("doc_value_field", Math.random());
			/**
			 * doc range value type
			 */
			double double_value = Math.random();
			DoubleRangeDocValuesField double_range_doc_values_field = new DoubleRangeDocValuesField("double_range_doc_value", new double[] {double_value - 10, double_value - 11},
				new double[] {double_value + 10, double_value + 11});
			/**
			 * point type
			 */
			DoublePoint double_point = new DoublePoint("point_field", Math.random(), Math.random());
			/**
			 * future type
			 */
			FeatureField feature_field = new FeatureField("feature_field", "future_name", 2.0f);
			/**
			 * suggest type
			 */
			SuggestField suggest_field = new SuggestField("suggest_field", "suggest" + Math.random(), 199);

			document.add(field_vector);
			document.add(suggest_field);
			document.add(text_field);
			document.add(text_field_no_store);
			document.add(string_field);
			document.add(string_field_no_store);
			document.add(doc_value_field);
			document.add(double_range_doc_values_field);
			document.add(double_point);
			document.add(feature_field);

			documentList.add(document);
		}
		StopWatch stopWatch = new StopWatch("add documents");
		stopWatch.start("start add documents, size:" + documentList.size());
		indexWriter.addDocuments(documentList);
		stopWatch.stop();
		System.out.println(stopWatch.prettyPrint());
		indexWriter.close();
	}

	void suggest() throws IOException {
		FSDirectory fsDirectory = new NIOFSDirectory(Path.of("E:\\lucene_index\\test.index.sugg"));
		AnalyzingInfixSuggester suggester = new AnalyzingInfixSuggester(fsDirectory, new CompletionAnalyzer(new StandardAnalyzer()));

		Map<String, Long> map = new HashMap<>();
		map.put("aaaa", 123L);
		map.put("bbb", 100L);
		// suggester.build();
		suggester.refresh();
	}

	void delete(IndexWriter indexWriter) throws IOException {
		Term term = new Term("ley", "*text*");
		Query query = new WildcardQuery(term, 16);
		indexWriter.deleteDocuments(query);
	}

	void merge(IndexWriter indexWriter) throws IOException {
		indexWriter.forceMerge(2);
	}

	public static void main(String[] args) throws IOException {
		IndexWriterConfig conf = new IndexWriterConfig(new CompletionAnalyzer(new StandardAnalyzer()));
		conf.setCodec(Codec.getDefault());
		conf.setUseCompoundFile(false);
		conf.setRAMBufferSizeMB(100);
		conf.setMaxBufferedDocs(100);
		Directory fsDirectory = NIOFSDirectory.open(Path.of("E:\\lucene_index\\test.index"));
		IndexWriter indexWriter = new IndexWriter(fsDirectory, conf);
		// IndexReader indexReader = DirectoryReader.open(fsDirectory);
		// IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		LuceneTestServiceImplTest luceneTestServiceImplTest = new LuceneTestServiceImplTest();
		/**
		 * add documents
		 */
		luceneTestServiceImplTest.add(indexWriter);
		/**
		 * query documents
		 */
		// luceneTestServiceImplTest.query(indexSearcher, indexReader);
		/**
		 * delete documents
		 */
		// luceneTestServiceImplTest.delete(indexWriter);
		/**
		 * suggest
		 */
		// luceneTestServiceImplTest.suggest();

		// indexReader.close();
		indexWriter.close();
	}
}