package com.garwan.jms2pc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.garwan.jms2pc.jms.BookStoreNotifier;

@Service
public class TransactionServiceImpl implements TransactionalService {

	@Autowired
	private BookService bookService;

	@Autowired
	private BookStoreNotifier bookStoreNotifier;

	@Override
	@Transactional
	public void doInTransaction() {
		bookService.createBook("book 1");
		bookStoreNotifier.sendMessage("a new book has been published");
	}

	@Override
	@Transactional
	public void doInTransactionWithException() {
		bookStoreNotifier.sendMessage("a new book will be published");
		bookService.createBook("book with a long name");
	}

	@Override
	@Transactional
	public void doInTransactionWithException2() {
		bookService.createBook("book 2");
		bookStoreNotifier.sendMessageWithException("a new book has been published");
	}

	@Override
	public void doWithoutTransactionWithException() {
		bookStoreNotifier.sendMessage("a new book will be published");
		bookService.createBook("book with a long name");
	}

	@Override
	public void doWithoutTransactionWithException2() {
		bookService.createBook("book 2");
		bookStoreNotifier.sendMessageWithException("a new book has been published");
	}
}
