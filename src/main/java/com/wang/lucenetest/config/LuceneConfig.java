package com.wang.lucenetest.config;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.store.SimpleFSLockFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @since JDK 11
 */
@Configuration
public class LuceneConfig {
	private static Directory fsDirectory = null;

	static {
		try {
			fsDirectory = FSDirectory.open(Path.of("E:\\lucene_index\\test.index"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Bean
	public IndexWriter indexWriter() throws IOException {
		IndexWriterConfig conf = new IndexWriterConfig(new StandardAnalyzer());
		conf.setCodec(Codec.getDefault());
		conf.setUseCompoundFile(false);
		conf.setRAMBufferSizeMB(100);
		conf.setMaxBufferedDocs(100);
		return new IndexWriter(fsDirectory, conf);
	}

	@Bean
	public IndexSearcher indexSearcher() throws IOException {
		IndexReader indexReader = DirectoryReader.open(fsDirectory);
		return new IndexSearcher(indexReader);
	}
}
