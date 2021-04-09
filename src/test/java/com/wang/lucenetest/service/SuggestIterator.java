package com.wang.lucenetest.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.search.suggest.InputIterator;
import org.apache.lucene.util.BytesRef;

/**
 * @since JDK 11
 */
public class SuggestIterator implements InputIterator {
	private Map.Entry<String, Long> entry;
	private Iterator<Map.Entry<String, Long>> iterator;

	public SuggestIterator(Map<String, Long> map) {
		iterator = map.entrySet().iterator();
	}

	@Override
	public long weight() {
		return entry.getValue();
	}

	@Override
	public BytesRef payload() {
		return null;
	}

	@Override
	public boolean hasPayloads() {
		return false;
	}

	@Override
	public Set<BytesRef> contexts() {
		Set<BytesRef> contexts = new HashSet<>();
		contexts.add(new BytesRef("suggest"));
		return contexts;
	}

	@Override
	public boolean hasContexts() {
		return true;
	}

	@Override
	public BytesRef next() throws IOException {
		if (iterator.hasNext()) {
			entry = iterator.next();
			return new BytesRef(entry.getKey().getBytes());
		}
		return null;
	}
}
