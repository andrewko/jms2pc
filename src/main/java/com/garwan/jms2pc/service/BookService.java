package com.garwan.jms2pc.service;

import java.util.List;

import com.garwan.jms2pc.domain.Book;

public interface BookService {
	
	Book createBook(String name);

	List<Book> loadAllBooks();
}
