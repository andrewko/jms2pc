package com.garwan.jms2pc.service;

public interface TransactionalService {

	void doInTransaction();

	void doInTransactionWithException();

	void doInTransactionWithException2();

	void doWithoutTransactionWithException();

	void doWithoutTransactionWithException2();
}
